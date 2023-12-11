package com.example.fiservapp.controller;

/**
 * @author Prasad pokharkar
 */
public class FIDORegisterDeviceException extends RuntimeException {
    public FIDORegisterDeviceException(Throwable cause) {
        super(cause);
    }
    
    public FIDORegisterDeviceException(String exMessage) {
        super(exMessage);
    }
}
