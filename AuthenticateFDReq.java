package com.example.fiservapp.model.fido;

import lombok.Data;

@Data
public class AuthenticateFDReq{
	private String origin;
	private String assertion;
	private String authID;
}