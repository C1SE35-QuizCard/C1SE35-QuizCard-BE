package com.example.quizcards.security;

import com.example.quizcards.entities.RefreshToken;
import com.example.quizcards.exception.AccessDeniedException;
import com.example.quizcards.service.ICustomUserDetailsService;
import com.example.quizcards.service.IRefreshTokenService;
import com.example.quizcards.utils.CookieSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ICustomUserDetailsService customUserDetailsService;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @Autowired
    private CookieSetter cs;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final List<String> excludeUrlPatterns = List.of(

            "/api/v1/auth/**",
            "/ws/**",
            "/api/**",
            "/api/v1/auth/signup",
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/api/**",
            "/ws/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String bearer =  request.getHeader("Authorization");
            if (!StringUtils.hasText(bearer) || bearer.startsWith("Bearer")) {
                throw new Exception("Token must be start by Bearer");
            }

            String rft = null;
            String jwt = null;
            final String email;

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                    } else if ("rft".equals(cookie.getName())) {
                        rft = cookie.getValue();
                    }
                }
            }

            if (rft == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Optional<RefreshToken> tokenData = refreshTokenService.findByToken(rft);

            if (tokenData.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            RefreshToken refreshToken = tokenData.get();

            if (!refreshToken.getUser().getEnabled()) {
                throw new AccessDeniedException("User is disabled");
            }

            email = refreshToken.getUser().getEmail();

            String oauth2Code = refreshToken.getUser().getUserCode();

            if (!((email != null || oauth2Code != null) && SecurityContextHolder.getContext().getAuthentication() == null)) {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails;

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String userName = tokenProvider.getUsernameFromJWT(jwt);

                userDetails = customUserDetailsService.loadUserByUsernameOnly(userName);
            } else {
                if (email == null) {
                    userDetails = customUserDetailsService.loadUserByUserCodeOnly(oauth2Code);
                } else {
                    userDetails = customUserDetailsService.loadUserByEmailOnly(email);
                }

                if (refreshTokenService.verifyExpiration(refreshToken) == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                } else {
                    if (userDetails instanceof UserPrincipal) {
                        UserPrincipal up = (UserPrincipal) userDetails;
                        String newAccessToken = tokenProvider.generateAccessToken(up);
                        refreshTokenService.updateRefreshTokenWithCurrentExpiredDate(refreshToken);

                        cs.generateTokenToCookie(response, newAccessToken, refreshToken.getToken(), "ok");
                    } else {
                        filterChain.doFilter(request, response);
                        return;
                    }
                }
            }

            setAuthentication(request, userDetails);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            LOGGER.error("Could not set user authentication in security context", ex);
            Map<String, Object> errors = new HashMap<>();
            errors.put("success", false);
            errors.put("message", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errors));
        }
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludeUrlPatterns.stream()
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }
}