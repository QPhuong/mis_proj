package com.iso.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.domain.ApplicationConfiguration;

@Service
public interface ApplicationConfigurationService extends Serializable{
	
	public ApplicationConfiguration save(ApplicationConfiguration config);
	
	public ApplicationConfiguration getLatestConfiguration();
	
	public List<ApplicationConfiguration> getAllConfiguration();

}
