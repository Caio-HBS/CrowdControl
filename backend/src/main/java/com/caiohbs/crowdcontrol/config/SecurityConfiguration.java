package com.caiohbs.crowdcontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for setting up application web security. This class enables
 * web security and method security, it also configures the security filter chain.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthFilter
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * This method sets up the HTTP security configuration, including disabling
     * CSRF protection, setting up session management to stateless, and
     * configuring request authorization rules. JWT authentication filter is
     * also added before standard username/password auth filter.
     *
     * @param httpSecurity the {@link HttpSecurity} object to be configured.
     * @return The {@link SecurityFilterChain} object representing the configured
     * security filter chain.
     * @throws Exception if any errors occur during the configuration process.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity
    ) throws Exception {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET, "/enable-acc").permitAll()
                        .requestMatchers(HttpMethod.GET, "/acc-recovery/{userEmail}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/create-super-user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                        .requestMatchers(HttpMethod.POST, "/reset-pass").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

}
