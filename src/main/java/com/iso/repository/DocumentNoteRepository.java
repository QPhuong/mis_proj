package com.iso.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.DocumentNote;

public interface DocumentNoteRepository extends PagingAndSortingRepository<DocumentNote, String>, DocumentNoteRepositoryCustom{

}
