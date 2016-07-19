package com.communicator.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.communicator.app.services.UserDetailsService;

/**
 * Security configuration
 * 
 * @author pavan
 *
 */

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	LogoutSuccessHandler logoutSuccessHandler;

	@Autowired
	UserDetailsService userDetailsService;

	/**
	 * Configuration of security. Form login.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().formLogin().loginPage("/login.html").loginProcessingUrl("/login")
				.defaultSuccessUrl("/index.html").permitAll().and().authorizeRequests()
				.antMatchers("/login.html", "/lib/**").permitAll().anyRequest().authenticated().and().logout()
				.logoutSuccessHandler(logoutSuccessHandler);
	}

	/**
	 * Method to configure authentication. This method ensures everyone can
	 * login by just providing name. No password is needed.
	 * 
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

}
