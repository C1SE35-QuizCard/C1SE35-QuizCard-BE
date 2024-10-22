package com.example.quizcards.service;

import com.example.quizcards.dto.response.HomeDataFreeUserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface IHomeService {
    ResponseEntity<HomeDataFreeUserResponse> getFreeUserHomeData(Long userId);

    ResponseEntity<?> getHomeData(Authentication authentication, HttpServletResponse response);
}
