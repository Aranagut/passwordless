package com.example.fiservapp.controller;

public class FIDOAuthenticateDeviceException extends RuntimeException{
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = -4090867593296134018L;

	public FIDOAuthenticateDeviceException(Throwable cause) {
	        super(cause);
	    }
	  
	  public FIDOAuthenticateDeviceException(String exMessage) {
	        super(exMessage);
	    }
}	
