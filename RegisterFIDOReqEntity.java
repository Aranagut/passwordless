package com.example.fiservapp.model.fido;

import com.example.fiservapp.model.Policy;
import com.example.fiservapp.model.Rp;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterFIDOReqEntity {
    String deviceType;
    Rp rp;
    String email;
    Policy policy;
}