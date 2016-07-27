package com.communicator.app.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.communicator.app.persistence.MessageRepository;
import com.communicator.app.persistence.model.Message;

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

	@Autowired
	MessageRepository messageRepository;

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
		message.setMessageFrom(from);
		message.setMessageTo(to);
		message.setMessageTime(new Date());

		// Use Simple Messaging template to send the message.
		simpMessagingTemplate.convertAndSend("/user/" + to + "/receive", message);

		// Save the message to database
		messageRepository.save(message);
	}
}
