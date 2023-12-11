package com.example.fiservapp.model;

public class MfaDeviceReq {
    private String deviceType;
    private MfaDeviceReqRp rp;

    public MfaDeviceReqRp getRp() {
        return rp;
    }

    public void setRp(MfaDeviceReqRp rp) {
        this.rp = rp;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    // Getters and setters
}