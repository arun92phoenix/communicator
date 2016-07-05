package com.communicator.app.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.communicator.app.services.CommunicatorService;

/**
 * Handler for successful logout. The logged in user is removed from the list of
 * participants and will be published to all the other participants.
 * 
 * @author pavan
 *
 */
@Component
public class LogoutSuccessHandler
		implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

	@Autowired
	CommunicatorService communicatorService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
			throws IOException, ServletException {

		// Remove the participant from list of participants
		communicatorService.removeParticipant(auth.getName());

		// Redirect the user to the login page.
		response.sendRedirect("/index.html");
	}

}
