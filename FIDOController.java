package com.example.fiservapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fiservapp.model.AuthResEntity;
import com.example.fiservapp.model.FIDO2RegisteredReq;
import com.example.fiservapp.model.LoginRequest;
import com.example.fiservapp.model.fido.ActivateFIDO;
import com.example.fiservapp.model.fido.ActivateFIDOResp;
import com.example.fiservapp.model.fido.AuthenticateFDReq;
import com.example.fiservapp.model.fido.AuthenticateFDResp;
import com.example.fiservapp.model.fido.InitiateFDAuthenticationReq;
import com.example.fiservapp.model.fido.InitiateFDAuthenticationResp;
import com.example.fiservapp.model.fido.RegisterFIDO;
import com.example.fiservapp.model.fido.RegisterFIDOResp;
import com.example.fiservapp.service.UserServiceImpl;

@RestController
public class FIDOController {

    @Autowired
    UserServiceImpl fidoservice;

    @CrossOrigin(origins = "*")
    @PostMapping("/registerFIDO")
    public RegisterFIDOResp registerFIDO(@RequestBody RegisterFIDO registerFIDO) {
        return fidoservice.registerFIDODevice(registerFIDO);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/activateFIDO")
    public ActivateFIDOResp activateFIDO(@RequestBody ActivateFIDO activateFIDO) {
        return fidoservice.activateFIDODevice(activateFIDO);
    }
    
    @CrossOrigin(origins = "*")
    @PostMapping(value = "/initiateAuthFIDO")
    public InitiateFDAuthenticationResp initiateFIDOAuthentication(@RequestBody InitiateFDAuthenticationReq request){
        InitiateFDAuthenticationResp  response = fidoservice.initiateFIDOAuthentication(request);
        return response;
    }
    
    @CrossOrigin(origins = "*")
    @PostMapping(value = "/initiateAuthFIDONoUsername")
    public InitiateFDAuthenticationResp initiateFIDOAuthNoUserName(@RequestBody InitiateFDAuthenticationReq request) {
    	InitiateFDAuthenticationResp response = fidoservice.initiateFIDOAuthNoUserName(request);
    	return response;
    }
    
    @CrossOrigin(origins = "*")
    @PostMapping(value="/authenticateFIDO")
    public AuthenticateFDResp autheticateFidoDevice(@RequestBody AuthenticateFDReq request) {
          AuthenticateFDResp response = fidoservice.authenticateFIDODevice(request);
          return response;
    }

    @GetMapping(value = "/test")
    public String test() {
        return "test";
    }
    
    @CrossOrigin(origins = "*")
	@PostMapping("/authenticate")
	@ResponseBody
	public AuthResEntity authenticate(@RequestBody LoginRequest user) {
		return fidoservice.validateUser(user);
	}

	@CrossOrigin(origins = "*")
	@PostMapping("/isFIDO2Registered")
	@ResponseBody
	public boolean isFIDO2Registered(@RequestBody FIDO2RegisteredReq user) {
		return fidoservice.isFIDO2Registered(user);
	}


}