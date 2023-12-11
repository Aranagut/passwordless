package com.example.fiservapp.config;

public class EndpointConstants {

	private EndpointConstants() {

	}

	public static final String REQUEST_OTP = "/requestOTP";
	public static final String VALIDATE_OTP = "/validateOTP";
	public static final String UNPAIR_MFA_DEVICE = "/unpairFIDO2Devices";
	public static final String CREATE_MFA_DEVICE = "/createTOTPDevice";
	public static final String ACTIVATE_MFA_DEVICE = "/activateTOTPDevice";
	public static final String GET_MFA_DEVICE = "/getMFADevice";
	public static final String ADD_USER = "/user";
	public static final String UPDATE_USER = "/user";
	public static final String DELETE_USER = "/user/{username}";
}
