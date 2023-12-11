package com.example.fiservapp.ciam.service;

import com.example.fiservapp.ciam.shared.model.CIAMHttpRequest;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestObjects;
import com.example.fiservapp.ciam.shared.model.CIAMHttpRequestParams;
import com.example.fiservapp.config.UserListService;
import com.example.fiservapp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
public class MFAService {
    private final UserListService userList;

    public MFAService() {
        userList = new UserListService();
    }

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private CIAMTokenService ciamTokenGenerationService;

    @Value("${ciam.mfa.base.url}")
    private String BASE_URL;
    @Value("${ciam.v2.device.authentication.path}")
    private String AUTH_PATH;

    /**
     * This function will initiate authentication by sending OTP to the registered
     * device. This function is common for all devices i.e. SMS, EMAIL , TOTP.
     * Execution - Create request based on device type , obtain accessToken and API
     * Call.
     *
     * @param username and deviceType
     * @return
     */
    public InitiateAuthResEntity initiateAuthentication(InitiateAuthRequest authRequest) {
        InitiateAuthResEntity response = null;
        MFAReqEntity request = requestBodyCreation(authRequest);
        String accessToken = ciamTokenGenerationService.getAccessToken();
        CIAMHttpRequest<InitiateAuthResEntity> httpRequest = mfaOTPRequest(accessToken, request);
        ResponseEntity<InitiateAuthResEntity> responseEntity = restTemplateService.execute(httpRequest);
        if (Objects.nonNull(responseEntity) && Objects.nonNull(responseEntity.getBody())) {
            response = responseEntity.getBody();
            response.setMessage(resposneMessageCreation(authRequest.getUsername()));
        }
        return response;
    }

    /**
     * This function will validate the OTP received by user from UI . This function
     * is common for all devices i.e. SMS, EMAIL , TOTP. Execution - Create request
     * based on device type , obtain accessToken and API Call.
     *
     * @param otp, authID and deviceType
     * @return
     */
    public AuthResEntity authenticateUser(String otp, String authID, String userName) {
        AuthResEntity response = null;
        String accessToken = ciamTokenGenerationService.getAccessToken();
        OTPValidationReqEntity requestBody = requestBodyCreation(otp, userName);
        CIAMHttpRequest<AuthResEntity> httpRequest = otpValidationrequest(requestBody, authID, accessToken);
        ResponseEntity<AuthResEntity> responseEntity = restTemplateService.execute(httpRequest);
        if (Objects.nonNull(responseEntity) && Objects.nonNull(responseEntity.getBody())) {
            response = responseEntity.getBody();
        }
        return response;

    }

    /**
     * Creation of HttpRequest to request the OTP to the specific device
     *
     * @param accessToken , request
     * @return
     */
    private CIAMHttpRequest<InitiateAuthResEntity> mfaOTPRequest(String accessToken, MFAReqEntity request) {
        CIAMHttpRequest<InitiateAuthResEntity> theCIAMHttpRequest = new CIAMHttpRequest<>();
        CIAMHttpRequestObjects theCIAMHttpRequestObjects = new CIAMHttpRequestObjects();
        theCIAMHttpRequestObjects.setUri(BASE_URL);
        theCIAMHttpRequestObjects.setPath(AUTH_PATH);
        theCIAMHttpRequestObjects.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        theCIAMHttpRequestObjects.setContentType(MediaType.APPLICATION_JSON);
        theCIAMHttpRequestObjects.setMethod(HttpMethod.POST);
        theCIAMHttpRequestObjects.setBody(request);
        theCIAMHttpRequest.setCustomRequestObjects(theCIAMHttpRequestObjects);
        CIAMHttpRequestParams theCustomRequestParams = new CIAMHttpRequestParams();
        HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("Authorization", "Bearer " + accessToken);
        theCustomRequestParams.setHeaderParams(headerParams);
        theCIAMHttpRequest.setCustomRequestParams(theCustomRequestParams);
        theCIAMHttpRequest.setReturnType(new ParameterizedTypeReference<InitiateAuthResEntity>() {
        });
        return theCIAMHttpRequest;
    }

