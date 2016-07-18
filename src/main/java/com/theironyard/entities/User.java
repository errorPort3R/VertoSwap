package com.theironyard.entities;

import javax.persistence.*;

/**
 * Created by Dan on 7/18/16.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    String password;
}
