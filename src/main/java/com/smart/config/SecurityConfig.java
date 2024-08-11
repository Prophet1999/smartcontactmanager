package com.smart.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
	
	@Value("${mail.smtp.port}")
	private String mail_smtp_port;
	
	@Value("${mail.smtp.host}")
	private String mail_smtp_host;
	
	@Value("${mail.smtp.ssl.enable}")
	private String mail_smtp_ssl_enable;
	
	@Value("${mail.smtp.auth}")
	private String mail_smtp_auth;
	
	@Value("${mail.username}")
	private String mail_username;
	
	@Value("${mail.password}")
	private String mail_password;

    @Lazy
    @Bean
    JavaMailSenderImpl mailSender()
	{
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Properties properties=mailSender.getJavaMailProperties();
		properties.put("mail.smtp.host",mail_smtp_host);
		properties.put("mail.smtp.port",mail_smtp_port);
		properties.put("mail.smtp.ssl.enable",mail_smtp_ssl_enable);
		properties.put("mail.smtp.auth",mail_smtp_auth);
		mailSender.setUsername(mail_username);
		mailSender.setPassword(mail_password);
		mailSender.setJavaMailProperties(properties);
		return mailSender;
	}

    @Bean
    BCryptPasswordEncoder getEncoder()
	{
		return new BCryptPasswordEncoder();
	}

    @Bean
    UserDetailsService getDetails()
	{
		return new UserDetailsServiceImpl();
	}

    @Bean
    DaoAuthenticationProvider getProvider()
	{
		DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
		provider.setUserDetailsService(getDetails());
		provider.setPasswordEncoder(getEncoder());
		return provider;
	}

    @Bean
    AuthenticationManager authenticationManager
            (AuthenticationConfiguration config) throws Exception
	{
		return config.getAuthenticationManager();
	}

	@SuppressWarnings("removal")
	@Bean
    SecurityFilterChain getFilter(HttpSecurity http) throws Exception
	{
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/**").permitAll())
                .formLogin(login -> login
                        .loginPage("/signin")
                        .loginProcessingUrl("/doLogin")
                        .defaultSuccessUrl("/user/index"))
                .csrf(csrf -> csrf.disable()).cors(cors -> cors.disable());
        http.headers(headers -> headers.cacheControl().disable());
    return http.build();
	}
	
}
