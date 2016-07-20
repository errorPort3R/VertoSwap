package com.theironyard.services;

import com.theironyard.entities.Item;
import com.theironyard.entities.Thread;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Dan on 7/19/16.
 */
public interface ThreadRepository extends CrudRepository<Thread, Integer>
{
    public Iterable<Thread> findBySender(User sender);
    public Iterable<Thread> findByReceiver(User receiver);
    public Iterable<Thread> findBySenderAndReceiverAndItem(User sender, User receiver, Item item);
}
