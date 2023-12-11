package com.example.fiservapp.ciam.shared.model;

import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class CIAMHttpRequest<T> {

	@Getter
	@Setter
	private String[] authNames;

	@Getter
	@Setter
	private ParameterizedTypeReference<T> returnType;
	@Setter
	private CIAMHttpRequestParams customRequestParams;
	public CIAMHttpRequestParams getCustomRequestParams() {
		
		if (Objects.isNull(customRequestParams)) {
			customRequestParams = new CIAMHttpRequestParams();
		}
		return customRequestParams;
	}
	
	@Setter
	private CIAMHttpRequestObjects customRequestObjects;
	public CIAMHttpRequestObjects getCustomRequestObjects() {
		
		if(Objects.isNull(customRequestObjects)) {
			customRequestObjects = new CIAMHttpRequestObjects();
		}
		return customRequestObjects;
	}
}
