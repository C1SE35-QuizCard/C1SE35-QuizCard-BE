package com.example.quizcards.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface IHomeService {
    ResponseEntity<?> getHomeDataFreeUser(Authentication authentication, HttpServletResponse response);

    ResponseEntity<?> getHomeDataPremiumUser(Authentication authentication, HttpServletResponse response);

    ResponseEntity<?> getHomeDataAdmin(Authentication authentication, HttpServletResponse response);

    ResponseEntity<?> getHomeDataGuest(HttpServletResponse response);
}
