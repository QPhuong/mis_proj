package com.iso.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;

@Document
public class AssessmentControl extends BaseDomain{

	private static final long serialVersionUID = 1L;
	
	@Id
	private ObjectId id;
	@Field
	private int level;
	@Field
	private int sequence;
	@Field
	private String control;
	@Field
	private String controlName;
	@Field
	private List<String> assessmentPoints;
	@DBRef
	private List<IsoDocument> findings;
	@Field
	private int status;
	@Field
	private String comment;
	@Transient
	private AssessmentControl parent;
	@Field
	private List<AssessmentControl> subAssessmentControls;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getControl() {
		return control;
	}
	public void setControl(String control) {
		this.control = control;
	}
	public String getControlName() {
		return controlName;
	}
	public void setControlName(String controlName) {
		this.controlName = controlName;
	}
	public List<String> getAssessmentPoints() {
		return assessmentPoints;
	}
	public void setAssessmentPoints(List<String> assessmentPoints) {
		this.assessmentPoints = assessmentPoints;
	}
	public List<IsoDocument> getFindings() {
		return findings;
	}
	public void setFindings(List<IsoDocument> findings) {
		this.findings = findings;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public AssessmentControl getParent() {
		return parent;
	}
	public void setParent(AssessmentControl parent) {
		this.parent = parent;
	}
	public List<AssessmentControl> getSubAssessmentControls() {
		return subAssessmentControls;
	}
	public void setSubAssessmentControls(
			List<AssessmentControl> subAssessmentControls) {
		this.subAssessmentControls = subAssessmentControls;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	
}
