package com.theironyard.services;

import com.theironyard.entities.Messagea;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Dan on 7/19/16.
 */
public interface MessageaRepository extends CrudRepository<Messagea, Integer>{
    public List<Messagea> findByRecipient(User user);
    public List<Messagea> findByAuthor(User user);
    public List<Messagea> findByConversation(String conversation);
}
