package com.communicator.app.persistence;

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

}
