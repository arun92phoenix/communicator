package com.communicator.app.web;

import java.security.Principal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.communicator.app.model.Message;
import com.communicator.app.services.CommunicatorService;

/**
 * Controller class
 * 
 * @author pavan
 *
 */
@Controller
public class CommunicatorController {

	@Autowired
	CommunicatorService communicatorService;

	/**
	 * Receives a message from a user and forwards it to the username provided.
	 * 
	 * @param message
	 * @param username
	 * @param principal
	 */
	@MessageMapping("/send.to.{username}")
	public void sendMessage(@Payload Message message, @DestinationVariable("username") String username,
			Principal principal) {
		communicatorService.deliverMessage(principal.getName(), username, message);
	}

	/**
	 * Returns the list of participants.
	 * 
	 * @return
	 */
	@SubscribeMapping("/participants")
	public Set<String> getParticipants() {
		return communicatorService.getParticipants();
	}

}
