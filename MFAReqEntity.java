package com.example.fiservapp.model;

import lombok.Data;

@Data
public class MFAReqEntity {
	private String userName;
	private String email;
	private String phoneNumber;
	private String deviceName;
	private String deviceType;
}
