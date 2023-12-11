package com.fiserv.ciam.security.demoapp.webauthn;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class authn
 */
@WebServlet("/authn")
public class authn extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String applicationname = "PingFederate";

	String authbaseurl = "https://auth.pingone.com";
	String apiURL = "https://api.pingone.com";

	// CIAM 20 POC
	
	/*
	 * String p1envID = "2530b4d2-3d32-4fe9-8d91-a030f7bd80dd"; String clientID
	 * ="850bbf42-adf4-40f7-915b-061a48482c35"; String clientSecret =
	 * "RvFCqONmschYtj_Ijlz9TiTAGQpH3QY.E2fweCG1NNfd_HqPEcJ9d2q0mT4x9HFW";
	 */
	 

	// CIAM-Share-Dev
	  String p1envID = "6c173a64-a0b2-4779-b38a-fa55c510fe09"; 
	  String clientID = "9212de9f-d1cb-4b8f-9b53-818a6818e893"; 
	  String clientSecret = "67q7NicW6t9bQYP1raiSECwFgS6eWCb7IEu9HUVIZOa9EHzm~fA37nudLIe-qMTK";
	 

	// CIAM shared DEV
//	String p1envID = "6c173a64-a0b2-4779-b38a-fa55c510fe09";
//	String clientID = "55b23e30-2bf6-4a11-99e6-6e3000fbfaa9";
//	String clientSecret = "0.h6oUHp6IWqo7fLRsbLYbpVcl~.Q-FA6yd-Wa1z8ONE86a~VCYtsEHyZFdR9PvM";

	// digital banking POC
//	String p1envID = "e6a3bbc1-57d4-43da-b258-37c1c1146b00";
//	String clientID = "f0ea8eb3-df8a-44fc-b07e-f2b73ca0cf30";
//	String clientSecret = "ULPHYSA0.l.X48lCDUX0Dti7luvy1O72A7Q9d07TShRyILfLcDbOQXjGXkkxA7b6";

	p1AccessToken accesstoken = null;
	pingOneAPIs p1apis = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public authn() {
		super();
		// TODO Auto-generated constructor stub
		accesstoken = new p1AccessToken(authbaseurl, p1envID, clientID, clientSecret);

		p1apis = new pingOneAPIs(apiURL, authbaseurl, p1envID);
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());response.getWriter().println();
//		response.getWriter().append("accesstoken: ").append(accesstoken.getAccessToken());

		HttpSession mysession = request.getSession();

		int actValue = 0;
		String username = "";
		if (request.getParameter("username") != null)
			username = request.getParameter("username");
		if (request.getParameter("action") != null) {
			String straction = request.getParameter("action");
			actValue = Integer.parseInt(straction);
			;
		}

		String hostname = request.getLocalName();
		if (request.getParameter("hostname") != null)
			hostname = request.getParameter("hostname");

		String RP = "{ \"id\": \"" + hostname + "\",  \"name\": \"" + applicationname + "\"}";

		String origin = "https://" + hostname;

		int portNum = request.getServerPort();
		if (portNum != 443 && portNum != 80)
			origin = origin + ":" + portNum;

		String nextPage = "home.jsp";

		if (actValue == 1) {
			String accessToken = accesstoken.getAccessToken();

			String userId = p1apis.getUserID(accessToken, username);

			if (userId == null) {
				request.setAttribute("errormsg", "The username, " + username + ", not found!");

			} else {

				mysession.setAttribute("userid", userId);

				String devType = "FIDO2";

				mfaDevice mfaDev = p1apis.registerFIDO2Device(accessToken, userId, devType, RP);

				mysession.setAttribute("regDevId", mfaDev.id);

				request.setAttribute("origin", origin);
				request.setAttribute("deviceId", mfaDev.id);
				System.out
						.println("publicKeyCredentialCreationOptions--> " + mfaDev.publicKeyCredentialCreationOptions);
				request.setAttribute("publicKeyCredentialCreationOptions", mfaDev.publicKeyCredentialCreationOptions);

				nextPage = "registersk.jsp";

			}

		} else if (actValue == 2) {
			// String devType = "SECURITY_KEY";
			String userid = (String) mysession.getAttribute("userid");
			String regDevId = (String) mysession.getAttribute("regDevId");
			if (userid == null || regDevId == null)
				request.setAttribute("errormsg", "wrong request!");
			else {
				String accessToken = accesstoken.getAccessToken();

				String attestation = null;
				if (request.getParameter("attestation") != null)
					attestation = request.getParameter("attestation");

				System.out.println(" origin: " + origin);
				System.out.println(" regDevId: " + regDevId);
				System.out.println(" attestation: " + attestation);

				p1apis.activeFIDO2Device(accessToken, userid, regDevId, origin, attestation);
				nextPage = "registerskss.html";
			}

		} else if (actValue == 3) {
			String accessToken = accesstoken.getAccessToken();

			String userId = p1apis.getUserID(accessToken, username);

			if (userId == null) {
				request.setAttribute("errormsg", "The username, " + username + ", not found!");

			} else {
				mysession.setAttribute("userid", userId);

				String devType = "FIDO2";

				mfaDevice mfaDev = p1apis.requestFIDO2Authentication(accessToken, userId, devType);

				if (mfaDev == null) {
					request.setAttribute("errormsg",
							"No security Key device registered for username, " + username + " !");
				} else {
					mysession.setAttribute("mfaDevice", mfaDev);

					request.setAttribute("origin", origin);

					request.setAttribute("deviceAuthenticationId", mfaDev.AuthenticationId);

					request.setAttribute("publicKeyCredentialRequestOptions", mfaDev.publicKeyCredentialRequestOptions);

					nextPage = "authenticatesk.jsp";
				}
			}
		} else if (actValue == 4) {
			String devType = "FIDO2";
			mfaDevice mfaDev = (mfaDevice) mysession.getAttribute("mfaDevice");

			if (mfaDev == null)
				request.setAttribute("errormsg", "wrong request!");
			else {
				String accessToken = accesstoken.getAccessToken();

				String assertion = null;
				if (request.getParameter("assertion") != null)
					assertion = request.getParameter("assertion");

				System.out.println(" origin: " + origin);
				System.out.println(" DevAuthenticationId: " + mfaDev.AuthenticationId);
				System.out.println(" assertion: " + assertion);

				if (p1apis.checkFIDO2Assertion(accessToken, mfaDev.AuthenticationId, origin, assertion))
					nextPage = "authenticateskss.html";
				else
					request.setAttribute("errormsg", "Security Key Authentication failed.");
			}

		} else {
			//
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(nextPage);
		dispatcher.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
