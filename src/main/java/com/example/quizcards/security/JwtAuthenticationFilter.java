package com.example.quizcards.security;

import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.service.ICustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ICustomUserDetailsService customUserDetailsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

//    private final List<String> excludeUrlPatterns = List.of(
//            "/ws/**",
//            "/api/v1/auth/signup",
//            "/api/v1/auth/login",
//            "/api/v1/auth/logout",
//            "/api/v1/auth/oauth2-login",
//            "/api/v1/auth/refresh-token",
//            "/api/v1/category/list",
//            "/api/v1/category/list/{{id}}",
//            "/api/v1/category/detail/{{id}}"
//    );
//
//    private final List<String> excludeIfThrows = List.of(
//            "/api/v1/auth/user-role",
//            "/api/v1/set/set-detail/{{id}}",
//            "/api/v1/set/count-public-set/{{userName}}",
//            "/api/v1/set/detail/cards/{{id}}"
////            "/api/v1/auth/user-info"
//    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String jwt = getJwtFromRequest(request);
//        if (StringUtils.hasText(jwt)) {
//            UserDetails userDetails;
//            try {
//                String userName = tokenProvider.getUsernameFromJWT(jwt);
//
//                userDetails = customUserDetailsService.loadUserByUsernameOnly(userName);
//
//                if (!userDetails.isEnabled()) {
//                    setResponseApiReturn(response, "Username is banned", HttpStatus.FORBIDDEN);
//                    return;
//                }
//
//                if (SecurityContextHolder.getContext().getAuthentication() == null) {
//                    setAuthentication(request, userDetails);
//                }
//            } catch (JwtException e) {
//                if (!byPassFilterIfThrows(request)) {
//                    setResponseApiReturn(response, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
//                    return;
//                }
//            } catch (UsernameNotFoundException e) {
//                if (!byPassFilterIfThrows(request)) {
//                    setResponseApiReturn(response, "Username not found", HttpStatus.UNAUTHORIZED);
//                    return;
//                }
//            } catch (Exception ex) {
//                LOGGER.error("Could not set user authentication in security context", ex);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        if (StringUtils.hasText(jwt)) {
            UserDetails userDetails;
            try {
                String userName = tokenProvider.getUsernameFromJWT(jwt);

                userDetails = customUserDetailsService.loadUserByUsernameOnly(userName);

                if (!userDetails.isEnabled()) {
                    setResponseApiReturn(response, "Username is banned", HttpStatus.FORBIDDEN);
                    return;
                }

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(request, userDetails);
                }
            } catch (Exception ex) {
                LOGGER.error("Could not set user authentication in security context", ex);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void setResponseApiReturn(HttpServletResponse response,
                                      String message,
                                      HttpStatus status) throws IOException {
        ApiResponse apiResponse = new ApiResponse(false, message);
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
    }

//    private boolean byPassFilterIfThrows(HttpServletRequest request) throws ServletException {
//        return excludeIfThrows.stream()
//                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        return excludeUrlPatterns.stream()
//                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
//    }
}