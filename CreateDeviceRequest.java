package com.example.fiservapp.model;

import lombok.Data;

@Data
public class CreateDeviceRequest {
	
	private String username;
	private String deviceType;
	private String deviceName;
}
