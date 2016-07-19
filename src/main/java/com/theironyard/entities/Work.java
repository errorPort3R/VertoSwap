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


}
