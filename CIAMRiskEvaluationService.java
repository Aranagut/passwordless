package com.example.fiservapp.ciam.service;

import java.util.Collections;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.fiservapp.ciam.shared.model.CIAMHttpRequest;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestObjects;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestParams;
import com.example.fiservapp.model.CIAMRiskReqEntity;
import com.example.fiservapp.model.CIAMRiskResEntity;


@Service
public class CIAMRiskEvaluationService{

	@Autowired
	RestTemplateService restTemplateService;
	
	@Value("${ciam.mfa.base.url}")
	private String BASE_URL;
	@Value("${ciam.risk.eval.path}")
	private String RISK_EVAL_PATH;

	public CIAMRiskResEntity createRiskEvaluation(String accessToken, CIAMRiskReqEntity pingProtectRequest) {
		CIAMRiskResEntity thePingProtectResponse = null;
		CIAMHttpRequest<CIAMRiskResEntity> theRequest = prepareRiskEvaluationRequest(accessToken, pingProtectRequest);
		ResponseEntity<CIAMRiskResEntity> response = restTemplateService.execute(theRequest);
		if(Objects.nonNull(response) && Objects.nonNull(response.getBody())) {
			thePingProtectResponse = response.getBody();
		}
		return thePingProtectResponse;
	}
	
	private CIAMHttpRequest<CIAMRiskResEntity> prepareRiskEvaluationRequest(String accessToken, CIAMRiskReqEntity ciamRiskRequest) {
		CIAMHttpRequest<CIAMRiskResEntity> customHttpRequest = new CIAMHttpRequest<>();
		CIAMHttpRequestParams theCustomRequestParams = new CIAMHttpRequestParams();
		HttpHeaders headerParams = new HttpHeaders();
		headerParams.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headerParams.setContentType(MediaType.APPLICATION_JSON);
		headerParams.add("Authorization", "Bearer " + accessToken);
		theCustomRequestParams.setHeaderParams(headerParams);
		customHttpRequest.setCustomRequestParams(theCustomRequestParams);
		CIAMHttpRequestObjects theCIAMHttpRequestObjects = new CIAMHttpRequestObjects();
		theCIAMHttpRequestObjects.setMethod(HttpMethod.POST);
		theCIAMHttpRequestObjects.setUri(BASE_URL);
		theCIAMHttpRequestObjects.setPath(RISK_EVAL_PATH);
		theCIAMHttpRequestObjects.setBody(ciamRiskRequest);
		customHttpRequest.setCustomRequestObjects(theCIAMHttpRequestObjects);
		customHttpRequest.setReturnType(new ParameterizedTypeReference<CIAMRiskResEntity>() {});
		return customHttpRequest;
	}
	

}
