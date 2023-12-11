package com.example.fiservapp.config;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.fiservapp.ciam.utils.JsonUtil;
import com.example.fiservapp.model.User;
import com.example.fiservapp.model.Users;

import lombok.Data;

@Data
@Service
public class UserListService {

	private Users users;
	public UserListService() {
		users = JsonUtil.readJSONObject("users.json", Users.class);
	}
	
	public Optional<User> getuserbyUsername(String username) {
		return users.getUserslist().stream().filter(users -> users.getUsername().equalsIgnoreCase(username)).findFirst();
	}
	
	public boolean isUserExist(String username) {
		return users.getUserslist().stream().anyMatch(users -> users.getUsername().equalsIgnoreCase(username));
	}
}
