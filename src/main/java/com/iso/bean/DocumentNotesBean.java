package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.iso.comparator.DocumentNoteComparator;
import com.iso.constant.DocumentActivityLogs;
import com.iso.domain.DocumentNote;
import com.iso.domain.IsoDocument;
import com.iso.domain.User;
import com.iso.service.IsoDocumentService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="documentNotesBean")
@ViewScoped
public class DocumentNotesBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private IsoDocument selectedDoc;
	private List<DocumentNote> lstNote;
	private DocumentNote selectedNote;
	private boolean addNew;
	
	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	public IsoDocument getSelectedDoc() {
		return selectedDoc;
	}
	public void setSelectedDoc(IsoDocument selectedDoc) {
		this.selectedDoc = selectedDoc;
	}
	public List<DocumentNote> getLstNote() {
		return lstNote;
	}
	public void setLstNote(List<DocumentNote> lstNote) {
		this.lstNote = lstNote;
	}
	public DocumentNote getSelectedNote() {
		return selectedNote;
	}
	public void setSelectedNote(DocumentNote selectedNote) {
		this.selectedNote = selectedNote;
	}
	public boolean isAddNew() {
		return addNew;
	}
	public void setAddNew(boolean addNew) {
		this.addNew = addNew;
	}
	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}
	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}
	
	@PostConstruct
	private void initialize() {
		doLoadDocumentNotes();
	}
	
	public void doLoadDocumentNotes() {
		Object bean = FaceContextUtils.getViewScopeBean("documentBean");
		if (bean != null) {
			DocumentBean docBean = (DocumentBean) bean;
			if (docBean.getSelectedDoc() != null && docBean.getSelectedDoc().getId() != null) {
				this.selectedDoc = isoDocumentService.getIsoDocumentAndSecurityById(docBean.getSelectedDoc().getId().toString(), FaceContextUtils.getLoginUser());
				
				this.lstNote = this.selectedDoc.getDocumentNotes();
				if (this.lstNote == null) {
					this.lstNote = new ArrayList<DocumentNote>();
				}
				
				this.selectedNote = null;
				Collections.sort(this.lstNote, new DocumentNoteComparator());
			}
		}else {
			FaceContextUtils.showErrorMessage("Error when loading document properties!", null);
		}
	}
	
	public void doAddNewNote() {
		this.selectedNote = new DocumentNote();
		this.addNew = true;
	}
	
	public void doDeleteNote() {
		try {
			this.lstNote.remove(this.selectedNote);
			this.selectedDoc.setDocumentNotes(lstNote);
			isoDocumentService.saveDocumentNote(FaceContextUtils.getLoginUser(), this.selectedDoc, DocumentActivityLogs.DELETE_NOTE);
			
			doLoadDocumentNotes();
		} catch(Exception e) {
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doSaveNote() {
		try {
			User loginUser = FaceContextUtils.getLoginUser();
			
			this.selectedNote.setDocVersion(this.selectedDoc.getLatestVersion());

			if (!this.lstNote.contains(this.selectedNote)) {
				
				this.selectedNote.setCreatedBy(loginUser);
				this.selectedNote.setCreatedOn(Calendar.getInstance().getTime());
				
				this.lstNote.add(this.selectedNote);
				this.selectedDoc.setDocumentNotes(lstNote);
				
				isoDocumentService.saveDocumentNote(FaceContextUtils.getLoginUser(), this.selectedDoc, DocumentActivityLogs.INSERT_NEW_NOTE);
				
			} else {
				
				this.selectedNote.setUpdatedBy(loginUser);
				this.selectedNote.setUpdatedOn(Calendar.getInstance().getTime());
				
				isoDocumentService.saveDocumentNote(FaceContextUtils.getLoginUser(), this.selectedDoc, DocumentActivityLogs.MODIFY_NOTE);
			}
			this.selectedNote.setEditing(false);
			
			doLoadDocumentNotes();
		} catch(Exception e) {
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doCancelEditNote() {
		this.selectedNote.setEditing(false);
		this.selectedNote = null;
		
	}
	
}
