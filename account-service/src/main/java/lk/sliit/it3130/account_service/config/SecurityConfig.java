package lk.sliit.it3130.account_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/api/accounts/register").permitAll()
            .requestMatchers("/error").permitAll()
            .anyRequest().authenticated()
        )
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

    return http.build();
 }
}