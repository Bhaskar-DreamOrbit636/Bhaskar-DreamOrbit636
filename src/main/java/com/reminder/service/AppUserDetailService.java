/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reminder.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.UserDAO;
import com.reminder.exception.UserInactiveException;

/**
 *
 * @author 
 */
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class AppUserDetailService implements UserDetailsService {

    @Autowired
    private UserDAO userRepository;
    
    private Logger logger = Logger.getLogger(AppUserDetailService.class);
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("loadUserByUsername(username=" + username + ") - start");
    	
		com.reminder.model.User user = userRepository.getUserByName(username);
		logger.info("--------------AppUserDetailService---------username = "+username);
        if (user == null) {
        	 //userRepository.updateUnSuccessLoginDate(user.getUserId());
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }
        if(!user.getActive()){
		
        	throw new UserInactiveException("User '" + username + "' Inactive");
        }
                	
        userRepository.updateLoginDate(user.getUserId());
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        if(user.getGroupAdmin()!=null && user.getGroupAdmin() ){
        	authorities.add(new SimpleGrantedAuthority("GROUPADMIN"));
        }
        
        
        if(user.getUserAdmin()!=null && user.getUserAdmin() ){
        	authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }
        logger.info("--------------AppUserDetailService---------authorities = "+authorities);
        UserDetails u = org.springframework.security.core.userdetails.User
        .withUsername(username)
        .password("")
        .authorities(authorities)
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, u.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("--------------AppUserDetailService---------return the UserDetails = " + u.toString());
        return u;

    }


    
/*    private Collection<? extends GrantedAuthority> getAuthorities(
      Collection<Role> roles) {
  
        return getGrantedAuthorities();
    }*/
    
    private List<GrantedAuthority> getGrantedAuthorities( List<GrantedAuthority> authorities) {
		logger.info("getGrantedAuthorities(authorities=" + authorities + ") - start");
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
		logger.info("getGrantedAuthorities(authorities=" + authorities + ") - end - return value=" + authorities);
        return authorities;
    }
    
 
/*    private List<String> getPrivileges(Collection<Role> roles) {
  
        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }
 
    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }*/
    
}