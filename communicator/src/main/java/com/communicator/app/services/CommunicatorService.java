package com.communicator.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
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

	/**
	 * Returns messages sent by a user to the current logged in user.
	 * 
	 * @param from
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Message> getMessagesFrom(String from, Long last, int size) {
		// Determining the current logged in user.
		String to = SecurityContextHolder.getContext().getAuthentication().getName();

		List<String> participants = new ArrayList<String>();
		participants.add(from);
		participants.add(to);

		Sort sort = new Sort(Direction.DESC, "messageTime");
		Pageable pageRequest = new PageRequest(0, size, sort);

		// Making a request using repository
		if (last == 0) {
			return messageRepository.findMessagesByMessageFromInAndMessageToIn(participants, participants, pageRequest);
		} else {
			return messageRepository.findMessagesByMessageFromInAndMessageToInAndIdLessThan(participants, participants,
					last, pageRequest);
		}

	}
}
