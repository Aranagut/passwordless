package com.example.fiservapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.fiservapp.config.EndpointConstants;
import com.example.fiservapp.model.User;
import com.example.fiservapp.model.Users;
import com.example.fiservapp.service.UserServiceImpl;

@Controller
public class UserController {
	
	@Autowired
    UserServiceImpl userService;
	
	/**
	 * Request User Registration
	 * 
	 * @param User
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@PutMapping(EndpointConstants.ADD_USER)
	@ResponseBody
	public Users addUser(@RequestBody User user) {
		return userService.addUser(user);
	}
	
	/**
	 * Request User Registration
	 * 
	 * @param User
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@PatchMapping(EndpointConstants.UPDATE_USER)
	@ResponseBody
	public Users updateUser(@RequestBody User user) {
		return userService.updateUser(user);
	}
	
	/**
	 * Delete User 
	 * 
	 * @param Username
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@DeleteMapping(EndpointConstants.DELETE_USER)
	@ResponseBody
	public Users deleteUser(@PathVariable String username) {
		return userService.deleteUser(username);
	}
}
