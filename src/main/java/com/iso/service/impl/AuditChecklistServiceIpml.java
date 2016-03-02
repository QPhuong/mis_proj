package com.iso.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.iso.domain.AssessmentControl;
import com.iso.domain.Category;
import com.iso.domain.IsoDocument;
import com.iso.repository.AuditChecklistRepository;
import com.iso.repository.CategoryRepository;
import com.iso.repository.IsoDocumentRepository;
import com.iso.service.AuditChecklistService;
import com.iso.util.ConfigurationPropertiesUtils;

@Component(value="auditChecklistService")
public class AuditChecklistServiceIpml implements AuditChecklistService{

	private static final long serialVersionUID = 1L;

	@Autowired
	private AuditChecklistRepository auditChecklistRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private IsoDocumentRepository isoDocumentRepository;
	
	@Override
	public List<AssessmentControl> readDefaultAssessmentPoints() throws IOException {
		
		//Read checklist file
		String filename = ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.AUDIT_CHECKLIST_FILE_EXCEL);
        String folderName = ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.AUDIT_CHECKLIST_FOLDER);
        
		InputStream file = this.getClass().getClassLoader().getResourceAsStream(StringUtils.trim(folderName) + File.separator + StringUtils.trim(filename));
		
		//Get the workbook instance for XLS file 
		XSSFWorkbook workbook = new XSSFWorkbook (file);
		 
		//Get first sheet from the workbook
		XSSFSheet sheet = workbook.getSheet("Compliance Checklist");
		 
		//Get iterator to all the rows in current sheet
		Iterator<Row> rowIterator = sheet.iterator();
		
		List<AssessmentControl> assessmentControls = new ArrayList<AssessmentControl>();
		AssessmentControl assessmentControlBefore = null;
		
		//Get iterator to all cells of current row
		while(rowIterator.hasNext()) {
			Row currentRow = rowIterator.next();
			if (currentRow.getRowNum() >= 2) {
				AssessmentControl currentAssessmentControl = readAssessmentControlValue(currentRow, assessmentControlBefore);
				assessmentControlBefore = currentAssessmentControl;
				if (currentAssessmentControl != null && StringUtils.isNotEmpty(currentAssessmentControl.getControl()) && currentAssessmentControl.getLevel() == 1) {
					currentAssessmentControl.setSequence(assessmentControls.size()+1);
					assessmentControls.add(currentAssessmentControl);
				}
			}
		}
		
		return assessmentControls;
	}
	
	private AssessmentControl readAssessmentControlValue(Row currentRow, AssessmentControl assessmemtControlBefore) {
		
		AssessmentControl assessmentControl = new AssessmentControl();
		
		Iterator<Cell> cellIterator = currentRow.cellIterator();
		
		while(cellIterator.hasNext()) {
			
			Cell currentCell = cellIterator.next();
			
			if (currentCell.getColumnIndex() == 1) { //control code
				String cellValue = currentCell.getRichStringCellValue().getString();
				if (StringUtils.isEmpty(cellValue)) {
					return null;
				}
				assessmentControl.setControl(cellValue);
				assessmentControl.setLevel(readControlLevel(assessmentControl, assessmemtControlBefore));
				
			}else if (currentCell.getColumnIndex() == 2) { //control name
				String cellValue = currentCell.getRichStringCellValue().getString();
				assessmentControl.setControlName(cellValue);
			}else if (currentCell.getColumnIndex() == 3) { //assessment points
				String cellValue = currentCell.getRichStringCellValue().getString();
				if (StringUtils.isNotEmpty(cellValue)) {
					String[] points = cellValue.split("\r?\n|\r");
					assessmentControl.setAssessmentPoints(new ArrayList<String>(Arrays.asList(points)));
				}
			}else if (currentCell.getColumnIndex() == 4) { //findings
				
			}else if (currentCell.getColumnIndex() == 5) { //status
				int cellValue = (int) currentCell.getNumericCellValue();
				assessmentControl.setStatus(cellValue);
			}else {
				continue;
			}
			
		}
		
		return assessmentControl;
	}

	private int readControlLevel(AssessmentControl assessmentControl, AssessmentControl assessmentControlBefore) {
		if (assessmentControlBefore != null) {
			if (assessmentControl.getControl().startsWith(assessmentControlBefore.getControl())) {
				assessmentControl.setParent(assessmentControlBefore);
				if (CollectionUtils.isEmpty(assessmentControlBefore.getSubAssessmentControls())) {
					assessmentControlBefore.setSubAssessmentControls(new ArrayList<AssessmentControl>());
				}
				assessmentControlBefore.getSubAssessmentControls().add(assessmentControl);
				assessmentControl.setSequence(assessmentControlBefore.getSubAssessmentControls().size());
				return assessmentControlBefore.getLevel()+1;
			}else {
				return readControlLevel(assessmentControl, assessmentControlBefore.getParent());
			}
		}
		return 1;
	}

	@Override
	public AssessmentControl save(AssessmentControl control) {
		return auditChecklistRepository.save(control);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AssessmentControl> getAllAssessmentControls() {
		return IteratorUtils.toList(auditChecklistRepository.findAll(new Sort(Sort.Direction.ASC, "sequence")).iterator());
	}

	@Override
	public AssessmentControl getAssessmentControlById(String id) {
		return auditChecklistRepository.findOne(id);
	}

	@Override
	public List<AssessmentControl> findDocumentAllControls(String organizationId) {
		List<AssessmentControl> controlLst = getAllAssessmentControls();
		if (CollectionUtils.isNotEmpty(controlLst)) {
			for (AssessmentControl control : controlLst) {
				control = findDocumentForEachControl(control, organizationId);
			}
		}
		return controlLst;
	}
	
	@Override
	public AssessmentControl findDocumentForEachControl(AssessmentControl control, String organizationId) {
		if (control != null) {
			String controlCode = control.getControl();
			control.setFindings(new ArrayList<IsoDocument>());
			
			//find category
			List<Category> categoryLst = categoryRepository.searchByControlNumber(controlCode, organizationId);
			if (CollectionUtils.isNotEmpty(categoryLst)) {
				
				//find document
				for (Category category: categoryLst) {
					List<IsoDocument> documentLst = isoDocumentRepository.findByCategory(category.getId().toString());
					control.getFindings().addAll(documentLst);
				}
			}
			
			//find document for sub controls
			if (CollectionUtils.isNotEmpty(control.getSubAssessmentControls())) {
				for (AssessmentControl subcontrol : control.getSubAssessmentControls()) {
					subcontrol = findDocumentForEachControl(subcontrol, organizationId);
				}
			}
		}
		return control;
	}

	@Override
	public List<AssessmentControl> autoEvaluate(List<AssessmentControl> controls) {
		if (CollectionUtils.isNotEmpty(controls)) {
			for(AssessmentControl control : controls) {
				int statusNum = evaluateControl(control, null);
				control.setStatus(statusNum);
			}
		}
		return controls;
	}
	
	private int evaluateControl(AssessmentControl control, AssessmentControl parent) {
		
		int statusNum = 0;
		
		if (CollectionUtils.isNotEmpty(control.getAssessmentPoints())) {
			if (CollectionUtils.isNotEmpty(control.getFindings())) {
				statusNum = 100;
			}else {
				if (parent != null && CollectionUtils.isNotEmpty(parent.getFindings())) {
					statusNum = 50;
				}
			}
		}
		
		int subStatusNum = 0;
		if (CollectionUtils.isNotEmpty(control.getSubAssessmentControls())) {
			for (AssessmentControl subControl : control.getSubAssessmentControls()) {
				subStatusNum += evaluateControl(subControl, control);
			}
			subStatusNum = (int) subStatusNum/control.getSubAssessmentControls().size();
		}
	
		if (subStatusNum > 0 && statusNum > 0) {
			statusNum = (int) (statusNum + subStatusNum)/2;
		}else if (subStatusNum > 0 && statusNum == 0) {
			statusNum = subStatusNum;
		}
		control.setStatus(statusNum);
		
		return statusNum;
	}
}
