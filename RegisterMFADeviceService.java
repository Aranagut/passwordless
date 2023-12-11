package com.example.fiservapp.ciam.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.example.fiservapp.ciam.shared.model.CIAMHttpRequest;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestObjects;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestParams;
import com.example.fiservapp.config.UserListService;
import com.example.fiservapp.controller.FIDOAuthenticateDeviceException;
import com.example.fiservapp.model.AuthResEntity;
import com.example.fiservapp.model.AuthenticateRequest;
import com.example.fiservapp.model.CreateDeviceResEntity;
import com.example.fiservapp.model.CreateMFADeviceReqEntity;
import com.example.fiservapp.model.IsFIDORegisteredResEntity;
import com.example.fiservapp.model.OTPValidationReqEntity;
import com.example.fiservapp.model.User;

@Service
public class RegisterMFADeviceService {

	private final UserListService userList;

	@Autowired
	public RegisterMFADeviceService(UserListService userList) {
		this.userList = userList;
	}

	@Autowired
	private RestTemplateService restTemplate;

	@Autowired
	private CIAMTokenService ciamTokenServices;

	@Value("${ciam.mfa.base.url}")
	private String BASE_URL;

	@Value("${ciam.v2.mfa.create.device.path}")
	private String TOTP_DEVICE_AUTH_PATH;

	@Value("${ciam.v2.mfa.unpair.device.path}")
	private String FIDO_UNPAIR_DEVICE_PATH;

	/**
	 * This function will Create the MFA Device for TOTP based on User Inputs. In
	 * this method we are reusing the OTPRequest Model to take inputs from User.
	 * Execution - Request Creation, generation of accessToken and API Call.
	 * 
	 * @param userName and DeviceType
	 * @return
	 */
	public CreateDeviceResEntity createMFADevice(String userName, String deviceType, String deviceName) {
		CreateDeviceResEntity response = null;
		CreateMFADeviceReqEntity request = createDeviceRequestBody(userName, deviceType, deviceName);
		String accessToken = ciamTokenServices.getAccessToken();
		CIAMHttpRequest<CreateDeviceResEntity> httpRequest = createDevice(accessToken, request, userName);
		try {
			ResponseEntity<CreateDeviceResEntity> responseEntity = restTemplate.execute(httpRequest);
			if (Objects.nonNull(responseEntity) && Objects.nonNull(responseEntity.getBody())) {
				response = responseEntity.getBody();
			}
		} catch (Exception ex) {
			throw new FIDOAuthenticateDeviceException(ex.getMessage());
		}
		return response;
	}

	/**
	 * This function will activate the created MFA Device for TOTP based on User
	 * Inputs. In this method we are reusing the OTPValidateReq Model to take inputs
	 * from User because this call need same inputs that we need to authenticate the
	 * OTP. Execution - Request Creation, generation of accessToken and API Call.
	 * 
	 * @param OTP, AuthID, UserName and DevieType
	 * @return
	 */
	public AuthResEntity activateMFADevice(AuthenticateRequest deviceActivateRequest) {
		AuthResEntity response = null;
		String accessToken = ciamTokenServices.getAccessToken();
		OTPValidationReqEntity requestBody = requestBodyCreation(deviceActivateRequest.getOtp(),
				deviceActivateRequest.getUser_name());
		CIAMHttpRequest<AuthResEntity> httpRequest = deviceActivationRequest(requestBody,
				deviceActivateRequest.getAuthID(), deviceActivateRequest.getUser_name(), accessToken);
		try {
			ResponseEntity<AuthResEntity> responseEntity = restTemplate.execute(httpRequest);
			if (Objects.nonNull(responseEntity) && Objects.nonNull(responseEntity.getBody())) {
				response = responseEntity.getBody();
			}
		} catch (Exception ex) {
			throw new FIDOAuthenticateDeviceException(ex.getMessage());
		}
		return response;

	}

	/**
	 * This function will retrieve all the registered MFA Device for that User.
	 * Execution - Generation of accessToken and API Call.
	 * 
	 * @param UserName
	 * @return List of Registered devices.
	 */
	public List<CreateDeviceResEntity> getMFADevice(String username) {
		List<CreateDeviceResEntity> response = null;
		String accessToken = ciamTokenServices.getAccessToken();
		CIAMHttpRequest<List<CreateDeviceResEntity>> httpRequest = retriveMFADeviceRequest(username, accessToken);
		try {
			ResponseEntity<List<CreateDeviceResEntity>> responseEntity = restTemplate.execute(httpRequest);
			if (Objects.nonNull(responseEntity) && Objects.nonNull(responseEntity.getBody())) {
				response = responseEntity.getBody();
			}
		} catch (Exception ex) {
			throw new FIDOAuthenticateDeviceException(ex.getMessage());
		}
		return response;

	}

