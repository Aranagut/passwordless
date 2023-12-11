package com.example.fiservapp.model.fido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitiateFDAuthenticationCIAMReq {
	private String deviceType;
	private String userName;
	private String rpID;
}