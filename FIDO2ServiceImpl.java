package com.example.fiservapp.service;

import com.example.fiservapp.controller.FIDOAuthenticateDeviceException;
import com.example.fiservapp.model.Rp;
import com.example.fiservapp.model.fido.*;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FIDO2ServiceImpl implements FIDO2Service {

    @Value("${ciam.mfa.base.url}")
    private String BASE_URL;

    @Autowired
    private RestTemplate restTemplate;

    public FIDO2ServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Register FIDO device - 1st step of FIDO device registration.
     * this will initiate device registration process for FIDO device.<br>
     * user will register his device against relying party domain.e.g.<b>sbi.com</b><br>
     * Execution - build HTTP Request, header and call CIAM MFA endpoint url.<br>
     * Endpoint: <b>{{ciam_mfa_url}}/ciam-mfa/v2/users/{{username}}/mfadevices
     *
     * @param accessToken
     * @param username
     * @param rpID
     * @param rpName
     * @return
     */
    @Override
    public RegisterFIDOResp registerFIDODevice(String accessToken, String username, String rpID, String rpName, String email) {
        // HttpHeader
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // HttpBody
        String deviceType = "FIDO2";
        Rp rp = new Rp(rpID, rpName);
        RegisterFIDOReqEntity registerFidoReqEntity = new RegisterFIDOReqEntity(deviceType, rp, email, null);
        HttpEntity<RegisterFIDOReqEntity> requestBody = new HttpEntity<RegisterFIDOReqEntity>(registerFidoReqEntity, headers);

        // URL
        String url = BASE_URL + "/users/" + username + "/mfadevices";
       
        // API Call
        try {
            ResponseEntity<RegisterFIDOResp> response = restTemplate.postForEntity(url, requestBody, RegisterFIDOResp.class);
            return response.getBody();
        } catch (Exception e) {
        	throw new FIDOAuthenticateDeviceException(e.getMessage());
        }
    }

    /**
     * Activate FIDO device - last step of FIDO device registration.
     * Once device activated user device registration process is completed.<br>
     * Execution - build HTTP Request, header and call CIAM MFA endpoint url.<br>
     * Endpoint: <b>{{ciam_mfa_url}}/ciam-mfa/v2/users/{{username}}/mfadevices/{{authId}}
     *
     * @param username
     * @param attestation
     * @param authId
     * @param origin
     * @param token
     * @return
     */
    @Override
    public ActivateFIDOResp activateFIDODevice(String username, String attestation, String authId, String origin, String token) {
        // HttpHeader
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // HttpBody
        ActivateFIDOReqEntity activateFIDOReqEntity = new ActivateFIDOReqEntity();
        activateFIDOReqEntity.setOrigin(origin);
        activateFIDOReqEntity.setAttestation(attestation);
        activateFIDOReqEntity.setDeviceType("FIDO2");
        
        
        HttpEntity<ActivateFIDOReqEntity> requestBody = new HttpEntity<>(activateFIDOReqEntity, headers);

        // URL
        String Url = BASE_URL + "/users/" + username + "/mfadevices/" + authId;

        // API Call
        try {
            ResponseEntity<ActivateFIDOResp> response = restTemplate.postForEntity(Url, requestBody, ActivateFIDOResp.class);
            return response.getBody();
        } catch (Exception e) {
        	throw new FIDOAuthenticateDeviceException(e.getMessage());
        }
    }

    public InitiateFDAuthenticationCIAMResp initiateFIDOAuthentication(String userName, String rpID, String deviceType, String token) {
        // HttpHeader
        HttpHeaders httpsHeader = new HttpHeaders();
        httpsHeader.setContentType(MediaType.APPLICATION_JSON);
        httpsHeader.setBearerAuth(token);

        // HttpBody
        InitiateFDAuthenticationCIAMReq httpRequest = new InitiateFDAuthenticationCIAMReq(deviceType, userName, rpID);
        HttpEntity<InitiateFDAuthenticationCIAMReq> requestBody = new HttpEntity<>(httpRequest, httpsHeader);

        // URL
        String url = BASE_URL + "/deviceAuthentications";

        try {
            ResponseEntity<InitiateFDAuthenticationCIAMResp> response = restTemplate.postForEntity(url, requestBody, InitiateFDAuthenticationCIAMResp.class);
            return response.getBody();
        } catch (Exception e) {
        	throw new FIDOAuthenticateDeviceException(e.getMessage());
        }
    }

    public AuthenticateFDCIAMResp authenticateFIDO(String origin, String authId, String assertion, String deviceType, String token) {

        // HttpHeader
        HttpHeaders HttpHeader = new HttpHeaders();
        HttpHeader.setContentType(MediaType.APPLICATION_JSON);
        HttpHeader.setBearerAuth(token);

        // HttpBody
        AuthenticateFDCIAMReq httpBody = new AuthenticateFDCIAMReq();
        httpBody.setAssertion(assertion);
        httpBody.setOrigin(origin);
        httpBody.setDeviceType(deviceType);
        HttpEntity<AuthenticateFDCIAMReq> requestBody = new HttpEntity<>(httpBody, HttpHeader);

        // url
        String URL = BASE_URL + "/deviceAuthentications/" + authId;

        try {
            ResponseEntity<AuthenticateFDCIAMResp> response = restTemplate.postForEntity(URL, requestBody, AuthenticateFDCIAMResp.class);
            return response.getBody();
        } catch (Exception e) {
            throw new FIDOAuthenticateDeviceException(e.getMessage());
        }
    }
}