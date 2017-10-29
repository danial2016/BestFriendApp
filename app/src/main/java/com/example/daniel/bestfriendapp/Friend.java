package com.example.daniel.bestfriendapp;

/**
 * Created by Daniel on 2017-10-27.
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
