package com.example.fiservapp.model;

import lombok.Data;

@Data
public class User {
    private String username;
    private String password;
    private String deviceType;
    private String email;
    private String mobileNo;
    private String deviceName;
    private String state;
}
