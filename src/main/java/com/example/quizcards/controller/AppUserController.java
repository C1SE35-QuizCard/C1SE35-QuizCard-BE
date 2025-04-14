package com.example.quizcards.controller;

import com.example.quizcards.dto.request.app_user_request.*;
import com.example.quizcards.dto.request.email.ConfirmEmailParam;
import com.example.quizcards.dto.request.email.OtpParam;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IAppUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserController {
    IAppUserService appUserService;

    @Value("${app.critical-information.changed-email-after-hours}")
    @NonFinal
    Integer changedEmailAfterHours;

    @GetMapping("/data")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsersWithPagination(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(appUserService.getAllUsersWithPagination(page, size));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "message", "An unknown error occurred."
                    ));
        }
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> detailUser(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(appUserService.detailUser(userId));
    }

    @PostMapping("/create-user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody AppUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appUserService.createAppUser(request));
    }

    @PutMapping("/update-user/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId, @Valid @RequestBody AppUserRequest request) {
        return ResponseEntity.ok(appUserService.updateAppUser(userId, request));
    }

    @DeleteMapping("/delete-user/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
        appUserService.deleteAppUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-basic-information")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> changeBasicInformation(@Valid @RequestBody BasicAppUserInformationRequest request) {
        appUserService.changeBasicInformation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/try-confirm-critical-information")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> isConfirmCriticalInformation(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();

        boolean isConfirmed = appUserService.checkConfirmedForChangeCriticalInformation(up.getId(), request);

        if (!isConfirmed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-confirm-critical-information")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> getConfirmCriticalInformation(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();

        Long userId = up.getId();

        if (appUserService.checkConfirmedForChangeCriticalInformation(userId, request)) {
            // accepted: Để dành cho việc người dùng đã xác thực rồi, không cần gửi lại nữa
            return ResponseEntity.accepted().build();
        }

        OtpParam otpData = appUserService.getOtpForChangeCriticalInformation(userId, request);

        if (otpData != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            // Kiểm tra xem việc gửi OTP đã quá 1 phút hay chưa
            if (otpData.getSentAt().plusMinutes(1).isAfter(currentTime)) {
                return ResponseEntity.status(HttpStatus.TOO_EARLY)
                        .header(HttpHeaders.RETRY_AFTER,
                                String.valueOf(Duration.between(
                                        otpData.getSentAt().plusMinutes(1), currentTime).getSeconds())
                        )
                        .body(Map.of(
                                "message", "Please wait for 1 minute before sending another OTP."
                        ));
            }
        }

        appUserService.createNewOtpForChangeCriticalInformation(up, request);
        // ok: Xác nhận OTP đã được gửi thành công
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm-critical-information")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> confirmCriticalInformation(
            @Valid @RequestBody ConfirmChangeCriticalInformationRequest confirmRequest,
            HttpServletRequest request) {
        appUserService.confirmModifyCriticalInformation(confirmRequest, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-email")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> changeEmail(@Valid @RequestBody EmailAppUserRequest emailRequest,
                                         HttpServletRequest request) {
        appUserService.changeEmail(emailRequest, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm-key/{key}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> confirmKeyToChangeEmail(@PathVariable String key) {
        appUserService.confirmKeyToChangeEmail(key);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/try-change-email")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> checkChangeEmail() {
        ConfirmEmailParam emailParam = appUserService.getEmailConfirmCriticalInformation();
        LocalDateTime lastSendEmail = emailParam.getSentAt();
        LocalDateTime currentTime = LocalDateTime.now();
        if (lastSendEmail != null && lastSendEmail.plusHours(changedEmailAfterHours).isAfter(currentTime)) {
            return ResponseEntity.status(HttpStatus.TOO_EARLY)
                    .header(HttpHeaders.RETRY_AFTER,
                            String.valueOf(Duration.between(lastSendEmail.plusHours(changedEmailAfterHours),
                                    currentTime).getSeconds()))
                    .header("X-User-Email", emailParam.getNewEmail())
                    .body(Map.of(
                            "message",
                            MessageFormat.format("Please wait for {0} hours before change new email.",
                                    Duration.between(lastSendEmail.plusHours(changedEmailAfterHours),
                                            currentTime).getSeconds() / 3600)
                    ));
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordAppUserRequest passwordAppUserRequest,
                                            HttpServletRequest request) {
        appUserService.changePassword(passwordAppUserRequest, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public CompletableFuture<ResponseEntity<?>> testAsync() {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().body((new Random()).nextInt()));
    }
}
