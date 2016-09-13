package com.communicator.app.persistence;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.communicator.app.persistence.model.Message;

/**
 * Repository interface to access Message store.
 * 
 * @author pavan
 *
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	/**
	 * Used to get messages which are sent by a particular user to the logged in
	 * user
	 * 
	 * @return
	 */
	public Page<Message> findMessagesByMessageFromInAndMessageToInAndIdLessThan(Collection<String> fromParticipants,
			Collection<String> toParticipants, Long lastId, Pageable page);
	
	public Page<Message> findMessagesByMessageFromInAndMessageToIn(Collection<String> fromParticipants,
			Collection<String> toParticipants, Pageable page);
}
