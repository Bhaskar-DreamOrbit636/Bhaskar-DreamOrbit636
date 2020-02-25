package com.reminder.security;



import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.reminder.service.AppUserDetailService;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:/application.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private AppUserDetailService userDetailService;
	
	@Autowired
	private CustomLogoutSuccessHandler customLoutHandler;
	
    /*@Autowired
    private ActiveDirectoryLdapAuthenticationProvider a;*/
	
	private Logger logger = Logger.getLogger(WebSecurityConfig.class);
	
/*	@Value("${r365.spring.ldap.userDnPatterns}")
	private String userDnPatterns;
	
	@Value("${r365.spring.ldap.ldapUrl}")
	private String ldapUrl;
	
	@Value("${r365.spring.ldap.managerDn}")
	private String managerDn;
	
	@Value("${r365.spring.ldap.managerPassword}")
	private String managerPassword;*/
	
	private String userDnPatterns = System.getProperty("r365.spring.ldap.userDnPatterns");
	private String ldapUrl = System.getProperty("r365.spring.ldap.ldapUrl");
	private String managerDn = System.getProperty("r365.spring.ldap.managerDn");
	private String managerPassword = System.getProperty("r365.spring.ldap.managerPassword");
	
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    	
    	try{
    		
 /*  		 ActiveDirectoryLdapAuthenticationProvider adProvider = 
                     new ActiveDirectoryLdapAuthenticationProvider("localhost",ldapUrl);
   		logger.info("-------------websecurtiy-----------ininininininininin"+ldapUrl);
         adProvider.setConvertSubErrorCodesToExceptions(true);
         adProvider.setUseAuthenticationRequestCredentials(true);
         logger.info("-------------websecurtiy-----------22222222");

         // set pattern if it exists
         // The following example would authenticate a user if they were a member
         // of the ServiceAccounts group
         // (&(objectClass=user)(userPrincipalName={0})
         //   (memberof=CN=ServiceAccounts,OU=alfresco,DC=mycompany,DC=com))
         if (userDnPatterns != null && userDnPatterns.trim().length() > 0)
         {
             adProvider.setSearchFilter(userDnPatterns);
         }
         logger.info("-------------websecurtiy-----------3333333");
         auth.authenticationProvider(adProvider).userDetailsService(userDetailService);
         //auth.authenticationProvider(adProvider);
         logger.info("-------------websecurtiy-----------444444444");*/
         // don't erase credentials if you plan to get them later
               
         
    	//auth.authenticationProvider(a).userDetailsService(userDetailsService());
    	logger.info("--WebSecurityConfig--Connecting wtih---------userSearchFilter: "+userDnPatterns +" --- LDApUrl: "+ ldapUrl +" --- ManagerDN: "+managerDn);	
      	    	
    	auth.ldapAuthentication().userSearchFilter(userDnPatterns).contextSource()
    	.url(ldapUrl)
    	.managerDn(managerDn).managerPassword(managerPassword);
    	
    	auth.userDetailsService(userDetailService);
    	
    	logger.info("--WebSecurityConfig--Connected with----------userSearchFilter: "+userDnPatterns +" --- LDApUrl: "+ ldapUrl +" --- ManagerDN: "+managerDn);
    	
    	}catch(Exception e){
			logger.error("--WebSecurityConfig--erorr connecting LDAP--" + e);
    	}
    }
    
    @Override
      protected void configure(HttpSecurity http) throws Exception {
    	
    	
    //	 Collection<? extends GrantedAuthority> userName= SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    	logger.info("--WebSecurityConfig--Configuring HttpSecurity");
        http.cors().and().csrf().disable().authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/swagger-ui.html").permitAll()   // only to test swagger
            .antMatchers(HttpMethod.POST,"/multipleSave").permitAll()  // only to test file upload from UI /savefile
            .antMatchers("/index.jsp").permitAll()
            .antMatchers("/dist/**").permitAll()// only to test file upload from UI /savefile
            .antMatchers("/lib/**").permitAll()
            .antMatchers("/css/**").permitAll()
            .antMatchers("/webjars/**").permitAll()
            .antMatchers("/images/**").permitAll()
            .antMatchers("/configuration/**").permitAll()
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/v2/**").permitAll()
            .antMatchers(HttpMethod.POST, "/login").permitAll()
           // .antMatchers("/users").hasAnyAuthority("ADMIN")
            //.antMatchers("/updateGroup").hasAnyAuthority("GROUPADMIN")
           // .antMatchers("/groups").hasAnyAuthority("GROUPADMIN")
            .antMatchers("/**").permitAll()
           // .anyRequest().authenticated()
            .and()
            // We filter the api/login requests
            .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
                    UsernamePasswordAuthenticationFilter.class)
            // And filter other requests to check the presence of JWT in header
            .addFilterBefore(new JWTAuthenticationFilter(userDetailService),
                    UsernamePasswordAuthenticationFilter.class).exceptionHandling().accessDeniedPage("/403")
            .and().logout().logoutSuccessHandler(logoutSuccessHandler())
            .and().headers().cacheControl().disable();
        
      }
    
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return customLoutHandler;
    }

}
