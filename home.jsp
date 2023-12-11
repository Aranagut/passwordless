<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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
      background-image: url("./images/passkey.png");
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
      background: rgb(75, 189, 270);
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
      <form acHello passkeys! Goodbye passwords.tion="/webauthn/authn">
      <svg class="svg-icon-bw" style="margin-left: 153px"; id="passkey" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" height="175"><g id="icon-passkey"><circle id="icon-passkey-head" cx="10.5" cy="6" r="4.5"></circle><path id="icon-passkey-key" d="M22.5 10.5a3.5 3.5.0 10-5 3.15V19L19 20.5 21.5 18 20 16.5 21.5 15l-1.24-1.24A3.5 3.5.0 0022.5 10.5zm-3.5.0a1 1 0 111-1 1 1 0 01-1 1z"></path><path id="icon-passkey-body" d="M14.44 12.52A6 6 0 0012 12H9a6 6 0 00-6 6v2H16V14.49a5.16 5.16.0 01-1.56-1.97z"></path></g></svg>
        <h1 style="color:white; font-size: 28px;  margin: -16px 40px -43px 4px"> Hello passkeys! Goodbye passwords</h1>
        <div class="banner">
        </div>
        <div class="item">
          <label for="username"><b>Sign in or sign up</b><span>*</span></label>
          <input id="username" type="text" name="username" required placeholder="USERNAME"/>
        </div>
        <input type="hidden" name="action" id="action" value="0" />
        <input type="hidden" name="hostname" id="hostname" value="" />
        <div class="btn-block">
          <button type="submit" onclick="registeruyubikey()">Register with a passkey</button>
        </div>
        <div class="btn-block">
		  <button type="submit" onclick="yubikeyauth()">Sign in with a passkey</button>
        </div>      
      </form>
    </div>
<script type="text/javascript">

    var errormsg="${errormsg}";
    if (errormsg.length>0)
	    alert(errormsg);

	
	function registeruyubikey() {
	    document.forms[0]['action'].value = "1";
	    document.forms[0]['hostname'].value = window.location.hostname;

	    tt="rp id: " + window.location.hostname + "\r\n" + "rp name: PingFederate"; 
		alert(tt);
		
		document.forms[0].submit();
	}

	function yubikeyauth() {
	    document.forms[0]['action'].value = "3";
	    document.forms[0]['hostname'].value = window.location.hostname;
		document.forms[0].submit();
	}
	
</script>
  </body>
</html>