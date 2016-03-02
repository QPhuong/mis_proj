package com.iso.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.iso.repository.UserRepository;
import com.iso.service.AuthenticationService;

@Service
public class UserDetailServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	@Autowired
	@Qualifier(value="authenticationService")
	private AuthenticationService authenticationService;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		com.iso.domain.User domainUser;
		if (authenticationService.getSelectedOrg() == null) {
			domainUser = userRepository.findByUsername(username);
		}else {
			domainUser = userRepository.findByUsernameAndOrganization(username, authenticationService.getSelectedOrg());
		}
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        
		UserDetails userDetail = new User(domainUser.getUsername(), domainUser.getPassword(), authorities);
	    return userDetail;
	}
	
	
}
