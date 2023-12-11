package com.example.fiservapp.ciam.model;

import lombok.Data;

@Data
public class TokenGenerationResponse {
		public String access_token;
		public String token_type;
		public String expires_in;
}
