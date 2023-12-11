package com.example.fiservapp.ciam.utils;

import java.io.File;

import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {
private JsonUtil() {
		
	}
	
	public static <T> T readJSONObject(String fileName, Class<T> requestedJavaClass) {
		T requestedJavaObj = null;
		/* File jsonFile = new File("src/main/resources/" + fileName); */
		try {
			File jsonFile = ResourceUtils.getFile("classpath:users.json"); 
			ObjectMapper theObjectMapper = new ObjectMapper();
			theObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			theObjectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
			requestedJavaObj =theObjectMapper.readValue(jsonFile, requestedJavaClass);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		return requestedJavaObj;
	}
}
