package com.iso.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.domain.AssessmentControl;

@Service
public interface AuditChecklistService extends Serializable{
	
	public List<AssessmentControl> readDefaultAssessmentPoints() throws IOException;
	public List<AssessmentControl> autoEvaluate(List<AssessmentControl> controls);
	
	public AssessmentControl save(AssessmentControl control);
	public List<AssessmentControl> getAllAssessmentControls();
	public AssessmentControl getAssessmentControlById(String id);
	
	public List<AssessmentControl> findDocumentAllControls(String organizationId);
	public AssessmentControl findDocumentForEachControl(AssessmentControl control, String organizationId);
}
