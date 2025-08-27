package com.base.encode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.base.encode.filter.JwtFilter;
import com.base.encode.service.JpaUserDetailsService;

@Configuration
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService, JwtFilter jwtFilter) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.jwtFilter = jwtFilter;
    }

    // inside your SecurityConfig class
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173"); // your frontend URL
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
    /*
     * @Bean
     * public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     * return http
     * .csrf(csrf -> csrf.disable())
     * .sessionManagement(session ->
     * session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
     * .authorizeHttpRequests(auth -> auth
     * // allow login and register without authentication
     * .requestMatchers("/auth/login", "/auth/register").permitAll()
     * // all other requests require authentication
     * .anyRequest().authenticated())
     * .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
     * .build();
     * }
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // 🔹 enable CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // permit all requests during testing
                        .anyRequest().permitAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        @SuppressWarnings("deprecation")
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jpaUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
