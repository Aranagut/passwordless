package com.example.fiservapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.fiservapp.ciam.service.MFAService;
import com.example.fiservapp.ciam.service.RegisterMFADeviceService;
import com.example.fiservapp.config.EndpointConstants;
import com.example.fiservapp.model.AuthResEntity;
import com.example.fiservapp.model.AuthenticateRequest;
import com.example.fiservapp.model.CreateDeviceRequest;
import com.example.fiservapp.model.CreateDeviceResEntity;
import com.example.fiservapp.model.InitiateAuthRequest;
import com.example.fiservapp.model.InitiateAuthResEntity;
import com.example.fiservapp.model.IsFIDORegisteredResEntity;

@Controller
public class MFAController {

	@Autowired
	MFAService mfaService;

	@Autowired
	RegisterMFADeviceService registerDevice;

	/**
	 * Request OTP EndPoint
	 * 
	 * @param InitiateAuthRequest
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@PostMapping(EndpointConstants.REQUEST_OTP)
	@ResponseBody
	public InitiateAuthResEntity initiateAuthentication(@RequestBody InitiateAuthRequest request) {
		return mfaService.initiateAuthentication(request);
	}

	/**
	 * Validate OTP EndPoint
	 * 
	 * @param AuthenticateRequest
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@PostMapping(EndpointConstants.VALIDATE_OTP)
	@ResponseBody
	public AuthResEntity authenticateUser(@RequestBody AuthenticateRequest request) {
		return mfaService.authenticateUser(request.getOtp(), request.getAuthID(), request.getUser_name());
	}

	/**
	 * Create MFA Device EndPoint
	 * 
	 * @param InitiateAuthRequest
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@PostMapping(EndpointConstants.CREATE_MFA_DEVICE)
	@ResponseBody
	public CreateDeviceResEntity createMFADevice(@RequestBody CreateDeviceRequest request) {
		return registerDevice.createMFADevice(request.getUsername(), request.getDeviceType(), request.getDeviceName());
	}

	/**
	 * Activate MFA Device EndPoint
	 * 
	 * @param AuthenticateRequest
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@PostMapping(EndpointConstants.ACTIVATE_MFA_DEVICE)
	@ResponseBody
	public AuthResEntity activateMFADevice(@RequestBody AuthenticateRequest request) {
		return registerDevice.activateMFADevice(request);
	}

	/**
	 * Retrieve all registered MFA device EndPoint
	 * 
	 * @param userName
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@GetMapping(EndpointConstants.GET_MFA_DEVICE)
	@ResponseBody
	public List<CreateDeviceResEntity> mfaDeviceRetrival(@RequestBody String username) {
		return registerDevice.getMFADevice(username);
	}

	/**
	 * Unpair MFADevice (FIDO2) EndPoint
	 * @param userName, deviceType, deviceName
	 * 
	 * @return
	 * 
	 */

	@CrossOrigin(origins = "*")
	@PostMapping(EndpointConstants.UNPAIR_MFA_DEVICE)
	@ResponseBody
	public IsFIDORegisteredResEntity unpairDevice(@RequestBody InitiateAuthRequest requestedUser) {
		return registerDevice.unpairDevice(requestedUser.getUsername());
	}
}
