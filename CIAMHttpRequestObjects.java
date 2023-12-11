package com.example.fiservapp.ciam.shared.model;

import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import lombok.Data;

@Data
public class CIAMHttpRequestObjects {
	
	private String uri;
	private String path;
	private HttpMethod method; 
	private Object body;
	private List<MediaType> accept; 
	private MediaType contentType; 
}
