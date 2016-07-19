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

    public Thread()
    {
    }

    public Thread(User sender, User reciever, Item item)
    {
        this.sender = sender;
        this.reciever = reciever;
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

    public User getReciever()
    {
        return reciever;
    }

    public void setReciever(User reciever)
    {
        this.reciever = reciever;
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
