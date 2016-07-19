package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by jeffryporter on 7/19/16.
 */
@Entity
@Table(name = "items")
public class Item
{
    public enum Status
    {
        PENDING,
        ACTIVE,
        INACTIVE,
        ARCHIVE,
        DELETE
    }

    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String location;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    ArrayList<String> photos;

    @Column(nullable = false)
    String acceptableExchange;

    @Column(nullable = false)
    Status status;

    @Column(nullable = false)
    LocalDateTime time;

    @Column(nullable = false)
    boolean service;

    @ManyToOne
    User user;
}