	/**
	 * Creation of HttpRequest to register the device.
	 * 
	 * @param accessToken, userName, DeviceType and DeviceName
	 * @return
	 */
	private CIAMHttpRequest<CreateDeviceResEntity> createDevice(String accessToken, CreateMFADeviceReqEntity request,
			String userName) {
		CIAMHttpRequest<CreateDeviceResEntity> theCIAMHttpRequest = new CIAMHttpRequest<>();
		CIAMHttpRequestObjects theCIAMHttpRequestObjects = new CIAMHttpRequestObjects();
		theCIAMHttpRequestObjects.setUri(BASE_URL);
		theCIAMHttpRequestObjects.setPath(TOTP_DEVICE_AUTH_PATH);
		CIAMHttpRequestParams theCustomRequestParams = new CIAMHttpRequestParams();
		HttpHeaders headerParams = new HttpHeaders();
		headerParams.add("Authorization", "Bearer " + accessToken);
		theCustomRequestParams.setHeaderParams(headerParams);
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("user_name", userName);
		theCustomRequestParams.setPathParams(pathParams);
		theCIAMHttpRequest.setCustomRequestParams(theCustomRequestParams);
		theCIAMHttpRequestObjects.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		theCIAMHttpRequestObjects.setContentType(MediaType.APPLICATION_JSON);
		theCIAMHttpRequestObjects.setMethod(HttpMethod.POST);
		theCIAMHttpRequestObjects.setBody(request);
		theCIAMHttpRequest.setCustomRequestObjects(theCIAMHttpRequestObjects);
		theCIAMHttpRequest.setReturnType(new ParameterizedTypeReference<CreateDeviceResEntity>() {
		});
		return theCIAMHttpRequest;
	}

	/**
	 * Creation of HttpRequest to Activate the registered device.
	 * 
	 * @param accessToken, userName, AuthID, OTP and DeviceType
	 * @return
	 */
	private CIAMHttpRequest<AuthResEntity> deviceActivationRequest(OTPValidationReqEntity request, String authID,
			String userName, String accessToken) {
		CIAMHttpRequest<AuthResEntity> theCIAMCiamHttpRequest = new CIAMHttpRequest<>();
		CIAMHttpRequestObjects theCIACiamHttpRequestObjects = new CIAMHttpRequestObjects();
		theCIACiamHttpRequestObjects.setUri(BASE_URL);
		theCIACiamHttpRequestObjects.setPath(TOTP_DEVICE_AUTH_PATH + "/" + authID);
		CIAMHttpRequestParams theCiamHttpRequestParams = new CIAMHttpRequestParams();
		HttpHeaders headerparams = new HttpHeaders();
		headerparams.add("Authorization", "Bearer " + accessToken);
		theCiamHttpRequestParams.setHeaderParams(headerparams);
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("user_name", userName);
		theCiamHttpRequestParams.setPathParams(pathParams);
		theCIAMCiamHttpRequest.setCustomRequestParams(theCiamHttpRequestParams);
		theCIACiamHttpRequestObjects.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		theCIACiamHttpRequestObjects.setContentType(MediaType.APPLICATION_JSON);
		theCIACiamHttpRequestObjects.setMethod(HttpMethod.POST);
		theCIACiamHttpRequestObjects.setBody(request);
		theCIAMCiamHttpRequest.setCustomRequestObjects(theCIACiamHttpRequestObjects);
		theCIAMCiamHttpRequest.setReturnType(new ParameterizedTypeReference<AuthResEntity>() {
		});
		return theCIAMCiamHttpRequest;
	}

