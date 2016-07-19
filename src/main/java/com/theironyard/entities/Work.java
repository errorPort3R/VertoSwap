package com.theironyard.entities;

import javax.persistence.*;

/**
 * Created by jeffryporter on 7/19/16.
 */
@Entity
@Table(name = "works")
public class Work
{
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String jobTitle;

    @Column(nullable = false)
    String description;

    @ManyToOne
    User user;

    public Work()
    {
    }

    public Work(String jobTitle, String description, User user)
    {
        this.jobTitle = jobTitle;
        this.description = description;
        this.user = user;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getJobTitle()
    {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle)
    {
        this.jobTitle = jobTitle;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
