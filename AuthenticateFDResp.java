package com.example.fiservapp.model.fido;

import lombok.Data;

@Data
public class AuthenticateFDResp{
	private String status;
	private String message;
	private String username;
	private String email;
}