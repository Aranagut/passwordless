package com.example.fiservapp.ciam.service;

import java.util.Collections;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequest;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestObjects;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestParams;
import com.example.fiservapp.ciam.utils.AesGcmEncryptorDecryptor;
import com.example.fiservapp.ciam.model.TokenGenerationResponse;

@Service
public class CIAMTokenService {

	@Autowired
	private RestTemplateService restTemplateService;

	/**
	 * ADMIN access token generated using parameters configured in properties. The
	 * Arguments other than return type specified are null and are not used to
	 * generate token
	 */
	
	@Value("${ciam.mfa.base.url}")
	private String BASE_URL;
	@Value("${ciam.mfa.service.account.id}")
	private String SVC_ACCOUNT;
	@Value("${ciam.mfa.service.account.sct}")
	private String SVC_SECRET;
	@Value("${ciam.risk.service.account.id}")
	private String RISK_SVC_ACCOUNT;
	@Value("${ciam.risk.service.account.sct}")
	private String RISK_SVC_SECRET;
	@Value("${ciam.service.encryption.key}")
	private String ENCRYPT_KEY;
	@Value("${ciam.v2.token.path}")
	private String TOKEN_PATH;
	
	public String getAccessToken() {
		String response = null;
		CIAMHttpRequest<TokenGenerationResponse> httpRequest = this.buildCIAMTokenHttpRequest(SVC_ACCOUNT, SVC_SECRET);
		ResponseEntity<TokenGenerationResponse> responseEntity = restTemplateService.execute(httpRequest);
		if(Objects.nonNull(responseEntity) && Objects.nonNull(responseEntity.getBody())) {
			response = responseEntity.getBody().getAccess_token();
		}
		return response;
	}
	
	public String getRiskAccessToken() {
		String response = null;
		CIAMHttpRequest<TokenGenerationResponse> httpRequest = this.buildCIAMTokenHttpRequest(RISK_SVC_ACCOUNT, RISK_SVC_SECRET);
		ResponseEntity<TokenGenerationResponse> responseEntity = restTemplateService.execute(httpRequest);
		if(Objects.nonNull(responseEntity) && Objects.nonNull(responseEntity.getBody())) {
			response = responseEntity.getBody().getAccess_token();
		}
		return response;
	}

	private CIAMHttpRequest<TokenGenerationResponse> buildCIAMTokenHttpRequest(String svcAccount , String svcSecret) {
		CIAMHttpRequest<TokenGenerationResponse> theCIAMHttpRequest = new CIAMHttpRequest<>();
		CIAMHttpRequestObjects theCIAMHttpRequestObjects = new CIAMHttpRequestObjects();
		theCIAMHttpRequestObjects.setUri(BASE_URL);
		theCIAMHttpRequestObjects.setPath(TOKEN_PATH); 
		theCIAMHttpRequestObjects.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		theCIAMHttpRequestObjects.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		theCIAMHttpRequestObjects.setMethod(HttpMethod.POST);
		theCIAMHttpRequest.setCustomRequestObjects(theCIAMHttpRequestObjects);
		CIAMHttpRequestParams theCustomRequestParams = new CIAMHttpRequestParams();
		LinkedMultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();
		formParams.add("grant_type", "client_credentials");
		formParams.add("client_id",svcAccount);
		formParams.add("client_secret",AesGcmEncryptorDecryptor.decrypt(svcSecret, ENCRYPT_KEY));
		theCustomRequestParams.setFormParams(formParams);
		theCIAMHttpRequest.setCustomRequestParams(theCustomRequestParams);
		theCIAMHttpRequest.setReturnType(new ParameterizedTypeReference<TokenGenerationResponse>() {
		});
		return theCIAMHttpRequest;
	}
}
