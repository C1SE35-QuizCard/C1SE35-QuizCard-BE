package com.example.quizcards.security;

import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.exception.TokenRefreshException;
import com.example.quizcards.service.ICustomUserDetailsService;
import com.example.quizcards.utils.RedisUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    JwtTokenProvider tokenProvider;

    ICustomUserDetailsService customUserDetailsService;

    RedisUtils redisUtils;

    @Value("TOKEN_BLACKLIST")
    @NonFinal
    String tokenBlacklistPrefix;

    @Value("TOKEN_IAT_AVAILABLE")
    @NonFinal
    String tokenIatPrefix;

    CacheManager cacheManager;

//    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            long startTime = System.currentTimeMillis();
            SecurityContextHolder.clearContext();
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                UserDetails userDetails;
                try {
                    if (!tokenProvider.validateToken(jwt)) {
                        throw new TokenRefreshException(jwt, "Invalid refresh token!");
                    }

                    long endTimeValidate = System.currentTimeMillis();
                    System.out.println("Token validation time: " + (endTimeValidate - startTime) + "ms");

                    Map<String, Object> getPropertiesFromClaims = tokenProvider.getPropertiesFromClaims(jwt);
                    String type = getPropertiesFromClaims.get("type").toString();

                    if (!type.equals("access_token")) {
                        throw new TokenRefreshException(jwt, "Invalid access token!");
                    }

                    long userId = Long.parseLong(getPropertiesFromClaims.get("uid").toString());
                    String jti = getPropertiesFromClaims.get("jti").toString();

                    String blkKey = tokenBlacklistPrefix + "_" + userId + "_" + jti;
                    String iatKey = tokenIatPrefix       + "_" + userId;

                    @SuppressWarnings("unchecked")
                    Cache<Object, Object> tokenMetaCache =
                            (Cache<Object, Object>) cacheManager.getCache("tokenMeta").getNativeCache();

                    // L1 cache
                    @SuppressWarnings("unchecked")
                    Map<String, Object> meta = (Map<String, Object>) tokenMetaCache.getIfPresent(jwt);
                    if (meta == null) {
                        // L1 miss → pipeline Redis
                        List<Object> vals = redisUtils.multiGet(List.of(blkKey, iatKey));
                        boolean isBlk = !vals.isEmpty() && vals.get(0) != null;
                        Long    iat   = null;
                        if (vals.size() > 1 && vals.get(1) != null) {
                            // nếu bạn lưu epoch millis (Long) hoặc String
                            iat = ((Instant) vals.get(1)).toEpochMilli();
                        }
                        meta = new HashMap<>();
                        meta.put("isBlk", isBlk);
                        meta.put("iat", iat);
                        tokenMetaCache.put(jwt, meta);
                    }

                    boolean isBlk = (boolean) meta.get("isBlk");
                    if (isBlk) {
                        throw new TokenRefreshException(jwt, "Token is blacklisted!");
                    }
                    Long iat = (Long) meta.get("iat");
                    if (iat != null && Long.parseLong(getPropertiesFromClaims.get("created_at").toString()) < iat) {
                        throw new TokenRefreshException(jwt, "Token is expired!");
                    }

                    long endTimeGetProperties = System.currentTimeMillis();
                    System.out.println("Get properties from claims and validate time: "
                            + (endTimeGetProperties - endTimeValidate) + "ms");

                    String userName = tokenProvider.getUsernameFromJWT(jwt);

                    userDetails = customUserDetailsService.loadUserByUsernameOnly(userName);

                    if (!userDetails.isEnabled()) {
                        setResponseApiReturn(response, "Username is banned", HttpStatus.FORBIDDEN);
                        return;
                    }

                    long endTimeLoadUser = System.currentTimeMillis();
                    System.out.println("Load user by username time: " + (endTimeLoadUser - endTimeGetProperties) + "ms");

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
            long endTime = System.currentTimeMillis();
            System.out.println("Authentication filter bypassed: " + (endTime - startTime) + "ms");
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