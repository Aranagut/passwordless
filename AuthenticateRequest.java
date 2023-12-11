package com.example.fiservapp.model;

import lombok.Data;

@Data
public class AuthenticateRequest {
	private String otp;
	private String authID;
	private String user_name;
}
