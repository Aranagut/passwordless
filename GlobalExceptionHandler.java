package com.example.fiservapp.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Prasad pokharkar
 */

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value={FIDORegisterDeviceException.class})
    protected ResponseEntity<Object> FIDORegisterDeviceException(FIDORegisterDeviceException ex, WebRequest request) {
         return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value={FIDOAuthenticateDeviceException.class})
    protected ResponseEntity<Object> FIDOAuthenticateDeviceException(FIDOAuthenticateDeviceException ex, WebRequest request) {
         return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}


