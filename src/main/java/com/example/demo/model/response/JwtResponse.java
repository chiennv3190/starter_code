package com.example.demo.model.response;

public class JwtResponse {

	private String jwttoken;

	public JwtResponse(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getJwttoken() {
		return jwttoken;
	}
	
}