	/**
	 * Creation of HttpRequest to retrieve all the registered device.
	 * 
	 * @param accessToken and UsreName.
	 * @return
	 */
	private CIAMHttpRequest<List<CreateDeviceResEntity>> retriveMFADeviceRequest(String userName, String accessToken) {
		CIAMHttpRequest<List<CreateDeviceResEntity>> theCIAMCiamHttpRequest = new CIAMHttpRequest<>();
		CIAMHttpRequestObjects theCIACiamHttpRequestObjects = new CIAMHttpRequestObjects();
		theCIACiamHttpRequestObjects.setUri(BASE_URL);
		theCIACiamHttpRequestObjects.setPath(TOTP_DEVICE_AUTH_PATH);
		CIAMHttpRequestParams theCiamHttpRequestParams = new CIAMHttpRequestParams();
		HttpHeaders headerparams = new HttpHeaders();
		headerparams.add("Authorization", "Bearer " + accessToken);
		theCiamHttpRequestParams.setHeaderParams(headerparams);
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("user_name", userName);
		theCiamHttpRequestParams.setPathParams(pathParams);
		theCIAMCiamHttpRequest.setCustomRequestParams(theCiamHttpRequestParams);
		theCIACiamHttpRequestObjects.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		theCIACiamHttpRequestObjects.setContentType(MediaType.APPLICATION_JSON);
		theCIACiamHttpRequestObjects.setMethod(HttpMethod.GET);
		theCIAMCiamHttpRequest.setCustomRequestObjects(theCIACiamHttpRequestObjects);
		theCIAMCiamHttpRequest.setReturnType(new ParameterizedTypeReference<List<CreateDeviceResEntity>>() {
		});
		return theCIAMCiamHttpRequest;
	}

	/**
	 * Creation of HttpRequestObject Body to Create Device
	 * 
	 * @param userName, deviceType and deviceName
	 * @return
	 */
	private CreateMFADeviceReqEntity createDeviceRequestBody(String userName, String deviceType, String deviceName) {
		CreateMFADeviceReqEntity request = new CreateMFADeviceReqEntity();
		request.setDeviceType(deviceType);
		request.setDevicename(deviceName);
		return request;
	}

	/**
	 * Creation of HttpRequestObject Body to Validate OTP and activate the MFA
	 * device
	 * 
	 * @param OTP and deviceType
	 * @return
	 */
	private OTPValidationReqEntity requestBodyCreation(String otp, String userName) {
		OTPValidationReqEntity request = new OTPValidationReqEntity();
		Optional<User> user = userList.getuserbyUsername(userName);
		if (user.isPresent()) {
			User requestedUser = user.get();
			request.setDeviceType(requestedUser.getDeviceType());
		}
		request.setOtp(otp);
		return request;
	}

	/**
	 * 
	 * Creation of HttpRequest to Unpair the registered FIDO device.
	 *
	 * 
	 * 
	 * @param accessToken, userName and DeviceType
	 * 
	 * @return null
	 * 
	 */

	public IsFIDORegisteredResEntity unpairDevice(String userName) {
		IsFIDORegisteredResEntity response = new IsFIDORegisteredResEntity();
		String accessToken = ciamTokenServices.getAccessToken();
		CIAMHttpRequest<Void> request = deleteDeviceRequest(accessToken, userName);
		try {
			ResponseEntity<Void> responseEntity = restTemplate.execute(request);
				if (responseEntity.getStatusCode().value() == HttpStatus.SC_NO_CONTENT) {
					response.setMessage("All regisetred Devices Unpaired Successfully");
					response.setStatus("SUCCESS");
				}
		} catch (Exception ex) {
			throw new FIDOAuthenticateDeviceException(ex.getMessage());
		}
		return response;
	}

	private CIAMHttpRequest<Void> deleteDeviceRequest(String accessToken, String userName) {
		CIAMHttpRequest<Void> theCIAMHttpRequest = new CIAMHttpRequest<>();
		CIAMHttpRequestObjects theCiamHttpRequestObjects = new CIAMHttpRequestObjects();
		theCiamHttpRequestObjects.setUri(BASE_URL);
		theCiamHttpRequestObjects.setPath(FIDO_UNPAIR_DEVICE_PATH);
		CIAMHttpRequestParams theCiamHttpRequestParams = new CIAMHttpRequestParams();
		HttpHeaders headerparams = new HttpHeaders();
		headerparams.add("Authorization", "Bearer " + accessToken);
		theCiamHttpRequestParams.setHeaderParams(headerparams);
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("userName", userName);
		pathParams.put("deviceType", "FIDO2");
		pathParams.put("deviceName", "ALL");
		theCiamHttpRequestParams.setPathParams(pathParams);
		theCIAMHttpRequest.setCustomRequestParams(theCiamHttpRequestParams);
		theCiamHttpRequestObjects.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		theCiamHttpRequestObjects.setContentType(MediaType.APPLICATION_JSON);
		theCiamHttpRequestObjects.setMethod(HttpMethod.DELETE);
		theCIAMHttpRequest.setCustomRequestObjects(theCiamHttpRequestObjects);
		theCIAMHttpRequest.setReturnType(new ParameterizedTypeReference<Void>() {
		});
		return theCIAMHttpRequest;
	}
}
