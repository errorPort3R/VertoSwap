package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by jeffryporter on 7/19/16.
 */
@Entity
@Table(name = "messages")
public class Message
{
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String body;

    @Column(nullable = false)
    LocalDateTime time;

    @ManyToOne
    Thread thread;
}
