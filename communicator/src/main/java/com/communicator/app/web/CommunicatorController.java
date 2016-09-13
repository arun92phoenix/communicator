package com.communicator.app.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.communicator.app.persistence.model.Message;
import com.communicator.app.services.CommunicatorService;

/**
 * Controller class
 * 
 * @author pavan
 *
 */
@Controller
public class CommunicatorController {

	private static final int MESSAGES_PAGE_SIZE = 10;
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
	 * Returns messages sent by a user to
	 * 
	 * @param from
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/messages/{from}")
	public @ResponseBody Page<Message> getMessagesFrom(@PathVariable("from") String from,
			@RequestParam("last") Long last) {
		return communicatorService.getMessagesFrom(from, last, MESSAGES_PAGE_SIZE);
	}

}
