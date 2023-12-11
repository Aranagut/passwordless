<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<html>
    <head>
        <title>FIDO2</title>
        <link rel="stylesheet" href="css/login-style.css">
    <style>
      html, body {
      min-height: 100%;
      }
      body, div, form, input, select, textarea, label {
      padding: 0;
      margin: 0;
      outline: none;
      font-family: Roboto, Arial, sans-serif;
      font-size: 14px;
      color: #666;
      line-height: 22px;
      }
      h1 {
      position: absolute;
      margin: 0;
      font-size: 40px;
      color: #fff;
      z-index: 2;
      line-height: 83px;
      }
      .testbox {
      display: flex;
      justify-content: center;
      align-items: center;
      height: inherit;
      padding: 20px;
      }
      form {
      width: 500px;
      padding: 20px;
      border-radius: 6px;
      background: #fff;
      box-shadow: 0 0 8px  #cc7a00;
      }
      .banner {
      position: relative;
      height: 200px;
      background-image: url("./images/fido2_1.jpeg");
      background-size: cover;
      display: flex;
      justify-content: center;
      align-items: center;
      text-align: center;
      }
      .banner::after {
      content: "";
      background-color: rgba(0, 0, 0, 0.2);
      position: absolute;
      width: 100%;
      height: 100%;
      }
      input, select, textarea {
      margin-bottom: 10px;
      border: 1px solid #ccc;
      border-radius: 3px;
      }
      input {
      width: calc(100% - 10px);
      padding: 5px;
      }
      input[type="date"] {
      padding: 4px 5px;
      }
      textarea {
      width: calc(100% - 12px);
      padding: 5px;
      }
      .item:hover p, .item:hover i, .question:hover p, .question label:hover, input:hover::placeholder {
      color: #cc7a00;
      }
      .item input:hover, .item select:hover, .item textarea:hover {
      border: 1px solid transparent;
      box-shadow: 0 0 3px 0 #cc7a00;
      color: #cc7a00;
      }
      .item {
      position: relative;
      margin: 10px 0;
      }
      .item span {
      color: red;
      }
      input[type="date"]::-webkit-inner-spin-button {
      display: none;
      }
      .item i, input[type="date"]::-webkit-calendar-picker-indicator {
      position: absolute;
      font-size: 20px;
      color: #cc7a00;
      }
      .item i {
      right: 1%;
      top: 30px;
      z-index: 1;
      }
      [type="date"]::-webkit-calendar-picker-indicator {
      right: 1%;
      z-index: 2;
      opacity: 0;
      cursor: pointer;
      }
      input[type=radio], input[type=checkbox]  {
      display: none;
      }
      label.radio {
      position: relative;
      display: inline-block;
      margin: 5px 20px 15px 0;
      cursor: pointer;
      }
      .question span {
      margin-left: 30px;
      }
      .question-answer label {
      display: block;
      }
      label.radio:before {
      content: "";
      position: absolute;
      left: 0;
      width: 17px;
      height: 17px;
      border-radius: 50%;
      border: 2px solid #ccc;
      }
      input[type=radio]:checked + label:before, label.radio:hover:before {
      border: 2px solid #cc7a00;
      }
      label.radio:after {
      content: "";
      position: absolute;
      top: 6px;
      left: 5px;
      width: 8px;
      height: 4px;
      border: 3px solid #cc7a00;
      border-top: none;
      border-right: none;
      transform: rotate(-45deg);
      opacity: 0;
      }
      input[type=radio]:checked + label:after {
      opacity: 1;
      }
      .btn-block {
      margin-top: 10px;
      text-align: center;
      }
      button {
      width: 300px;
      padding: 10px;
      border: none;
      border-radius: 5px;
      background: #cc7a00;
      font-size: 16px;
      color: #fff;
      cursor: pointer;
      }
      button:hover {
      background: #ff9800;
      }
      @media (min-width: 568px) {
      .name-item, .city-item {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      }
      .name-item input, .name-item div {
      width: calc(50% - 20px);
      }
      .name-item div input {
      width:97%;}
      .name-item div label {
      display:block;
      padding-bottom:5px;
      }
      }
    </style>
        
    </head>
    <body>
    <div class="testbox">
      <form action="/webauthn/authn">
        <div class="banner">
          <h1>Security Key Registration</h1>
        </div>
        
        <input type="hidden" name="action" id="action" value="2" />
        <input type="hidden" name="hostname" id="hostname" value="" />

        <div class="item">
          <label for="deviceId">FIDO Device Id</label>
          <input id="deviceId" type="text" name="deviceId" value='${deviceId}'/>
        </div>

        <div class="item">
          <label for="Origin">Origin</label>
          <input id="Origin" type="text" name="Origin" value='${origin}'/>
        </div>

        <div class="item">
          <label for="attestation">Attestation<span>*</span></label>
          <input id="attestation" type="text" name="attestation" required/>
        </div>
        <div class="btn-block">
          <button type="submit">Activate the Yubikey</button>
        </div>
      </form>
    </div>    
   
