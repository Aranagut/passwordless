package com.example.fiservapp.model.fido;

import com.example.fiservapp.model.Rp;
import lombok.Data;

@Data
public class RegisterFIDOResp {
    private String deviceType;
    private String publicKeyCredentialCreationOptions;
    private String authId;
    private Rp rp;
    private String status;
}