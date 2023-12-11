package com.fiserv.ciam.security.demoapp.webauthn;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class p1AccessToken {

	String authbaseurl= "";
	String p1envID = "";
	String clientID = "";
	String ClientSecret = "";
	String accesstoken = null;
	long validseconds = 0;
	LocalDateTime atTime = LocalDateTime.now();
	
	public p1AccessToken(String url, String envId, String id, String secret) {
		// TODO Auto-generated constructor stub
		authbaseurl= url;
		p1envID = envId;
		clientID = id;
		ClientSecret = secret;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String authbaseurl= "https://auth.pingone.com";
		String p1envID = "2530b4d2-3d32-4fe9-8d91-a030f7bd80dd";
		String clientID = "850bbf42-adf4-40f7-915b-061a48482c35";
		String clientSecret = "RvFCqONmschYtj_Ijlz9TiTAGQpH3QY.E2fweCG1NNfd_HqPEcJ9d2q0mT4x9HFW";
       
		p1AccessToken p1AccessToken = new p1AccessToken(authbaseurl, p1envID, clientID, clientSecret);
		
		System.out.println("p1AccessToken:  " + p1AccessToken.getAccessToken());
        
	}
	
	public String getAccessToken()
	{
		LocalDateTime nowTime = LocalDateTime.now();
		long seconds = ChronoUnit.SECONDS.between(atTime, nowTime);
		
		if (accesstoken == null || seconds>= validseconds)
		{
		
	        try {
	        	
	            HttpResponse response = APICommon.postData(authbaseurl + "/"+p1envID+"/as/token?grant_type=client_credentials&scope=openid+p1:read:env:user+p1:create:env:use+p1:create:device", "", "POST", getHttpHeader(), "pfSessionId");
	            
	            System.out.println("getAccessToken = " + response.getStatusLine().getStatusCode());

	            String responseString = new BasicResponseHandler().handleResponse(response);
	            
	            System.out.println(responseString);

	            parseValidatedToken(responseString);
	            
	        } catch (Exception e) {
	        	
	        	System.out.println("Oops ! .. API have some issue " + e.getMessage());
	        	
	        }
		}
		
		return accesstoken;
	}
	
    public Map<String, String> getHttpHeader(){
        Map<String, String> map = new HashMap<>();
        String auth = clientID + ":" + ClientSecret;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        map.put("Authorization", authHeader);
        
        map.put("Content-Type", "application/x-www-form-urlencoded");
        return map;
    }	
	
	public void parseValidatedToken(String token) throws Exception 
    {
        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(token);
          
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
          
        // getting access token
        accesstoken = (String) jo.get("access_token");
        validseconds = (long) jo.get("expires_in");
        atTime = LocalDateTime.now();
          
//        System.out.println("accesstoken= " + accesstoken );
//        System.out.println("valid for " + validseconds + " seconds.");
          
    }
}