<script type="text/javascript">
  
  var publicKeyCredentialCreationOptionsVal = '${publicKeyCredentialCreationOptions}';
  document.forms[0]['hostname'].value = window.location.hostname;
  Register(publicKeyCredentialCreationOptionsVal);

  //=========

  var authAbortController = window.PublicKeyCredential ? new AbortController() : null;
	var authAbortSignal = window.PublicKeyCredential ? authAbortController.signal : null;

	window.abortWebAuthnSignal = function abortWebAuthnSignal() {
	    authAbortController.abort();
	    authAbortController = new AbortController();
	    authAbortSignal = authAbortController.signal;
	}

	window.IsWebAuthnSupported = function IsWebAuthnSupported() {
	    if (!window.PublicKeyCredential) {
	        console.log("Web Authentication API is not supported on this browser.");
	        return false;
	    }
	    return true;
	}

	window.isWebAuthnPlatformAuthenticatorAvailable = function isWebAuthnPlatformAuthenticatorAvailable() {
	    var timer;
	    var p1 = new Promise(function(resolve) {
	        timer = setTimeout(function() {
	            console.log("isWebAuthnPlatformAuthenticatorAvailable - Timeout");
	            resolve(false);
	        }, 1000);
	    });
	    var p2 = new Promise(function(resolve) {
	        if (IsWebAuthnSupported() && window.PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable) {
	            resolve(
		            window.PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable().catch(function(err) {
	                    console.log(err);
	                    return false;
	                }));
	        }
	        else {
	            resolve(false);
	        }
	    });
	    return Promise.race([p1, p2]).then(function (res) {
	        clearTimeout(timer);
	        console.log("isWebAuthnPlatformAuthenticatorAvailable - " +  res);
	        return res;
	    });
	}

	window.WebAuthnPlatformRegistration = function WebAuthnPlatformRegistration(publicKeyCredentialCreationOptions) {
	    return new Promise(function(resolve, reject) {
	        isWebAuthnPlatformAuthenticatorAvailable().then(function (result) {
	            if (result) {
	                resolve(Register(publicKeyCredentialCreationOptions));
	            }
	            reject(Error("UnSupportedBrowserError"));
	        });
	    });
	}

	function Register(publicKeyCredentialCreationOptions) {
	    return new Promise(function(resolve, reject) {
	        var options = JSON.parse(publicKeyCredentialCreationOptions);
	        var publicKeyCredential = {};
	        publicKeyCredential.rp = options.rp;
	        publicKeyCredential.user = options.user;
	        publicKeyCredential.user.id = new Uint8Array(options.user.id);
	        publicKeyCredential.challenge = new Uint8Array(options.challenge);
	        publicKeyCredential.pubKeyCredParams = options.pubKeyCredParams;

	        // Optional parameters
	        if ('timeout' in options) {
	            publicKeyCredential.timeout = options.timeout;
	        }
	        if ('excludeCredentials' in options) {
	            publicKeyCredential.excludeCredentials = credentialListConversion(options.excludeCredentials);
	        }
	        if ('authenticatorSelection' in options) {
	            publicKeyCredential.authenticatorSelection = options.authenticatorSelection;
	        }
	        if ('attestation' in options) {
	            publicKeyCredential.attestation = options.attestation;
	        }
	        if ('extensions' in options) {
	            publicKeyCredential.extensions = options.extensions;
	        }
	        console.log('publicKeyCredential-->' + publicKeyCredential);
	        navigator.credentials.create({"publicKey": publicKeyCredential, "signal": authAbortSignal})
	            .then(function (newCredentialInfo) {
	                // Send new credential info to server for verification and registration.
	                console.log(newCredentialInfo);
	                var publicKeyCredential = {};
	                if ('id' in newCredentialInfo) {
	                    publicKeyCredential.id = newCredentialInfo.id;
	                }
	                if ('type' in newCredentialInfo) {
	                    publicKeyCredential.type = newCredentialInfo.type;
	                }
	                if ('rawId' in newCredentialInfo) {
	                    publicKeyCredential.rawId = toBase64Str(newCredentialInfo.rawId);
	                }
	                if (!newCredentialInfo.response) {
	                    throw "Missing 'response' attribute in credential response";
	                }
	                var response = {};
	                response.clientDataJSON = toBase64Str(newCredentialInfo.response.clientDataJSON);
	                response.attestationObject = toBase64Str(newCredentialInfo.response.attestationObject);
	                publicKeyCredential.response = response;
	                resolve(JSON.stringify(publicKeyCredential));
	                
	                var attestationVal = JSON.stringify(publicKeyCredential);
	                document.forms[0]['attestation'].value = attestationVal.replaceAll('"', '\\\"');
	                
	            }).catch(function (err) {
	            	alert("err: " + err);
	                // No acceptable authenticator or user refused consent. Handle appropriately.
	                console.log(err);
	                reject(Error(err.name));
	        });
	    });
	    alert('WebAuthn Register Exit');
	}

	function credentialListConversion(list) {
	    var credList = [];
	    for (var i=0; i < list.length; i++) {
	        var cred = {
	            type: list[i].type,
	            id: new Uint8Array(list[i].id)
	        };
	        if (list[i].transports) {
	            cred.transports = list[i].transports;
	        }
	        credList.push(cred);
	    }
	    return credList;
	}

	function toBase64Str(bin){
	    return btoa(String.fromCharCode.apply(null, new Uint8Array(bin)));
	}

  
</script>
</body>    
</html>