package com.example.fiservapp.service;

import com.example.fiservapp.model.fido.ActivateFIDOResp;
import com.example.fiservapp.model.fido.RegisterFIDOResp;

public interface FIDO2Service {
    RegisterFIDOResp registerFIDODevice(String accessToken, String username, String rpID, String rpName, String email);

    ActivateFIDOResp activateFIDODevice(String username, String attestation, String authId, String origin, String token);
}
