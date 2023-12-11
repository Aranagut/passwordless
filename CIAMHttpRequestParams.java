package com.example.fiservapp.ciam.shared.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class CIAMHttpRequestParams {
	
	@Setter
	private Map<String, Object> pathParams;
	public Map<String, Object> getPathParams() {
		if(Objects.isNull(pathParams)) {
			pathParams = new LinkedHashMap<>();
		}
		return pathParams;
	}
	@Setter
	private MultiValueMap<String, String> queryParams;
	public MultiValueMap<String, String> getQueryParams() {
		if(Objects.isNull(queryParams)) {
			queryParams = new LinkedMultiValueMap<>();
		}
		return queryParams;
	}
	@Setter
	private HttpHeaders headerParams;
	public HttpHeaders getHeaderParams() {
		if(Objects.isNull(headerParams)) {
			headerParams = new HttpHeaders();
		}
		return headerParams;
	}
	@Getter
	@Setter
	private MultiValueMap<String, String> cookieParams;
	@Getter
	@Setter
	private MultiValueMap<String, Object> formParams;
}

