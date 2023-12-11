package com.example.fiservapp.model;

import lombok.Data;

@Data
public class CreateDeviceResEntity {
	
	private String authId;
	private String deviceType;
	private String status;
	private String deviceName;
	private String secret;
	private String registrationUri;
}
