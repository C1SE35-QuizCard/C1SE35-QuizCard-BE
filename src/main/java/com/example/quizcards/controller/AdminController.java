package com.example.quizcards.controller;

import com.example.quizcards.dto.IAppUserDTO;
import com.example.quizcards.dto.request.AppUserRequest;
import com.example.quizcards.service.IAppUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

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

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> detailUser(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(appUserService.detailUser(userId));
    }

    @PutMapping("/update-user/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId, @Valid @RequestBody AppUserRequest request) {
        return ResponseEntity.ok().body(appUserService.updateAppUser(userId, request));
    }

    @DeleteMapping("/delete-user/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
        appUserService.deleteAppUser(userId);
        return ResponseEntity.ok().build();
    }
}
