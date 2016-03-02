package com.iso.repository;

import com.iso.domain.UserSessionActivity;

public interface UserSessionActivityRepositoryCustom {

	public UserSessionActivity findByUserIdAndIPAddressAndSessionId(String userId, String ipAddress, String sessionId);
	public long countByUserId(String userId);
	
}
