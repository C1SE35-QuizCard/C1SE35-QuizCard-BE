package com.example.quizcards.controller;

import com.example.quizcards.service.IHomeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {
    @Autowired
    private IHomeService homeService;

    @GetMapping("/free")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getHomeDataFreeUser(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return homeService.getHomeDataFreeUser(authentication, response);
    }

    @GetMapping("/premium")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getHomeDataPremiumUser(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return homeService.getHomeDataFreeUser(authentication, response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getHomeDataAdmin(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return homeService.getHomeDataAdmin(authentication, response);
    }

    @GetMapping("/anonymous")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> getHomeDataFreerUser(HttpServletResponse response) {
        return homeService.getHomeDataGuest(response);
    }
}
