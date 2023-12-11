package com.example.fiservapp.model.fido;

import lombok.Data;

@Data
public class InitiateFDAuthenticationCIAMResp {
	private String publicKeyCredentialRequestOptions;
	private String message;
	private String authId;
	private String status;
}