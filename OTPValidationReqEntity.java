package com.example.fiservapp.model;

import lombok.Data;

@Data
public class OTPValidationReqEntity {
	private String deviceType;
	private String otp;
}
