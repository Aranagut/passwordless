package com.example.fiservapp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Users {
	@JsonProperty("users")
	private List<User> userslist;
}
