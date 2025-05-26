//package com.example.quizcards.controller;
//
//import com.example.quizcards.dto.request.streak.StreakRequest;
//import com.example.quizcards.dto.request.streak.StreakRequestV2;
//import com.example.quizcards.entities.AppUser;
//import com.example.quizcards.security.UserPrincipal;
//import com.example.quizcards.service.impl.StreakServiceImpl;
//import com.fasterxml.jackson.annotation.JsonFormat;
//import jakarta.validation.Valid;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//
//@RestController
//@RequestMapping("/v2/streak-learning")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class StreakControllerV2 {
//    StreakServiceImpl streakService;
//
//    @GetMapping("/get-learned-data-in-client-time")
//    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
//    ResponseEntity<?> getLearnedDataInClientTimeV2(
//            @RequestParam("timeFromClient")
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//            OffsetDateTime timeFromClient,
//            @RequestParam(value = "locale", required = false, defaultValue = "en-US")
//            String localeCode) {
//        // Validate timezone offset
//        ZoneOffset offset = timeFromClient.getOffset();
//        int totalSeconds = offset.getTotalSeconds();
//        int offsetHours = totalSeconds / 3600;
//        if (offsetHours < -12 || offsetHours > 14) {
//            return ResponseEntity.badRequest().body(null);
//        }
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
//        AppUser user = AppUser.builder()
//                .userId(userDetails.getId())
//                .build();
//
//        return ResponseEntity.ok(
//                streakService.getAnalysisStreakV2(user, timeFromClient, localeCode)
//        );
//    }
//
//    @GetMapping("/get-learned-data-in-client-time-v3")
//    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
//    ResponseEntity<?> getLearnedDataInClientTimeV3(
//            @RequestParam(value = "locale", required = false, defaultValue = "en-US")
//            String localeCode) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
//        AppUser user = AppUser.builder()
//                .userId(userDetails.getId())
//                .userTz(ZoneOffset.of(userDetails.getUserTz()))
//                .build();
//
//        return ResponseEntity.ok(
//                streakService.getAnalysisStreakV3(user, localeCode)
//        );
//    }
//
//    @PatchMapping("/update-streak-data")
//    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
//    ResponseEntity<?> updateStreakDataV2(@Valid @RequestBody StreakRequestV2 request) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
//        AppUser user = AppUser.builder()
//                .userId(userDetails.getId())
//                .userTz(ZoneOffset.of(userDetails.getUserTz()))
//                .username(userDetails.getUsername())
//                .build();
//        boolean resultUpdate = streakService.generateStreakV2(user, request.getOffsetHours(), request.getOffsetMinutes());
//        if (resultUpdate) {
//            return ResponseEntity.ok("Streak updated");
//        }
//        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
//    }
//
//    @PatchMapping("/update-streak-data-v3")
//    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
//    ResponseEntity<?> updateStreakDataV3() {
//        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
//    }
//}
