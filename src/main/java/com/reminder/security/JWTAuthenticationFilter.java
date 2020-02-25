package com.reminder.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import com.reminder.service.AppUserDetailService;


public class JWTAuthenticationFilter extends GenericFilterBean {
	
	@Autowired
	private AppUserDetailService userDetailService;
	
	public JWTAuthenticationFilter(AppUserDetailService userDetailService){
		 this.userDetailService = userDetailService;
	}
	
  @Override
  public void doFilter(ServletRequest request,
             ServletResponse response,
             FilterChain filterChain)
      throws IOException, ServletException {
/*    Authentication authentication = TokenAuthenticationService
        .getAuthentication((HttpServletRequest)request);*/
	//  Collection<? extends GrantedAuthority>  c =SecurityContextHolder.getContext().getAuthentication().getAuthorities();
   /* SecurityContextHolder.getContext()
        .setAuthentication(authentication);*/
  
    filterChain.doFilter(request,response);
  }
}
