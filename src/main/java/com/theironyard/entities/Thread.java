package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by jeffryporter on 7/19/16.
 */
@Entity
@Table(name = "threads")
public class Thread
{
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    User sender;

    @Column(nullable = false)
    User reciever;

    @Column(nullable = false)
    Item item;
}
