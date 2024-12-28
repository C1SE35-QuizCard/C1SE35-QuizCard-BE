package com.example.quizcards.controller.admin;

import com.example.quizcards.service.IAppUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quizcards.dto.IAppUserDTO;
import com.example.quizcards.dto.request.AppUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private IAppUserService appUserService;

    @GetMapping("/user-list")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers(@RequestParam(value = "page", required = false) Integer pages,
                                         PagedResourcesAssembler<IAppUserDTO> assembler) {
        try {
            if (pages == null) { pages = 0; }
            Page<IAppUserDTO> data = appUserService.getAllUsers(pages);
            System.out.println("Requested page: " + pages);
            return ResponseEntity.ok(assembler.toModel(data));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "message", "An unknown error occurred."
                    ));
        }
    }

    @GetMapping("/data")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(appUserService.getAllUsers());
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
}
