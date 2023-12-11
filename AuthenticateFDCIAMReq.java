package com.example.fiservapp.model.fido;

import lombok.Data;

@Data
public class AuthenticateFDCIAMReq{
	private String deviceType;
	private String origin;
	private String assertion;
}