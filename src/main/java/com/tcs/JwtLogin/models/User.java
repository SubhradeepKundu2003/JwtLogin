package com.tcs.JwtLogin.models;


import jakarta.persistence.Entity;


@Entity
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
