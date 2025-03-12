package com.example.quizcards.controller.admin;

import com.example.quizcards.dto.IAppUserDTO;
import com.example.quizcards.dto.request.AppUserRequest;
import com.example.quizcards.service.IAppUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
    @Autowired
    private IAppUserService appUserService;


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


//    @Async("taskExecutorc")
//    @GetMapping("/data")
//    public CompletableFuture<ResponseEntity<List<IAppUserDTO>>> getAllUsers() {
//        return appUserService.getAllUsers()
//                .thenApply(ResponseEntity::ok)
//                .exceptionally(ex -> {
//                    // Xử lý ngoại lệ nếu cần
//                    return ResponseEntity.internalServerError().body(null);
//                });
//    }

//    @GetMapping("/test")
////    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
//    public CompletableFuture<ResponseEntity<String>> testAsync() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Controller Authentication: " + (authentication != null ? authentication.getName() : "No Authentication"));
//        return appUserService.testAsync()
//                .thenApply(ResponseEntity::ok); // Đóng gói dữ liệu vào ResponseEntity
//    }


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