    /**
     * Creation of HttpRequest to validate the OTP received from user
     *
     * @param accessToken, request, authID
     * @return
     */
    private CIAMHttpRequest<AuthResEntity> otpValidationrequest(OTPValidationReqEntity request, String authID,
                                                                String accessToken) {
        CIAMHttpRequest<AuthResEntity> theCIAMCiamHttpRequest = new CIAMHttpRequest<>();
        CIAMHttpRequestObjects theCIACiamHttpRequestObjects = new CIAMHttpRequestObjects();
        theCIACiamHttpRequestObjects.setUri(BASE_URL);
        theCIACiamHttpRequestObjects.setPath(AUTH_PATH + "/" + authID);
        theCIACiamHttpRequestObjects.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        theCIACiamHttpRequestObjects.setContentType(MediaType.APPLICATION_JSON);
        theCIACiamHttpRequestObjects.setMethod(HttpMethod.POST);
        theCIACiamHttpRequestObjects.setBody(request);
        theCIAMCiamHttpRequest.setCustomRequestObjects(theCIACiamHttpRequestObjects);
        CIAMHttpRequestParams theCiamHttpRequestParams = new CIAMHttpRequestParams();
        HttpHeaders headerparams = new HttpHeaders();
        headerparams.add("Authorization", "Bearer " + accessToken);
        theCiamHttpRequestParams.setHeaderParams(headerparams);
        theCIAMCiamHttpRequest.setCustomRequestParams(theCiamHttpRequestParams);
        theCIAMCiamHttpRequest.setReturnType(new ParameterizedTypeReference<AuthResEntity>() {
        });
        return theCIAMCiamHttpRequest;
    }

    /**
     * Creation of HttpRequestObject Body for Request OTP
     *
     * @param Request form UI contains username and deviceType
     * @return
     */
    private MFAReqEntity requestBodyCreation(InitiateAuthRequest authRequest) {
        MFAReqEntity request = new MFAReqEntity();
        request.setUserName(authRequest.getUsername());
        Optional<User> user = userList.getuserbyUsername(authRequest.getUsername());
        if (user.isPresent()) {
            User requestedUser = user.get();
            if (requestedUser.getDeviceType().equalsIgnoreCase("sms")) {
                request.setPhoneNumber(requestedUser.getMobileNo());
            }
            if (requestedUser.getDeviceType().equalsIgnoreCase("EMAIL")) {
                request.setEmail(requestedUser.getEmail());
            }
            if (requestedUser.getDeviceType().equalsIgnoreCase("TOTP")) {
                request.setDeviceName(requestedUser.getDeviceName());
            }
            request.setDeviceType(requestedUser.getDeviceType());
        }

        return request;
    }

    /**
     * Creation of HttpRequestObject Body to Validate OTP
     *
     * @param Request form UI contains OTP and deviceType
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
     * Creation of Response message to the user to show in UI screen based on the device type.
     *
     * @param username form UI contains Logged in user name.
     * @return response message string
     */
    private String resposneMessageCreation(String username) {
        String message = "";
        String deviceInfo = "";
        Optional<User> user = userList.getuserbyUsername(username);
        if (user.isPresent()) {
            User requestedUser = user.get();
            if (requestedUser.getDeviceType().contains("SMS")) {
                deviceInfo = requestedUser.getMobileNo();
                message = "Please enter the OTP sent on your registered Mobile - " + deviceInfo.replaceAll("\\d(?=\\d{4})", "*");
            } else if (requestedUser.getDeviceType().contains("EMAIL")) {
                deviceInfo = requestedUser.getEmail();
                message = "Please enter the OTP sent on your registered EMAIL - " + deviceInfo.replaceAll("(?<=.{3}).(?=[^@]*?.@)", "*");
            } else if (requestedUser.getDeviceType().contains("TOTP")) {
                deviceInfo = requestedUser.getDeviceName();
                message = "Enter the one-time passcode on your registered device - " + deviceInfo;
            }
            // message = "Enter the one-time passcode on your registered device - " + deviceInfo;
        }
        return message;
    }

}
