package com.communicator.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.communicator.app.services.CommunicatorService;

@Component
public class LoginSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {

	@Autowired
	CommunicatorService communicatorService;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		communicatorService.addParticipant(event.getAuthentication().getName());
	}

}
