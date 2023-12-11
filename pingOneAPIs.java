package com.fiserv.ciam.security.demoapp.webauthn;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class pingOneAPIs {

	String apibaseurl= "";
	String authApibaseurl= "";
	String p1envID = "";
	
	public pingOneAPIs(String apiUrl, String authApiUrl, String envID) {
		// TODO Auto-generated constructor stub
		apibaseurl = apiUrl;
		authApibaseurl = authApiUrl;
		p1envID = envID;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String authbaseurl= "https://auth.pingone.com";
		String apiURL = "https://api.pingone.com";
		String p1envID = "2530b4d2-3d32-4fe9-8d91-a030f7bd80dd";
		
		String clientID = "850bbf42-adf4-40f7-915b-061a48482c35";
		String clientSecret = "RvFCqONmschYtj_Ijlz9TiTAGQpH3QY.E2fweCG1NNfd_HqPEcJ9d2q0mT4x9HFW";
		
		p1AccessToken p1AccessToken = new p1AccessToken(authbaseurl, p1envID, clientID, clientSecret);
		
		String accessToken = p1AccessToken.getAccessToken();
		
		System.out.println("p1AccessToken:  " + accessToken);
		
		pingOneAPIs p1apis = new pingOneAPIs(apiURL, authbaseurl, p1envID);
		
		String userId = p1apis.getUserID(accessToken, "Usbrogntc");
		
		System.out.println("userId: " + userId);
		
//		String devId = "b95cb9d8-f328-41cf-915e-bf59cfd86b22";
//		String origin = "https://localhost:8443";

		String RP = "{ \"id\": \"localhost\",  \"name\": \"PingFederate\"}";
		
//		String attestation = "{\"id\":\"wHJYzWJddpkWGNj0zc1OAkrjQwJqj7S-8IoqZQNTICTEPVNgxhP4sGOYZ6sxlUhV\",\"type\":\"public-key\",\"rawId\":\"wHJYzWJddpkWGNj0zc1OAkrjQwJqj7S+8IoqZQNTICTEPVNgxhP4sGOYZ6sxlUhV\",\"response\":{\"clientDataJSON\":\"eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoiczVYNnozR0tCaGhjS1lrcHVXSk0xS2FBdzVScFBoazlTNC1jU0t4QW83dyIsIm9yaWdpbiI6Imh0dHBzOi8vbG9jYWxob3N0Ojg0NDMiLCJjcm9zc09yaWdpbiI6ZmFsc2UsIm90aGVyX2tleXNfY2FuX2JlX2FkZGVkX2hlcmUiOiJkbyBub3QgY29tcGFyZSBjbGllbnREYXRhSlNPTiBhZ2FpbnN0IGEgdGVtcGxhdGUuIFNlZSBodHRwczovL2dvby5nbC95YWJQZXgifQ==\",\"attestationObject\":\"o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YVjCSZYN5YgOjGh0NBcPZHZgW4/krrmihjLHmVzzuoMdl2PFAAAAAwAAAAAAAAAAAAAAAAAAAAAAMMByWM1iXXaZFhjY9M3NTgJK40MCao+0vvCKKmUDUyAkxD1TYMYT+LBjmGerMZVIVaUBAgMmIAEhWCDAcljNYl12mRYY2PTNoh8GR0r7J8WQag8HCKsOuZ3JvSJYIKKTZNW9TwN02zMPaq6AIwAA3WjUqrAIYyF0WqGwkJLJoWtjcmVkUHJvdGVjdAI=\"}}";
		
//		mfaDevice mfaDev = p1apis.activeFIDO2Device(accessToken, userId, devId, origin, attestation);
		
		String devType = "FIDO2";
		
		mfaDevice mfaDev = p1apis.requestFIDO2Authentication(accessToken, userId, devType);
		
//		String deviceId = p1apis.getUserDevices(accessToken, userId, DevType, RP);
		
		System.out.println("deviceId: " + mfaDev.publicKeyCredentialRequestOptions);
		
	}

	public String getUserID(String AccessToken, String username)
	{
		String userid = null;
		
        try {
        	
        	String requestURL = apibaseurl + "/v1/environments/" + p1envID + "/users?limit=100&filter=username%20eq%20%22" + username + "%22"; 

        	HttpResponse response = APICommon.getData(requestURL, getHTTPHeader(AccessToken, "application/json"), "pfSessionId");
            
            System.out.println("getUserID = " + response.getStatusLine().getStatusCode());

            String responseString = new BasicResponseHandler().handleResponse(response);
            
            System.out.println(responseString);

            userid = parseUserId(responseString);
            
        } catch (Exception e) {
        	
        	System.out.println("Oops ! .. API have some issue " + e.getMessage());
        	
        }		
		
		return userid;
	}
	
    public Map<String, String> getHTTPHeader(String AccessToken, String contentType){
        Map<String, String> map = new HashMap<>();
        
//        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        
        String authHeader = "Bearer " + AccessToken;
        map.put("Authorization", authHeader);
        
        map.put("Content-Type", contentType);
        return map;
    }
    

	public String parseUserId(String token) throws Exception 
    {
        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(token);
          
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
        // getting user id
        JSONObject embedded = (JSONObject) jo.get("_embedded");
        JSONArray users = (JSONArray) embedded.get("users");
        JSONObject user = (JSONObject) users.get(0);
        String uid  = (String) user.get("id");
        System.out.println("user id= " + uid );
        return uid;
          
    }    

	
    public String getUserDevices(String AccessToken, String userid, String DevType, String RP)
	{
    	String deviceId = null;
        try {
        	
            HttpResponse response = APICommon.getData(apibaseurl + "/v1/environments/" + p1envID + "/users/" + userid + "/devices", getHTTPHeader(AccessToken, "application/json"), "pfSessionId");
            
            System.out.println("getUserDevices = " + response.getStatusLine().getStatusCode());

            String responseString = new BasicResponseHandler().handleResponse(response);
            
            System.out.println(responseString);

            deviceId = parseDeviceId(responseString, DevType);
            
        } catch (Exception e) {
        	
        	System.out.println("Oops ! .. API have some issue " + e.getMessage());
        	
        }		
		
		return deviceId;
		
	}
    
	public String parseDeviceId(String jsonData, String deviceType) throws Exception 
    {
        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(jsonData);
          
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
        // getting user id
        JSONObject embedded = (JSONObject) jo.get("_embedded");
        JSONArray devices = (JSONArray) embedded.get("devices");

        String deviceId = null;

        // iterating phoneNumbers
        Iterator devItr = devices.iterator();
          
        while (devItr.hasNext()) 
        {
        	JSONObject device = (JSONObject) devItr.next();
        	String devId =  (String) device.get("id");
        	String devType =  (String) device.get("type");
        	String devNickname =  (String) device.get("nickname");
        	
        	if (devType.indexOf(deviceType)==0)
        		deviceId = devId;
        }
        
        System.out.println("device id= " + deviceId );
        
        return deviceId;
          
    }
	
	
    public mfaDevice registerFIDO2Device(String AccessToken, String userid, String devType, String RP)
	{
    	mfaDevice mfaDev = new mfaDevice();
        try {
        	
        	String body = "{\"type\": \"" + devType + "\", \"rp\": " + RP + " }"; 
        		    
            HttpResponse response = APICommon.postData(apibaseurl + "/v1/environments/" + p1envID + "/users/" + userid + "/devices", body, "POST", getHTTPHeader(AccessToken, "application/json"), "pfSessionId");
            
            System.out.println("registerFIDO2Device = " + response.getStatusLine().getStatusCode());

            String responseString = new BasicResponseHandler().handleResponse(response);
            
            System.out.println(responseString);

            mfaDev = parseNewFidoDevice(responseString, devType);
            
        } catch (Exception e) {
        	
        	System.out.println("Oops ! .. API have some issue: API Error " + e.getMessage());
        	
        }		
		
		return mfaDev;
		
	}
    
	public mfaDevice parseNewFidoDevice(String token, String devType) throws Exception 
    {
		mfaDevice mfaDev = new mfaDevice();
		
		mfaDev.type = devType;
		
        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(token);
          
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
        
        mfaDev.id = (String) jo.get("id");
        mfaDev.publicKeyCredentialCreationOptions = (String) jo.get("publicKeyCredentialCreationOptions");
        
        System.out.println("device id= " + mfaDev.id );
        System.out.println("publicKeyCredentialCreationOptions= " + mfaDev.publicKeyCredentialCreationOptions );
        
        return mfaDev;
          
    }
	
	
    public mfaDevice activeFIDO2Device(String AccessToken, String userid, String deviceId, String origin, String attestation)
	{
    	mfaDevice mfaDev = new mfaDevice();
        try {
        	
        	String requestURL = apibaseurl + "/v1/environments/" + p1envID + "/users/" + userid + "/devices/" + deviceId;
        	
        	System.out.println("request URLL: " + requestURL);

        	String body = "{\"origin\": \"" + origin + "\", \"attestation\": \"" + attestation + "\"}"; 
        		    
        	System.out.println("request body: " + body);
        	
        	HttpResponse response = APICommon.postData(requestURL, body, "POST", getHTTPHeader(AccessToken, "application/vnd.pingidentity.device.activate+json"), "pfSessionId");
            
            System.out.println("activeFIDO2Device = " + response.getStatusLine().getStatusCode());

            String responseString = new BasicResponseHandler().handleResponse(response);
            
            System.out.println(responseString);

//            mfaDev = parseNewFidoDevice(responseString, devType);
            
        } catch (Exception e) {
        	
        	System.out.println("Oops ! .. API have some issue " + e.getMessage());
        	
        }		
		
		return mfaDev;
		
	}

    
    public mfaDevice requestFIDO2DeviceAuth(String AccessToken, String userid, String deviceId, String origin, String attestation)
	{
    	mfaDevice mfaDev = new mfaDevice();
        try {
        	
        	String requestURL = apibaseurl + "/v1/environments/" + p1envID + "/users/" + userid + "/devices/" + deviceId;
        	
        	System.out.println("request URL: " + requestURL);

        	String body = "{\"origin\": \"" + origin + "\", \"attestation\": \"" + attestation + "\"}"; 
        		    
        	System.out.println("request body: " + body);
        	
        	HttpResponse response = APICommon.postData(requestURL, body, "POST", getHTTPHeader(AccessToken, "application/vnd.pingidentity.device.activate+json"), "pfSessionId");
            
            System.out.println("requestFIDO2DeviceAuth = " + response.getStatusLine().getStatusCode());

            String responseString = new BasicResponseHandler().handleResponse(response);
            
            System.out.println(responseString);

//            mfaDev = parseNewFidoDevice(responseString, devType);
            
        } catch (Exception e) {
        	
        	System.out.println("Oops ! .. API have some issue " + e.getMessage());
        	
        }		
		
		return mfaDev;
		
	}
    
    
//==================Authentication
    
    public mfaDevice requestFIDO2Authentication(String AccessToken, String userid, String devType)
	{
    	mfaDevice mfaDev = new mfaDevice();
        try {
        	
        	String body = "{ \"user\": {   \"id\": \"" + userid +"\" }  }"; 

//	{{authPath}}/{{envID}}/deviceAuthentications        	
            HttpResponse response = APICommon.postData(authApibaseurl + "/" + p1envID + "/deviceAuthentications", body, "POST", getHTTPHeader(AccessToken, "application/json"), "pfSessionId");
            
            System.out.println("requestFIDO2Authentication = " + response.getStatusLine().getStatusCode());

            String responseString = new BasicResponseHandler().handleResponse(response);
            
            System.out.println(responseString);

    		String fido2DeviceId = parseFidoDevice(responseString, devType);
    		
    		if (fido2DeviceId==null)
    			return null;
    		
    		String selectedDeviceID = parseSelectedDevice(responseString);
    		
    		String authenticationID = parseAuthId(responseString);
    		
    		if (selectedDeviceID == null || selectedDeviceID.compareTo(fido2DeviceId) !=0)
    		{
    			
    			//{{authPath}}/{{envID}}/deviceAuthentications/{{deviceAuthID}}
    			body = "{ \"device\": { \"id\": \""  + fido2DeviceId + "\"  }, \"compatibility\": \"FULL\" }";
    			
    			response = APICommon.postData(authApibaseurl + "/" + p1envID + "/deviceAuthentications/" + authenticationID, body, "POST", getHTTPHeader(AccessToken, "application/vnd.pingidentity.device.select+json"), "pfSessionId");

    			responseString = new BasicResponseHandler().handleResponse(response);
    		}	
    		
            mfaDev = parsepublicKeyCredentialRequestOptions(responseString, devType);
            
        } catch (Exception e) {
        	
        	System.out.println("Oops ! .. API have some issue " + e.getMessage());
        	
        }		
		
		return mfaDev;
		
	}    

    
	public mfaDevice parsepublicKeyCredentialRequestOptions(String token, String devType) throws Exception 
    {
		mfaDevice mfaDev = new mfaDevice();
		
		mfaDev.type = devType;
		
        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(token);
          
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
      
        mfaDev.AuthenticationId = (String) jo.get("id");
        mfaDev.publicKeyCredentialRequestOptions = (String) jo.get("publicKeyCredentialRequestOptions");
        
        JSONObject joSelDev = (JSONObject) jo.get("selectedDevice");;
        mfaDev.id = (String) joSelDev.get("id");
        
        return mfaDev;
    }

	public String parseAuthId(String token) throws Exception 
    {
        Object obj = new JSONParser().parse(token);
        JSONObject jo = (JSONObject) obj;

        String authId = (String) jo.get("id");
        return authId;
    }

	
	public String parseSelectedDevice(String token) throws Exception 
    {
        Object obj = new JSONParser().parse(token);
        JSONObject jo = (JSONObject) obj;
        JSONObject joSelDev = (JSONObject) jo.get("selectedDevice");;

        String selectedDevId = null;
        if (joSelDev != null)
        	selectedDevId = (String) joSelDev.get("id");
        
        return selectedDevId;
    }
	

	public String parseFidoDevice(String jsonData, String deviceType) throws Exception 
    {
        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(jsonData);
          
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
        // getting user id
        JSONObject embedded = (JSONObject) jo.get("_embedded");
        JSONArray devices = (JSONArray) embedded.get("devices");

        String deviceId = null;

        // iterating phoneNumbers
        Iterator devItr = devices.iterator();
          
        while (devItr.hasNext()) 
        {
        	JSONObject device = (JSONObject) devItr.next();
        	String devId =  (String) device.get("id");
        	String devType =  (String) device.get("type");
        	String devNickname =  (String) device.get("nickname");
        	
        	if (devType.indexOf(deviceType)==0)
        		deviceId = devId;
        }
        
        System.out.println("device id == " + deviceId );
        
        return deviceId;
          
    }
	
    public boolean checkFIDO2Assertion(String AccessToken, String devAuthId, String origin, String assertion)
	{
    	boolean successAuth = false;
        try {
     
        	// {{authPath}}/{{envID}}/deviceAuthentications/{{deviceAuthID}}
        	
        	String requestURL = authApibaseurl + "/" + p1envID + "/deviceAuthentications/" + devAuthId;
        	
        	System.out.println("request URL: " + requestURL);

//        	String body = "{\"origin\": \"" + origin +"\", \"assertion\": \"" + assertion + "\", \"compatibility\" : \"FULL\"}"; 

        	String body = "{\"origin\": \"" + origin +"\", \"assertion\": \"" + assertion + "\", \"compatibility\" : \"SECURITY_KEY_ONLY\"}";
        	
        		    
        	System.out.println("request body: " + body);
        	
        	HttpResponse response = APICommon.postData(requestURL, body, "POST", getHTTPHeader(AccessToken, "application/vnd.pingidentity.assertion.check+json"), "pfSessionId");
            
            System.out.println("status code = " + response.getStatusLine().getStatusCode());

            String responseString = new BasicResponseHandler().handleResponse(response);
            
            System.out.println(responseString);

            successAuth = true;
            
//            mfaDev = parseNewFidoDevice(responseString, devType);
            
        } catch (Exception e) {
        	
        	System.out.println("Oops ! .. API have some issue " + e.getMessage());
        	
        }		
		
		return successAuth;
		
	}

}