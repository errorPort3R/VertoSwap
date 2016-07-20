package com.theironyard.services;

import com.theironyard.entities.Message;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Dan on 7/19/16.
 */
public interface MessageRepository extends CrudRepository<Message, Integer>
{
}
