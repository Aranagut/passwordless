package com.example.fiservapp.model;

import lombok.Value;

@Value
public class ActivateFIDORespEntity{
	String deviceType;
	String attestation;
	String origin;
}