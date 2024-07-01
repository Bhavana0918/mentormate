 package com.mentormate.mentormate.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mentormate.mentormate.services.CustomUserDetailsService;

@Configuration
public class SecurityConfig {
	
	@Autowired
	CustomUserDetailsService customUsersService;

	// PasswordEncoder bean for encoding and decoding passwords
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// security filter chain configuration
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomSuccessHandler customSuccessHandler)
			throws Exception {

		http.csrf(c -> c.disable())
				.authorizeHttpRequests(request -> request.requestMatchers("/mentor").hasAuthority("MENTOR")

						.requestMatchers("/mentee").hasAuthority("MENTEE").requestMatchers("/admin").hasAuthority("ADMIN").requestMatchers("/registration", "/css/**","/home", "/about-us", "/contact-us","/forgotPassword","/resetPassword/**")

						.permitAll().anyRequest().authenticated())

				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
						.successHandler(customSuccessHandler).permitAll().permitAll())

				.logout(form -> form.invalidateHttpSession(true).clearAuthentication(true)
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/home?logout")
						.permitAll());

		return http.build();

	}

	// Configure authentication manager to use custom user details service and password encoder

	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUsersService).passwordEncoder(passwordEncoder());
	}

}
