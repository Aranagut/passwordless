package com.example.fiservapp.ciam.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.fiservapp.ciam.shared.model.CIAMHttpRequest;
import com.example.fiservapp.ciam.utils.HttpRequestBuillderUtil;
@Service
public class RestTemplateService {
	
	@Autowired
	private RestTemplate template;
	
	/**
	 * Invokes the API using the RestTemlplate Spring flow
	 * 
	 * @param <T>
	 * @param httpRequest
	 * @return
	 * @throws RestClientException
	 */
	public <T> ResponseEntity<T> execute(CIAMHttpRequest<T> httpRequest) throws RestClientException {

		HttpMethod method = httpRequest.getCustomRequestObjects().getMethod();
		//Build the URI
		URI uri = buidURIBasedOnPathURLAndQueryParams(httpRequest);
		RequestEntity<Object> requestEntity = buildRequestBody(httpRequest, uri);
		ResponseEntity<T> responseEntity = template.exchange(uri, method, requestEntity,
				httpRequest.getReturnType());
		if (responseEntity.getStatusCode().is2xxSuccessful()) return responseEntity;
		throw new RestClientException("API returned " + responseEntity.getStatusCode()
					+ " and it wasn't handled by the RestTemplate error handler");
	}

	private <T> RequestEntity<Object> buildRequestBody(CIAMHttpRequest<T> httpRequest, URI uri) {
		
		HttpHeaders headerParams = httpRequest.getCustomRequestParams().getHeaderParams();
		MultiValueMap<String, String> cookieParams = httpRequest.getCustomRequestParams().getCookieParams();
		BodyBuilder requestBuilder = RequestEntity.method(httpRequest.getCustomRequestObjects().getMethod(), uri);
		List<MediaType> accept = httpRequest.getCustomRequestObjects().getAccept();
		if (Objects.nonNull(accept)) {
			requestBuilder.accept(accept.toArray(new MediaType[accept.size()]));
		}
		if (Objects.nonNull(httpRequest.getCustomRequestObjects().getContentType())) {
			requestBuilder.contentType(httpRequest.getCustomRequestObjects().getContentType());
		}
		//Default Header
		requestBuilder.header("User-Agent", "Java-SDK");
		if(Objects.nonNull(headerParams)) {
			HttpRequestBuillderUtil.addHeadersToRequest(headerParams, requestBuilder);
		}
		if(Objects.nonNull(cookieParams)) {
			HttpRequestBuillderUtil.addCookiesToRequest(cookieParams, requestBuilder);
		}
		//Select Form Params/ Body based on the content type and set it
		return requestBuilder.body(HttpRequestBuillderUtil.selectBody(
				httpRequest.getCustomRequestObjects().getBody(), httpRequest.getCustomRequestParams().getFormParams(),
				httpRequest.getCustomRequestObjects().getContentType()));
	}

	private <T> URI buidURIBasedOnPathURLAndQueryParams(CIAMHttpRequest<T> httpRequest) {

		URI uri;
		UriComponentsBuilder builder = null;
		MultiValueMap<String, String> queryParams = httpRequest.getCustomRequestParams().getQueryParams();
		Map<String, Object> pathParams = httpRequest.getCustomRequestParams().getPathParams();
		try {
			Map<String, Object> uriParams = new HashMap<>();
			uriParams.putAll(pathParams);
			String finalUri = httpRequest.getCustomRequestObjects().getPath();
			if (Objects.nonNull(queryParams) && !queryParams.isEmpty()) {
				String queryUri = HttpRequestBuillderUtil.generateQueryUri(queryParams, uriParams);
				finalUri += "?" + queryUri;
			}
			String expandedPath = HttpRequestBuillderUtil.expandPath(finalUri, uriParams);
			builder = UriComponentsBuilder.fromHttpUrl(httpRequest.getCustomRequestObjects().getUri())
					.path(expandedPath);

			uri = new URI(builder.build().toUriString());
		} catch (URISyntaxException ex) {
			throw new RestClientException("Could not build URL: " + builder.toUriString(), ex);
		}
		return uri;
	}
}
