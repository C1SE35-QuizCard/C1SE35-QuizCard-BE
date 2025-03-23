package com.example.quizcards.config;

import com.example.quizcards.security.JwtAuthenticationFilter;
import com.example.quizcards.service.impl.CustomUserDetailsServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomUserDetailsServiceImpl userDetailsService;

    private final SecurityContextRepository securityContextRepository;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsServiceImpl userDetailsService,
                          @Lazy SecurityContextRepository securityContextRepository) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.securityContextRepository = securityContextRepository;
    }

//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
//                          CustomUserDetailsServiceImpl userDetailsService) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//        this.userDetailsService = userDetailsService;
//    }


    // Mảng chứa các endpoint cần xác thực
    String[] authEndpoints = {
            "/api/v1/auth/user-info",
            "/api/v1/auth/logout",
            "/api/v1/auth/update-password",
            "/api/v1/home/data",
            "/api/v1/home/admin",
            "/api/v1/set/count-set",
            "/api/v1/set/count-set-in-current-date",
            "/api/v1/set/create-new-set",
            "/api/v1/set/update-set",
            "/api/v1/set/delete-set/",
            "/api/v1/flashcards/create-new-flashcard",
            "/api/v1/flashcards/update-flashcard",
            "/api/v1/flashcards/delete-flashcard",
            "/api/v1/folder/create-new-folder",
            "/api/v1/folder/update-folder",
            "/api/v1/folder/delete-folder",
            "/api/v1/folder/user",
            "/api/v1/collection/create-new-collection",
            "/api/v1/collection/delete-collection",
            "/api/v1/deadline/create-deadline",
            "/api/v1/deadline/update-deadline",
            "/api/v1/deadline/delete-deadline/",
            "/api/v1/category-subscription/current-benefit",
            "/api/v1/category-subscription/current-subscription",
            "/api/v1/flashcard-settings/update",
            "/api/v1/flashcard-settings/",
            "/api/v1/flashcard-settings/sort",
            "/api/v1/progress/user/set/",
            "/api/v1/progress/user/assign-progress",
            "/api/v1/progress/user/reset-progress/",
            "/api/v1/users/**",
            "/api/v1/category/create",
            "/api/v1/category/update",
            "/api/v1/category/delete",
            "/api/v1/notification/**"
    };

    // Mảng chứa các endpoint cho phép truy cập công khai
    String[] permitAllEndpoints = {
            "/api/auth/forgot-password",
            "/api/v1/ka",
            "/api/v1/ka/**",
            "/ws/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Sử dụng phương pháp mới để vô hiệu hóa CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(authEndpoints).authenticated()
                        .requestMatchers(permitAllEndpoints).permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext(request -> request.securityContextRepository(securityContextRepository))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
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
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }
}