package com.example.fiservapp.model;

import lombok.Data;

@Data
public class CIAMRiskResEntity {
	private String riskLevel;
	private String riskScore;
	private String recommendedAction;
	private String evaluationId;
}
