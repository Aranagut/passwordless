package com.example.fiservapp.model.fido;

import lombok.Data;

@Data
public class AuthenticateFDCIAMResp{
	private String message;
	private String status;
	private String email;
	private String username;
}