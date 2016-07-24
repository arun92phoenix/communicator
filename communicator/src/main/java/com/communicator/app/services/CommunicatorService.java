package com.communicator.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.communicator.app.model.Message;

/**
 * Main Service class containing the logic
 * 
 * @author pavan
 *
 */
@Service
public class CommunicatorService {

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	/**
	 * Sends the message to a particular user.
	 * 
	 * @param from
	 *            Message From
	 * @param to
	 *            Message to
	 * @param message
	 *            Actual Message
	 */
	public void deliverMessage(String from, String to, Message message) {

		// Set whom the message is from
		message.setFrom(from);

		// Use Simple Messaging template to send the message.
		simpMessagingTemplate.convertAndSend("/user/" + to + "/receive", message);
	}	
}
