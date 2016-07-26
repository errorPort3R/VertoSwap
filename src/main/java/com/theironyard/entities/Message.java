package com.theironyard.entities;

import javax.persistence.*;
import javax.security.sasl.AuthorizeCallback;
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
    private int id;

    @ManyToOne
    private User author;

    @ManyToOne
    private User recipient;

    @ManyToOne
    private Item item;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private LocalDateTime time;

    public Message()
    {
    }

    public Message(User author, User recipient, Item item, String body, LocalDateTime time)
    {
        this.author = author;
        this.recipient = recipient;
        this.item = item;
        this.body = body;
        this.time = time;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public User getAuthor()
    {
        return author;
    }

    public void setAuthor(User author)
    {
        this.author = author;
    }

    public User getRecipient()
    {
        return recipient;
    }

    public void setRecipient(User recipient)
    {
        this.recipient = recipient;
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public LocalDateTime getTime()
    {
        return time;
    }

    public void setTime(LocalDateTime time)
    {
        this.time = time;
    }
}
