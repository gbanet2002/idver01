package com.telkomsel.idver.idver01.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@ConfigurationProperties
@Validated
public class Idver {
	@Autowired 
	//get parameter from application.properties
	
	@Value("${api_key_login}")
    @NotEmpty
    private String api_key_login;
	
	@Value("${api_secret_login}")
    @NotEmpty
    private String api_secret_login;
	
	@Value("${api_url_login}")
    @NotEmpty
    private String api_url_login;
	
	@Value("${api_key_idver}")
    @NotEmpty
    private String api_key_idver;
	
	@Value("${api_secret_idver}")
    @NotEmpty
    private String api_secret_idver;
	
	@Value("${api_url_idver}")
    @NotEmpty
    private String api_url_idver;
	
	public String idver(String username, String password, String product_code, String msisdn, String homeloc, String workloc, String distance, String similarity) {
		
		String result = "000";
		
		//login first
		//Login lg = new Login();
		String resp = login(username, password, api_key_login, api_secret_login, api_url_login);
		String session_id = this.getSessionID(resp);
		String timestamp = Long.toString(System.currentTimeMillis());
		String trx_id = timestamp+msisdn;
		
		JSONObject requestBody = constructSMSBodyIdver(session_id, product_code, timestamp, trx_id, msisdn, homeloc, workloc, distance, similarity);
		String x_signature = getXSignature(api_key_idver, api_secret_idver);
		
		// Response variable
		String mashery_response = null;
		int mashery_response_code = 0;

		try {
					URL obj = new URL(api_url_idver);
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();

					// add request header
					con.setRequestMethod("POST");
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("api_key", api_key_idver);
					con.setRequestProperty("x-signature", x_signature);
					// con.setReadTimeout(mashery_timeout);
					con.setDoOutput(true);

					// Send post request
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(requestBody.toString());
					wr.flush();
					wr.close();

					mashery_response_code = con.getResponseCode();

					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();
					mashery_response = response.toString();

					Map<String, List<String>> map = con.getHeaderFields();
					for (Map.Entry<String, List<String>> entry : map.entrySet()) {
						// System.out.println("Key : " + entry.getKey()
						// + " ,Value : " + entry.getValue());
						//System.out.println(entry.getKey() + " : " + entry.getValue());
					}
		} catch (IOException e) {
					e.printStackTrace();
		}
		
		result = "Resp Code : " + mashery_response_code + ", Resp : " + mashery_response;
		
		return result;
	}
	
	public String login(String username, String password, String api_key, String api_secret, String api_url) {
		
		String result = "000";

		JSONObject requestBody = constructSMSBodyLogin(username, password);
		System.out.println("Request body = " + requestBody.toString());

		// Generate signature
		String x_signature = getXSignature(api_key, api_secret);
		System.out.println("X-Signature = " + x_signature);

		// Response variable
		String mashery_response = null;
		int mashery_response_code = 0;

		try {
			URL obj = new URL(api_url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("api_key", api_key);
			con.setRequestProperty("x-signature", x_signature);
			// con.setReadTimeout(mashery_timeout);
			con.setDoOutput(true);

			// Send post request
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(requestBody.toString());
			wr.flush();
			wr.close();

			mashery_response_code = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			mashery_response = response.toString();

			Map<String, List<String>> map = con.getHeaderFields();
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				// System.out.println("Key : " + entry.getKey()
				// + " ,Value : " + entry.getValue());
				//System.out.println(entry.getKey() + " : " + entry.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//result = "Resp Code : " + mashery_response_code + ", Resp : " + mashery_response;
		result = mashery_response;

		return result;
	}

	private static JSONObject constructSMSBodyLogin(String username, String password) {

		JSONObject login_body = new JSONObject();
		login_body.put("username", username);
		login_body.put("password", password);

		return login_body;
	}
	
	private static JSONObject constructSMSBodyIdver(String session_id, String product_code, String timestamp, String trx_id, String msisdn, String homeloc, String workloc, String distance, String similarity ) {

		JSONObject idver_body = new JSONObject();
		idver_body.put("session_id", session_id);
		idver_body.put("product_code", product_code);
		idver_body.put("timestamp", timestamp);
		idver_body.put("trx_id", trx_id);
		idver_body.put("msisdn", msisdn);
		idver_body.put("homeloc", homeloc);
		idver_body.put("workloc", workloc);
		idver_body.put("distance", distance);
		idver_body.put("similarity", similarity);
		
		return idver_body;
	}
	
	private static String getXSignature(String api_key, String api_secret) {
		// System.out.println(api_key + api_secret +
		// Long.toString(System.currentTimeMillis() / 1000L));
		return DigestUtils.sha256Hex(api_key + api_secret + Long.toString(System.currentTimeMillis() / 1000L));
	}
	
	private String getSessionID(String json) {
	    
		String SessionID = "";
		
	    JSONObject obj = new JSONObject(json);
	    SessionID = obj.getString("session_id");

	    return SessionID;
	    
	}
}
