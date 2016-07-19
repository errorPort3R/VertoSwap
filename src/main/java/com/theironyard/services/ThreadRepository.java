package com.theironyard.services;

import com.theironyard.entities.Thread;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Dan on 7/19/16.
 */
public interface ThreadRepository extends CrudRepository<Thread, Integer>
{
    public Iterable<Thread> findByUser(User user);
}
