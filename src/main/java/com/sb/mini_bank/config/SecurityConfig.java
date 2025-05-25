package com.sb.mini_bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /* This class is for security configuration and allows only OpenAPI (Swagger)
    to be accessed without authentication or login.  */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                    "/swagger-ui.html",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/v3/api-docs.yaml"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic -> {});

        return http.build();
    }
}
