package com.example.fiservapp.model.fido;

import lombok.Data;

@Data
public class ActivateFIDO {
    private String origin;
    private String attestation;
    private String authId;

    private String deviceType = "FIDO2";

    private String username;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }
}
