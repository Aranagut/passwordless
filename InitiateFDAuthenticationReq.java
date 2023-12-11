package com.example.fiservapp.model.fido;

import lombok.Data;

@Data
public class InitiateFDAuthenticationReq {
	private String userName;
	private String rpID;
}