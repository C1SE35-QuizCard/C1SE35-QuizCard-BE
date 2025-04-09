package com.example.quizcards.controller;

import com.example.quizcards.dto.request.StreakRequest;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.impl.StreakServiceImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/streak-learning")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StreakController {
    StreakServiceImpl streakService;

    @GetMapping("/get-learned-data-in-client-time")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> getLearnedDataInClientTime(@RequestParam(value = "offsetHours") int offsetHours,
                                                 @RequestParam(value = "offsetMinutes") int offsetMinutes,
                                                 @RequestParam(value = "locale", required = false, defaultValue = "en-US")
                                                 String localeCode) {
        if (offsetHours > 14 || offsetHours < -12 || offsetMinutes > 59 || offsetMinutes < -59) {
            return ResponseEntity.badRequest().body("Invalid offset");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
        AppUser user = AppUser.builder()
                .userId(userDetails.getId())
                .build();

        return ResponseEntity.ok(
                streakService.getAnalysisStreak(user, offsetHours, offsetMinutes, localeCode)
        );
    }

    @GetMapping("/get-learned-data-in-client-time-v2")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> getLearnedDataInClientTimeV2(@RequestParam(value = "locale", required = false, defaultValue = "en-US")
                                                 String localeCode) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
        AppUser user = AppUser.builder()
                .userId(userDetails.getId())
                .build();

        return ResponseEntity.ok(
                streakService.getAnalysisStreakV2(user, localeCode)
        );
    }

    @GetMapping("/get-learned-data-by-month-year")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> getLearnedDataByMonthYear(@RequestParam("month") int month,
                                                @RequestParam("year") int year,
                                                @RequestParam(value = "locale", required = false, defaultValue = "en-US")
                                                String locale) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
        AppUser user = AppUser.builder()
                .userId(userDetails.getId())
                .build();
        return ResponseEntity.ok(streakService.getLearnedDetails(user, month, year, locale));
    }

    @GetMapping("/get-all-learned-data")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> getAllLearnedData(
            @RequestParam(value = "locale", required = false, defaultValue = "en-US")
            String localeCode
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
        AppUser user = AppUser.builder()
                .userId(userDetails.getId())
                .build();
        return ResponseEntity.ok(streakService.getAllLearnedDate(user, localeCode));
    }

    @PatchMapping("/update-streak-data")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> updateStreakData(@Valid @RequestBody StreakRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
        AppUser user = AppUser.builder()
                .userId(userDetails.getId())
                .build();
        boolean resultUpdate = streakService.generateStreak(user, request.getTimeFromClient());
        if (resultUpdate) {
            return ResponseEntity.ok("Streak updated");
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @GetMapping("/get-learned-data-by-date-range")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> getLearnedDataByDateRange(
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam("startDate") LocalDate startDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam(value = "locale", required = false, defaultValue = "en-US")
            String locale,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "40") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
        AppUser user = AppUser.builder()
                .userId(userDetails.getId())
                .build();
        return ResponseEntity.ok(streakService.getLearnedByDateRange(user, startDate, endDate, locale, page, size));
    }
}
