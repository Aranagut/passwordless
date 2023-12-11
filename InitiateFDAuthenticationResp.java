package com.example.fiservapp.model.fido;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InitiateFDAuthenticationResp {
	private String authId;
	//private String username;
	private String publicKeyCredentialRequestOptions;
	private String status;
}