package com.example.quizcards.controller;

import com.example.quizcards.service.IHomeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {
    @Autowired
    private IHomeService homeService;

    @GetMapping
    public ResponseEntity<?> getHomeData(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return homeService.getHomeData(authentication, response);
    }
}
