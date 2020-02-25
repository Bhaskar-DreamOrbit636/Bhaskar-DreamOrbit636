package com.reminder.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

//@Controller
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
	
    private Logger logger = Logger.getLogger(JWTLoginFilter.class);

	public JWTLoginFilter(String url, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
	}

	@Override
	// @Bean
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException, ServletException {
		logger.info("attemptAuthentication(req=" + req + ", res=" + res + ") - start - entering attemptAuthentication");
		AccountCredentials creds = new ObjectMapper().readValue(req.getInputStream(), AccountCredentials.class);

		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(),
				creds.getPassword(), Collections.emptyList()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		logger.info("successfulAuthentication(req=" + req + ", res=" + res + ", chain=" + chain + ", auth=" + auth + ") - start - entering successfulAuthentication");
		TokenAuthenticationService.addAuthentication(res, auth.getName());
		logger.info("successfulAuthentication(req=" + req + ", res=" + res + ", chain=" + chain + ", auth=" + auth + ") - end - exit in successfulAuthentication");
	}
}