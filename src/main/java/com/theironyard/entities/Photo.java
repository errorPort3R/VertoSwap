package com.theironyard.entities;

import javax.persistence.*;

/**
 * Created by jeffryporter on 7/19/16.
 */
@Entity
@Table(name = "items")
public class Photo
{
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String newFilename;

    @Column(nullable = false)
    private String caption;

    @ManyToOne
    User user;

    @ManyToOne
    Item item;

    public Photo()
    {
    }

    public Photo(String originalFilename, String newFilename, String caption, User user, Item item)
    {
        this.originalFilename = originalFilename;
        this.newFilename = newFilename;
        this.caption = caption;
        this.user = user;
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

    public String getOriginalFilename()
    {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename)
    {
        this.originalFilename = originalFilename;
    }

    public String getNewFilename()
    {
        return newFilename;
    }

    public void setNewFilename(String newFilename)
    {
        this.newFilename = newFilename;
    }

    public String getCaption()
    {
        return caption;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
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
