package com.iso.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.ApplicationConfiguration;

public interface ApplicationConfigurationRepository extends PagingAndSortingRepository<ApplicationConfiguration, String>, ApplicationConfigurationRepositoryCustom{

}
