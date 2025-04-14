package com.example.quizcards.security;

import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.exception.TokenRefreshException;
import com.example.quizcards.service.ICustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    JwtTokenProvider tokenProvider;

    ICustomUserDetailsService customUserDetailsService;

    RedisTemplate<String, String> redisTemplate;

    @Value("TOKEN_BLACKLIST_")
    @NonFinal
    String tokenBlacklistPrefix;

    @Value("TOKEN_IAT_")
    @NonFinal
    String tokenIatPrefix;

//    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
//            System.out.println(Thread.currentThread().threadId());
            SecurityContextHolder.clearContext();
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                UserDetails userDetails;
                try {
                    if (!tokenProvider.validateToken(jwt)) {
                        throw new TokenRefreshException(jwt, "Invalid refresh token!");
                    }

                    Map<String, Object> getPropertiesFromClaims = tokenProvider.getPropertiesFromClaims(jwt);
                    String type = getPropertiesFromClaims.get("type").toString();

                    if (!type.equals("access_token")) {
                        throw new TokenRefreshException(jwt, "Invalid access token!");
                    }

                    long userId = Long.parseLong(getPropertiesFromClaims.get("uid").toString());
                    String jti = getPropertiesFromClaims.get("jti").toString();

                    if (redisTemplate.opsForValue().get(tokenBlacklistPrefix + userId + "_" + jti) != null) {
                        throw new TokenRefreshException(jwt, "Token is blacklisted!");
                    }

                    String userName = tokenProvider.getUsernameFromJWT(jwt);

                    userDetails = customUserDetailsService.loadUserByUsernameOnly(userName);

                    if (!userDetails.isEnabled()) {
                        setResponseApiReturn(response, "Username is banned", HttpStatus.FORBIDDEN);
                        return;
                    }

//                if (SecurityContextHolder.getContext().getAuthentication() == null) {
//                    setAuthentication(request, userDetails);
//                }

                    // dùng cho async lẫn sync luôn
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                } catch (Exception ex) {
                    log.error("Could not set user authentication in security context", ex);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            // dùng cho async lẫn sync luôn, xóa để khỏi lẫn lộn với thread khác
            SecurityContextHolder.clearContext();
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
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
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
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