package com.oktaice.scim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ScimServerApplication {

	/**
     * TODO: (C) Implement Rate Limiting (https://developer.okta.com/standards/SCIM/#rate-limiting)
	 * @param args
	 */

	public static void main(String[] args) {
		SpringApplication.run(ScimServerApplication.class, args);
	}


//	@Configuration
//	static class OktaOAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
//
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			http.authorizeRequests()
//					.anyRequest().authenticated()
//					.and()
//					.oauth2ResourceServer().jwt(); //or .opaqueToken();
//
//			// process CORS annotations
//			http.cors();
//
//			// force a non-empty response body for 401's to make the response more browser friendly
//			Okta.configureResourceServer401ResponseBody(http);
//		}
//	}
}
