package com.communicator.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.communicator.app.services.CommunicatorService;

/**
 * Handler for successful login. The logged in user is added to the list of
 * participants and will be published to all the other participants.
 * 
 * @author pavan
 * 
 *
 */
@Component
public class LoginSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {

	@Autowired
	CommunicatorService communicatorService;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		// Add the participant to the list of participants
		// communicatorService.addParticipant(event.getAuthentication().getName());
		// TODO: Add code to update logged in status.
	}

}
