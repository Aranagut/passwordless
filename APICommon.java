package com.fiserv.ciam.security.demoapp.webauthn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;

public class APICommon {


    public APICommon() {
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        String line;
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while ((line = rd.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }

    public static HttpResponse getDataNoRedirect(String uri,Map<String, String> header, String pfSessionId) throws JSONException, IOException, URISyntaxException {
        //  @SuppressWarnings("deprecation") HttpClient client = new DefaultHttpClient();

    	System.out.println("passed: " + pfSessionId);
        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("PF", pfSessionId);
        cookie.setDomain(".businesstrack.com");
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
        cookieStore.addCookie(cookie);

        HttpClient client = HttpClientBuilder.create().disableRedirectHandling().setDefaultCookieStore(cookieStore).build();
        
        HttpGet request = new HttpGet(new URI(uri));
        if (header != null){
            for (Map.Entry m:header.entrySet()){
                request.setHeader(m.getKey().toString(),m.getValue().toString());
            }
        }
        HttpResponse response = client.execute(request);
        return response;
    }

    public static HttpResponse getData(String uri,Map<String, String> header, String pfSessionId) throws JSONException, IOException, URISyntaxException {
//        @SuppressWarnings("deprecation")HttpClient client = new DefaultHttpClient();
//        HttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();

//    	System.out.println("passed: " + pfSessionId);
        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("PF", pfSessionId);
        cookie.setDomain(".businesstrack.com");
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
        cookieStore.addCookie(cookie);

        HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        HttpGet request = new HttpGet(new URI(uri));
        if (header != null){
            for (Map.Entry m:header.entrySet()){
                request.setHeader(m.getKey().toString(),m.getValue().toString());
            }
        }

        HttpResponse response = client.execute(request);
        return response;
    }
/*
    public static HttpResponse getData(String uri,Map<String, String> header) throws JSONException, IOException, URISyntaxException {
        @SuppressWarnings("deprecation")HttpClient client = new DefaultHttpClient();
//        HttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();

        HttpGet request = new HttpGet(new URI(uri));
        if (header != null){
            for (Map.Entry m:header.entrySet()){
                request.setHeader(m.getKey().toString(),m.getValue().toString());
            }
        }
        HttpResponse response = client.execute(request);
        return response;
    }
*/
    public static HttpResponse postData(String uri, String data, String method, Map<String,String> header, String pfSessionId) throws JSONException, IOException, URISyntaxException {
     //   @SuppressWarnings("deprecation") HttpClient client = new DefaultHttpClient();

        System.out.println("passed: " + pfSessionId);
        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("PF", pfSessionId);
        cookie.setDomain(".businesstrack.com");
        cookie.setPath("/");

        cookie.setSecure(true);
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
        cookieStore.addCookie(cookie);
        HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();

        HttpUriRequest request;
        switch (method.toUpperCase()) {
            case "PUT":
                request = new HttpPut(new URI(uri));
                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(data));
                break;
            case "DELETE":
                request = new HttpDelete(new URI(uri));
                break;
            case "POST":
            default:
                request = new HttpPost(new URI(uri));
                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(data));
                break;
        }
        if (header != null){
            for (Map.Entry m:header.entrySet()){
                request.setHeader(m.getKey().toString(),m.getValue().toString());
            }
        }
        HttpResponse response = client.execute(request);
        return response;
    }
/*
    public static HttpResponse postData(String uri, String data, String method, Map<String,String> header) throws JSONException, IOException, URISyntaxException {
        @SuppressWarnings("deprecation") HttpClient client = new DefaultHttpClient();

        HttpUriRequest request;
        switch (method.toUpperCase()) {
            case "PUT":
                request = new HttpPut(new URI(uri));
                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(data));
                break;
            case "DELETE":
                request = new HttpDelete(new URI(uri));
                break;
            case "POST":
            default:
                request = new HttpPost(new URI(uri));
                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(data));
                break;
        }
        if (header != null){
            for (Map.Entry m:header.entrySet()){
                request.setHeader(m.getKey().toString(),m.getValue().toString());
            }
        }
        HttpResponse response = client.execute(request);
        return response;
    }
*/
    public static String response(int responseCode){
        String response = "";
        switch (responseCode) {
            case 200:
                response = "200 - Success";
                break;
            case 400:
                response = "400 - Bad Request";
                break;
            case 401:
                response = "401 - Unauthorized";
                break;
            case 403:
                response = "403 - Forbidden";
                break;
            case 404:
                response = "404 - Not Found";
                break;
            case 405:
                response = "405 - Method Not Allowed";
                break;
            case 406:
                response = "406 - Not Acceptable";
                break;
            case 500 :
                response = "500 - Internal Server Error";
                break;
            case 501:
                response = "501 - Not Implemented";
                break;
            case 412:
                response = "412 - Precondition Failed";
                break;
            case 415:
                response = "415 - Unsupported Media Type";
                break;
            case 301:
                response = "301 - Moved Permanently";
                break;
            default:
                response = "000 - Fail";
                break;
        }
        return response.toUpperCase();
    }

}
