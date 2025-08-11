package com.example.redditoauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login**", "/error**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/success", true) // no #_ fragment
                );

        return http.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Add Reddit's required User-Agent header
        restTemplate.getInterceptors().add(userAgentInterceptor());

        // Log Reddit token endpoint errors
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(org.springframework.http.client.ClientHttpResponse response) throws IOException {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    log.error("Reddit token endpoint error: HTTP {} Body: {}", response.getStatusCode(), sb);
                }
                super.handleError(response);
            }
        });

        return restTemplate;
    }

    private ClientHttpRequestInterceptor userAgentInterceptor() {
        return (request, body, execution) -> {
            request.getHeaders().add(HttpHeaders.USER_AGENT, "web:myredditapp:1.0 (by /u/myusername)");
            return execution.execute(request, body);
        };
    }
}
