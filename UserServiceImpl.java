package com.example.fiservapp.service;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.example.fiservapp.ciam.service.CIAMRiskEvaluationService;
import com.example.fiservapp.ciam.service.CIAMTokenService;
import com.example.fiservapp.ciam.service.RestTemplateService;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequest;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestObjects;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestParams;
import com.example.fiservapp.config.UserListService;
import com.example.fiservapp.controller.FIDOAuthenticateDeviceException;
import com.example.fiservapp.controller.FIDORegisterDeviceException;
import com.example.fiservapp.model.AuthResEntity;
import com.example.fiservapp.model.CIAMRiskReqEntity;
import com.example.fiservapp.model.CIAMRiskResEntity;
import com.example.fiservapp.model.FIDO2RegisteredReq;
import com.example.fiservapp.model.IsFIDORegisteredResEntity;
import com.example.fiservapp.model.LoginRequest;
import com.example.fiservapp.model.User;
import com.example.fiservapp.model.Users;
import com.example.fiservapp.model.fido.ActivateFIDO;
import com.example.fiservapp.model.fido.ActivateFIDOResp;
import com.example.fiservapp.model.fido.AuthenticateFDCIAMResp;
import com.example.fiservapp.model.fido.AuthenticateFDReq;
import com.example.fiservapp.model.fido.AuthenticateFDResp;
import com.example.fiservapp.model.fido.InitiateFDAuthenticationCIAMResp;
import com.example.fiservapp.model.fido.InitiateFDAuthenticationReq;
import com.example.fiservapp.model.fido.InitiateFDAuthenticationResp;
import com.example.fiservapp.model.fido.RegisterFIDO;
import com.example.fiservapp.model.fido.RegisterFIDOResp;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private static final String FIDO2 = "FIDO2";

	private static final String ACTIVE = "ACTIVE";

	private final UserListService userList;

	@Autowired
	private CIAMRiskEvaluationService ciamRiskEvaluationService;

	@Autowired
	private CIAMTokenService ciamTokenGenerationService;

	@Autowired
	private HttpServletRequest httpSevletRequest;

	@Autowired
	FIDO2ServiceImpl fidoService;

	@Autowired
	private RestTemplateService restTemplate;

	@Autowired
	CIAMTokenService tokenService;

	public UserServiceImpl() {
		userList = new UserListService();
	}

	@Value("${ciam.mfa.base.url}")
	private String BASE_URL;

	@Value("${ciam.v2.is.fido2.registered.path}")
	private String FIDO2_REGISTERED_PATH;

	@Override
	public Users addUser(User user) {
		try {
			File jsonFile = ResourceUtils.getFile("classpath:users.json");
			ObjectMapper theObjectMapper = new ObjectMapper();
			theObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			theObjectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
			userList.getUsers().getUserslist().add(user);
			theObjectMapper.writeValue(jsonFile, userList.getUsers());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new FIDORegisterDeviceException(e.getMessage());
		}
		return userList.getUsers();
	}

	@Override
	public Users deleteUser(String userName) {
		try {
			File jsonFile = ResourceUtils.getFile("classpath:users.json");
			ObjectMapper theObjectMapper = new ObjectMapper();
			theObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			theObjectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
			if (!userList.isUserExist(userName)) {
				throw new FIDORegisterDeviceException("User doesnt exist");
			}
			User user = userList.getuserbyUsername(userName).get();
			userList.getUsers().getUserslist().remove(user);
			theObjectMapper.writeValue(jsonFile, userList.getUsers());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new FIDORegisterDeviceException(e.getMessage());
		}
		return userList.getUsers();
	}

	public Users updateUser(User user) {
		try {
			File jsonFile = ResourceUtils.getFile("classpath:users.json");
			ObjectMapper theObjectMapper = new ObjectMapper();
			theObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			theObjectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
			if (!userList.isUserExist(user.getUsername())) {
				throw new FIDORegisterDeviceException("User doesnt exist");
			}
			User userObj = userList.getuserbyUsername(user.getUsername()).get();
			userObj.setPassword(user.getPassword());
			userObj.setDeviceType(user.getDeviceType());
			userObj.setEmail(user.getEmail());
			userObj.setMobileNo(user.getMobileNo());
			userObj.setState(user.getState());
			theObjectMapper.writeValue(jsonFile, userList.getUsers());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new FIDORegisterDeviceException(e.getMessage());
		}
		return userList.getUsers();
	}

	@Override
	public AuthResEntity validateUser(LoginRequest loginRequest) {
		loginRequest.setIp(httpSevletRequest.getRemoteAddr());
		AuthResEntity response = new AuthResEntity();
		List<User> users = userList.getUsers().getUserslist();
		Predicate<User> theUsersPredicate = user -> user.getUsername().equalsIgnoreCase(loginRequest.getUsername())
				&& user.getPassword().equalsIgnoreCase(loginRequest.getPassword());
		Optional<User> userOptionObj = users.stream().filter(theUsersPredicate).findFirst();
		if (userOptionObj.isPresent()) {
			String accessToken = ciamTokenGenerationService.getRiskAccessToken();
			CIAMRiskReqEntity riskRequest = protectRequestCreation(loginRequest);
			CIAMRiskResEntity riskEvalResponse = ciamRiskEvaluationService.createRiskEvaluation(accessToken,
					riskRequest);
			response.setRiskLevel(riskEvalResponse.getRiskLevel());
			// response.setRiskLevel("MEDIUM");
			response.setStatus("SUCCESS");
			response.setMessage("First Factor Suuccessfull");
		} else {
			response.setStatus("FAILURE");
			response.setMessage("First Factor Unsuccessfull");
		}
		return response;
	}

	public boolean isFIDO2Registered(FIDO2RegisteredReq user) {
		boolean response = false;
		IsFIDORegisteredResEntity resEntity = null;
		String accessToken = tokenService.getAccessToken();
		CIAMHttpRequest<IsFIDORegisteredResEntity> httpRequest = isFIDO2UserRequest(accessToken, user);
		ResponseEntity<IsFIDORegisteredResEntity> responseEntity = restTemplate.execute(httpRequest);
		if (Objects.nonNull(responseEntity) && Objects.nonNull(responseEntity.getBody())) {
			resEntity = responseEntity.getBody();
			if (resEntity.getStatus().contains("SUCCESS")) {
				response = true;
			} else if (resEntity.getStatus().contains("FAILURE")) {
				response = false;
			}
		}
		return response;
	}

	@Override
	public RegisterFIDOResp registerFIDODevice(RegisterFIDO registerFIDO) {
		// get access_token
		String token = tokenService.getAccessToken();
		// read request parameter
		String rpID = registerFIDO.getRpID();
		String rpName = registerFIDO.getRpName();
		String username = "";
		String email = "";
		Optional<User> user = userList.getuserbyUsername(registerFIDO.getUsername());
		if (user.isPresent()) {
			username = registerFIDO.getUsername();
			User requestedUser = user.get();
			email = requestedUser.getEmail();
		}
		// call register service
		RegisterFIDOResp registeredDevice = fidoService.registerFIDODevice(token, username, rpID, rpName, email);
		return registeredDevice;
	}

	@Override
	public ActivateFIDOResp activateFIDODevice(ActivateFIDO activateFIDO) {
		String token = tokenService.getAccessToken();
		String username = activateFIDO.getUsername();
		String authId = activateFIDO.getAuthId();
		String origin = activateFIDO.getOrigin();
		String attestation = activateFIDO.getAttestation();
		ActivateFIDOResp activateFIDOResp = fidoService.activateFIDODevice(username, attestation, authId, origin,
				token);
		return activateFIDOResp;
	}

	private CIAMHttpRequest<IsFIDORegisteredResEntity> isFIDO2UserRequest(String accessToken,
			FIDO2RegisteredReq request) {
		CIAMHttpRequest<IsFIDORegisteredResEntity> theCIAMHttpRequest = new CIAMHttpRequest<>();
		CIAMHttpRequestObjects theCIAMHttpRequestObjects = new CIAMHttpRequestObjects();
		theCIAMHttpRequestObjects.setUri(BASE_URL);
		theCIAMHttpRequestObjects.setPath(FIDO2_REGISTERED_PATH);
		CIAMHttpRequestParams theCustomRequestParams = new CIAMHttpRequestParams();
		HttpHeaders headerParams = new HttpHeaders();
		headerParams.add("Authorization", "Bearer " + accessToken);
		theCustomRequestParams.setHeaderParams(headerParams);
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("username", request.getUsername());
		pathParams.put("rp", request.getRp());
		theCustomRequestParams.setPathParams(pathParams);
		theCIAMHttpRequest.setCustomRequestParams(theCustomRequestParams);
		theCIAMHttpRequestObjects.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		theCIAMHttpRequestObjects.setContentType(MediaType.APPLICATION_JSON);
		theCIAMHttpRequestObjects.setMethod(HttpMethod.GET);
		;
		theCIAMHttpRequest.setCustomRequestObjects(theCIAMHttpRequestObjects);
		theCIAMHttpRequest.setReturnType(new ParameterizedTypeReference<IsFIDORegisteredResEntity>() {
		});
		return theCIAMHttpRequest;
	}

	@Override
	public InitiateFDAuthenticationResp initiateFIDOAuthentication(InitiateFDAuthenticationReq request) {
		InitiateFDAuthenticationResp response = null;
		Optional<User> user = userList.getuserbyUsername(request.getUserName());
		if (user.isPresent()) {
			User requestedUser = user.get();
			if (requestedUser.getState().contains(ACTIVE)) {
				// Obtain token
				String token = tokenService.getAccessToken();

				// Read request params
				String userName = request.getUserName();
				String rpID = request.getRpID();
				String deviceType = FIDO2;

				// CIAM MFA service call
				InitiateFDAuthenticationCIAMResp initiateAuth = fidoService.initiateFIDOAuthentication(userName, rpID,
						deviceType, token);
				// Response
				response = new InitiateFDAuthenticationResp(initiateAuth.getAuthId(),
						initiateAuth.getPublicKeyCredentialRequestOptions(), initiateAuth.getStatus());
			} else {
				throw new FIDOAuthenticateDeviceException("User is Disable");
			}
		}
		return response;
	}

	@Override
	public InitiateFDAuthenticationResp initiateFIDOAuthNoUserName(InitiateFDAuthenticationReq request) {
		InitiateFDAuthenticationResp response = null;
		if (Objects.nonNull(request.getRpID())) {
			// Obtain Token to get into CIAM MFA API call
			String token = tokenService.getAccessToken();
			// Read Request Params from UI
			String rpID = request.getRpID();
			// CIAM MFA service call which gives CIAM Response
			InitiateFDAuthenticationCIAMResp initiateAuthNoUsername = fidoService.initiateFIDOAuthentication(null, rpID,
					FIDO2, token);
			// Response to UI
			response = new InitiateFDAuthenticationResp(initiateAuthNoUsername.getAuthId(),
					initiateAuthNoUsername.getPublicKeyCredentialRequestOptions(), initiateAuthNoUsername.getStatus());
		} else {
			throw new FIDOAuthenticateDeviceException("RpID is Null Please Enter Valid RpID");
		}
		return response;
	}

	@Override
	public AuthenticateFDResp authenticateFIDODevice(AuthenticateFDReq request) {
		// Obtain token
		String token = tokenService.getAccessToken();

		// Read request params
		String origin = request.getOrigin();
		String authID = request.getAuthID();
		String assersion = request.getAssertion();
		String deviceType = FIDO2;

		// CIAM MFA service call
		AuthenticateFDCIAMResp authenticateFidoDevice = fidoService.authenticateFIDO(origin, authID, assersion,
				deviceType, token);

		// Response
		AuthenticateFDResp response = new AuthenticateFDResp();
		response.setStatus(authenticateFidoDevice.getStatus());
		response.setMessage(authenticateFidoDevice.getMessage());
		response.setEmail(authenticateFidoDevice.getEmail());
		response.setUsername(authenticateFidoDevice.getUsername());
		return response;
	}

	/**
	 * Prepare Ping Protect Risk Evaluation Request
	 * 
	 * @param loginRequest
	 * @return
	 */
	private CIAMRiskReqEntity protectRequestCreation(LoginRequest loginRequest) {
		CIAMRiskReqEntity protectrequest = new CIAMRiskReqEntity();
		protectrequest.setAppId("Passwordless Web App");
		protectrequest.setIpConfig(loginRequest.getIp());
		protectrequest.setUserId(loginRequest.getUsername());
		protectrequest.setBrowserUserAgent(loginRequest.getUserAgent());
		protectrequest.setSignalData(loginRequest.getSdkdata());
		return protectrequest;
	}

}
