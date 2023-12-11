package com.example.fiservapp.model.fido;

import lombok.Data;

@Data
public class ActivateFIDOReqEntity{
	private String deviceType;
	private String attestation;
	private String origin;
}