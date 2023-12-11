package com.example.fiservapp.ciam.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpRequestBuillderUtil {
	
	private HttpRequestBuillderUtil() {
		
	}

	/**
	 * Include queryParams in uriParams taking into account the paramName
	 *
	 * @param queryParams The query parameters
	 * @param uriParams   The path parameters return template query string
	 */
	public static String generateQueryUri(MultiValueMap<String, String> queryParams, Map<String, Object> uriParams) {
		StringBuilder queryBuilder = new StringBuilder();
		queryParams.forEach((name, values) -> {
			try {
				final String encodedName = URLEncoder.encode(name, "UTF-8");
				if (CollectionUtils.isEmpty(values)) {
					if (queryBuilder.length() != 0) {
						queryBuilder.append('&');
					}
					queryBuilder.append(encodedName);
				} else {
					generateQueryUri(uriParams, queryBuilder, values, encodedName);
				}
			} catch (UnsupportedEncodingException e) {
				log.warn("Unsupported Query Param", e);
			}
		});
		return queryBuilder.toString();

	}

	private static void generateQueryUri(Map<String, Object> uriParams, StringBuilder queryBuilder, List<String> values,
			final String encodedName) throws UnsupportedEncodingException {
		int valueItemCounter = 0;
		for (Object value : values) {
			if (queryBuilder.length() != 0) {
				queryBuilder.append('&');
			}
			queryBuilder.append(encodedName);
			if (value != null) {
				String templatizedKey = encodedName + valueItemCounter++;
				final String encodedValue = URLEncoder.encode(value.toString(), "UTF-8");
				uriParams.put(templatizedKey, encodedValue);
				queryBuilder.append('=').append("{").append(templatizedKey).append("}");
			}
		}
	}

	/**
	 * Expand path template with variables
	 *
	 * @param pathTemplate path template with place holders
	 * @param variables    variables to replace
	 * @return path with place holders replaced by variables
	 */
	public static String expandPath(String pathTemplate, Map<String, Object> variables) {
		// disable default URL encoding
		DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
		uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setUriTemplateHandler(uriBuilderFactory);

		return restTemplate.getUriTemplateHandler().expand(pathTemplate, variables).toString();
	}
	
	
	/**
	 * Select the body to use for the request
	 *
	 * @param obj         the body object
	 * @param formParams  the form parameters
	 * @param contentType the content type of the request
	 * @return Object the selected body
	 */
	public static Object selectBody(Object obj, MultiValueMap<String, Object> formParams, MediaType contentType) {
		boolean isForm = MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)
				|| MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType);
		return isForm ? formParams : obj;
	}

	/**
	 * Add cookies to the request that is being built
	 *
	 * @param cookies        The cookies to add
	 * @param requestBuilder The current request
	 */
	public static void addCookiesToRequest(MultiValueMap<String, String> cookies, BodyBuilder requestBuilder) {
		if (!cookies.isEmpty()) {
			requestBuilder.header("Cookie", buildCookieHeader(cookies));
		}
	}
	
	/**
	 * Build cookie header. Keeps a single value per cookie (as per
	 * <a href="https://tools.ietf.org/html/rfc6265#section-5.3"> RFC6265 section
	 * 5.3</a>).
	 *
	 * @param cookies map all cookies
	 * @return header string for cookies.
	 */
	private static String buildCookieHeader(MultiValueMap<String, String> cookies) {
		final StringBuilder cookieValue = new StringBuilder();
		String delimiter = "";
		for (final Map.Entry<String, List<String>> entry : cookies.entrySet()) {
			final String value = entry.getValue().get(entry.getValue().size() - 1);
			cookieValue.append(String.format("%s%s=%s", delimiter, entry.getKey(), value));
			delimiter = "; ";
		}
		return cookieValue.toString();
	}
	
	/**
	 * Add headers to the request that is being built
	 * 
	 * @param headers        The headers to add
	 * @param requestBuilder The current request
	 */
	public static void addHeadersToRequest(HttpHeaders headers, BodyBuilder requestBuilder) {
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			List<String> values = entry.getValue();
			for (String value : values) {
				if (value != null) {
					requestBuilder.header(entry.getKey(), value);
				}
			}
		}
	}
}
