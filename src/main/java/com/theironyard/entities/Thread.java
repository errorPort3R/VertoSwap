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

    @ManyToOne
    User sender;

    @ManyToOne
    User receiver;

    @ManyToOne
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
