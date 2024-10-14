package com.example.quizcards.security;

import com.example.quizcards.entities.RefreshToken;
import com.example.quizcards.service.ICustomUserDetailsService;
import com.example.quizcards.service.IRefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ICustomUserDetailsService customUserDetailsService;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${jwt.jwtExpirationInMs}")
    private Long jwtExpirationInMs;

    @Value("${jwt.refreshTokenExpirationInMs}")
    private Long refreshTokenExpirationInMs;

    private final List<String> excludeUrlPatterns = List.of(
            "/api/v1/auth/**",
            "/ws/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String rft = null;
            final String email;
            String jwt = getJwtFromRequest(request);

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("rft".equals(cookie.getName())) {
                        rft = cookie.getValue();
                    }
                }
            }

            if (rft == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Optional<RefreshToken> refreshToken = refreshTokenService.findByToken(rft);

            if (refreshToken.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            RefreshToken refresh = refreshToken.get();
            email = refresh.getUser().getEmail();

            String oauth2Code = refresh.getUser().getUserCode();

            UserDetails userDetails;

            if (!((email != null || oauth2Code != null) && SecurityContextHolder.getContext().getAuthentication() == null)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String userName = tokenProvider.getUsernameFromJWT(jwt);

                userDetails = customUserDetailsService.loadUserByUsernameOnly(userName);
            } else {
                if (email == null) {
                    userDetails = customUserDetailsService.loadUserByUserCodeOnly(oauth2Code);
                } else {
                    userDetails = customUserDetailsService.loadUserByUsername(email);
                }

                if (refreshTokenService.verifyExpiration(refresh) == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                } else {
                    if (userDetails instanceof UserPrincipal) {
                        UserPrincipal up = (UserPrincipal) userDetails;
                        String newAccessToken = tokenProvider.generateAccessToken(up);
                        refreshTokenService.updateRefreshTokenWithCurrentExpiredDate(refresh);

                        ResponseCookie newAccessTokenCookie = ResponseCookie.from("token", newAccessToken)
                                .httpOnly(true)
                                .secure(true)
                                .sameSite("None")
                                .path("/")
                                .maxAge(jwtExpirationInMs)
                                .build(); // Thời gian tồn tại của cookie (0)

                        ResponseCookie newRefreshTokenCookie = ResponseCookie.from("rft", refresh.getToken())
                                .httpOnly(true)
                                .secure(true)
                                .sameSite("None")
                                .path("/")
                                .maxAge(refreshTokenExpirationInMs)
                                .build();

                        response.addHeader("Set-Cookie", newAccessTokenCookie.toString());
                        response.addHeader("Set-Cookie", newRefreshTokenCookie.toString());
                    }
                }
            }

            setAuthentication(request, userDetails);
        } catch (Exception ex) {
            LOGGER.error("Could not set user authentication in security context", ex);
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludeUrlPatterns.stream()
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }
}