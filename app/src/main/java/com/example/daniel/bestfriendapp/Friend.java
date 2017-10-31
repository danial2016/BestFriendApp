package com.example.daniel.bestfriendapp;

/**
 * Created by Daniel on 2017-10-27.
 * This class represent a Friend-object and contains the desired information
 * about a friend which is necessary for the application to use: name, email
 * and the phone-number.
 * Return-methods have been added, but setter-methods are not needed since we
 * don't want to change the info of a Friend-object.
 */

public class Friend {
    private String name;
    private String phoneNumber;
    private String email;

    public Friend(String name, String phoneNumber, String email){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
