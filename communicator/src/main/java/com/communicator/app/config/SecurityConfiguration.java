package com.communicator.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	LogoutSuccessHandler logoutSuccessHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().formLogin().loginPage("/index.html").loginProcessingUrl("/login")
				.defaultSuccessUrl("/communicate.html").permitAll().and().authorizeRequests()
				.antMatchers("/index.html", "/lib/**").permitAll().anyRequest().authenticated().and().logout()
				.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler).logoutSuccessUrl("/index.html");
	}

	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(new AuthenticationProvider() {

			@Override
			public boolean supports(Class<?> authentication) {
				return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
			}

			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				return authentication;

			}
		});
	}
}
