package com.example.quizcards.config;

import com.example.quizcards.security.JwtAuthenticationFilter;
import com.example.quizcards.service.impl.CustomUserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomUserDetailsServiceImpl userDetailsService;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsServiceImpl userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Sử dụng phương pháp mới để vô hiệu hóa CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/user-info").authenticated()
                        .requestMatchers("/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/auth/update-password").authenticated()
                        .requestMatchers("/api/v1/set/count-set").authenticated()
                        .requestMatchers("/api/v1/set/count-set-in-current-date").authenticated()
                        .requestMatchers("/api/v1/set/create-new-set").authenticated()
                        .requestMatchers("/api/v1/set/update-set").authenticated()
                        .requestMatchers("/api/v1/set/delete-set/").authenticated()
                        .requestMatchers("/api/v1/flashcards/create-new-flashcard").authenticated()
                        .requestMatchers("/api/v1/flashcards/update-flashcard").authenticated()
                        .requestMatchers("/api/v1/flashcards/delete-flashcard").authenticated()
                        .requestMatchers("/api/v1/folder/create-new-folder").authenticated()
                        .requestMatchers("/api/v1/folder/update-folder").authenticated()
                        .requestMatchers("/api/v1/folder/delete-folder").authenticated()
                        .requestMatchers("/api/v1/collection/create-new-collection").authenticated()
                        .requestMatchers("/api/v1/collection/delete-collection").authenticated()
                        .requestMatchers("/api/v1/deadline/create-deadline").authenticated()
                        .requestMatchers("/api/v1/deadline/update-deadline").authenticated()
                        .requestMatchers("/api/v1/deadline/delete-deadline/").authenticated()
                        .requestMatchers("/api/v1/category-subscription/current-benefit").authenticated()
                        .requestMatchers("/api/v1/category-subscription/current-subscription").authenticated()
                        .requestMatchers("/api/v1/flashcard-settings/update").authenticated()
                        .requestMatchers("/api/v1/flashcard-settings/").authenticated()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(h -> h.disable())
                .formLogin(f -> f.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Authentication required!\"}");
                    response.getWriter().flush();
                }));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Cho phép gửi cookie
        config.addAllowedOrigin("http://localhost:3000"); // Chỉ định origin cụ thể
        config.addAllowedHeader("*"); // Chấp nhận tất cả các header
        config.addAllowedMethod("*"); // Cho phép tất cả các phương thức HTTP

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}