package com.example.fiservapp.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CIAMRiskReqEntity {

	@NotNull
	private String appId;
	@NotNull
	private String ipConfig;
	@NotNull
	private String userId;
	@Nullable
	private String sessionId;
	@Nullable
	private String browserCookie;
	@Nullable
	private String browserUserAgent;
	@Nullable
	private String signalData;
}
