package com.iso.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.iso.domain.AssessmentControl;
import com.iso.service.AuditChecklistService;
import com.iso.util.FaceContextUtils;

@ManagedBean
@ViewScoped
public class EvaluationBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<AssessmentControl> assessmentControls;
	
	private boolean temporaryChecklist = false;
	
	private TreeNode root;
	private TreeNode selectedNode;
	
	@ManagedProperty(value="#{auditChecklistService}")
	private AuditChecklistService auditChecklistService;
	
	public List<AssessmentControl> getAssessmentControls() {
		return assessmentControls;
	}
	public void setAssessmentControls(List<AssessmentControl> assessmentControls) {
		this.assessmentControls = assessmentControls;
	}
	public TreeNode getRoot() {
		return root;
	}
	public void setRoot(TreeNode root) {
		this.root = root;
	}
	public TreeNode getSelectedNode() {
		return selectedNode;
	}
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	public AuditChecklistService getAuditChecklistService() {
		return auditChecklistService;
	}
	public void setAuditChecklistService(AuditChecklistService auditChecklistService) {
		this.auditChecklistService = auditChecklistService;
	}
	public boolean isTemporaryChecklist() {
		return temporaryChecklist;
	}
	public void setTemporaryChecklist(boolean temporaryChecklist) {
		this.temporaryChecklist = temporaryChecklist;
	}
	
	@PostConstruct
	private void initialize() {
		try {
			assessmentControls = auditChecklistService.getAllAssessmentControls();
			if (CollectionUtils.isEmpty(assessmentControls)) {
				assessmentControls = auditChecklistService.readDefaultAssessmentPoints();
				temporaryChecklist = true;
			}
			buildTreeRoot();
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	private void buildTreeRoot() {
		root = new DefaultTreeNode(new AssessmentControl(), null);
		if (CollectionUtils.isNotEmpty(this.assessmentControls)) {
			for (AssessmentControl point : this.assessmentControls) {
				buildTreeNode(root, point);
			}
		}
	}
	
	private void buildTreeNode(TreeNode nodeParent, AssessmentControl data) {
		TreeNode node = new DefaultTreeNode(data, nodeParent);
		node.setExpanded(true);
		if (CollectionUtils.isNotEmpty(data.getSubAssessmentControls())) {
			for (AssessmentControl point : data.getSubAssessmentControls()) {
				buildTreeNode(node, point);
			}
		}
	}
	
	public void doSave() {
		try {
			if (CollectionUtils.isNotEmpty(assessmentControls)) {
				for (AssessmentControl control : this.assessmentControls) {
					auditChecklistService.save(control);
				}
				this.temporaryChecklist = false;
			}
			buildTreeRoot();
			FaceContextUtils.showInfoMessage("Save successfully", null);
		} catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doCheckDocumentsForAllControls() {
		try {
			assessmentControls = auditChecklistService.findDocumentAllControls(FaceContextUtils.getLoginUser().getOrganization().getId().toString());
			buildTreeRoot();
			FaceContextUtils.showInfoMessage("Check document successfully", null);
		} catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doAutoEvaluate() {
		try {
			assessmentControls = auditChecklistService.autoEvaluate(this.assessmentControls);
			buildTreeRoot();
			FaceContextUtils.showInfoMessage("Check document successfully", null);
		} catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
}
