package com.theironyard.services;

import com.theironyard.entities.Thread;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Dan on 7/19/16.
 */
public interface ThreadRepository extends CrudRepository<Thread, Integer> {
}
