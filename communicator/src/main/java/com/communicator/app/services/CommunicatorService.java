package com.communicator.app.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.communicator.app.model.Message;

@Service
public class CommunicatorService {

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	Set<String> participants = new HashSet<String>();

	public void deliverMessage(String from, String to, Message message) {
		message.setFrom(from);
		simpMessagingTemplate.convertAndSend("/user/" + to + "/receive", message);
	}

	public void addParticipant(String userName) {
		participants.add(userName);
		simpMessagingTemplate.convertAndSend("/update.participants", participants);
	}

	public Set<String> getParticipants() {
		return participants;
	}

	public void removeParticipant(String userName) {
		participants.remove(userName);
		simpMessagingTemplate.convertAndSend("/update.participants", participants);
	}
}
