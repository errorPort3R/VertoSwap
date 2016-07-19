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
    User receiver;

    @Column(nullable = false)
    Item item;

    public Thread()
    {
    }

    public Thread(User sender, User receiver, Item item)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.item = item;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public User getSender()
    {
        return sender;
    }

    public void setSender(User sender)
    {
        this.sender = sender;
    }

    public User getReceiver()
    {
        return receiver;
    }

    public void setReceiver(User reciever)
    {
        this.receiver = reciever;
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }
}
