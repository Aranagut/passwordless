package com.example.fiservapp.service;

import com.example.fiservapp.model.AuthResEntity;
import com.example.fiservapp.model.LoginRequest;
import com.example.fiservapp.model.User;
import com.example.fiservapp.model.Users;
import com.example.fiservapp.model.fido.ActivateFIDO;
import com.example.fiservapp.model.fido.ActivateFIDOResp;
import com.example.fiservapp.model.fido.AuthenticateFDReq;
import com.example.fiservapp.model.fido.AuthenticateFDResp;
import com.example.fiservapp.model.fido.InitiateFDAuthenticationReq;
import com.example.fiservapp.model.fido.InitiateFDAuthenticationResp;
import com.example.fiservapp.model.fido.RegisterFIDO;
import com.example.fiservapp.model.fido.RegisterFIDOResp;

public interface UserService {
	
	Users addUser(User user); 
	
	Users deleteUser(String userName); 
	
	Users updateUser(User user); 
	
    AuthResEntity validateUser(LoginRequest request);

    RegisterFIDOResp registerFIDODevice(RegisterFIDO registerFIDO);

    ActivateFIDOResp activateFIDODevice(ActivateFIDO activateFIDO);

    InitiateFDAuthenticationResp initiateFIDOAuthentication(InitiateFDAuthenticationReq initiateFDAuthenticationReq);
    
    InitiateFDAuthenticationResp initiateFIDOAuthNoUserName(InitiateFDAuthenticationReq initiateFDAuthenticationReq);
    
    AuthenticateFDResp authenticateFIDODevice(AuthenticateFDReq authenticateFDReq);
}