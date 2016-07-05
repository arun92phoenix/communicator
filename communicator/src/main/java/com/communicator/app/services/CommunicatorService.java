package com.communicator.app.services;

import java.util.HashSet;
import java.util.Set;

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

	Set<String> participants = new HashSet<String>();

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

	/**
	 * Adds a participant to the list of participants and updates the users.
	 * 
	 * @param userName
	 */
	public void addParticipant(String userName) {

		// Add participant to the HashSet of participants
		participants.add(userName);

		// Send an update to all users
		simpMessagingTemplate.convertAndSend("/update.participants", participants);
	}

	/**
	 * Returns the list of all participants.
	 * 
	 * @return
	 */
	public Set<String> getParticipants() {
		return participants;
	}

	/**
	 * Removes a participant from the list of participants and updates the
	 * users.
	 * 
	 * @param userName
	 */
	public void removeParticipant(String userName) {

		// Remove the participant from the hash set of participants.
		participants.remove(userName);

		// Send an update to all users
		simpMessagingTemplate.convertAndSend("/update.participants", participants);
	}
}
