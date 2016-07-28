package com.theironyard.services;

import com.theironyard.entities.Message;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Dan on 7/19/16.
 */
public interface MessageRepository extends CrudRepository<Message, Integer>{
    public List<Message> findByRecipient(User user);
    public List<Message> findByAuthor(User user);
    public List<Message> findByConversation(String conversation);
}
