package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Dan on 7/18/16.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    private String password;

    private LocalDateTime lastMessageCheck=null;

    public User()
    {
    }

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
        lastMessageCheck = LocalDateTime.now();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public LocalDateTime getLastMessageCheck()
    {
        return lastMessageCheck;
    }

    public void setLastMessageCheck(LocalDateTime lastMessageCheck)
    {
        this.lastMessageCheck = lastMessageCheck;
    }
}
