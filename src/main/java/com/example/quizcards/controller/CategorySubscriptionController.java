package com.example.quizcards.controller;

import com.example.quizcards.dto.request.CategorySubscriptionRequest;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ICategorySubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/v1/category-subscription")
public class CategorySubscriptionController {
    @Autowired
    private ICategorySubscriptionService categorySubscriptionService;



    @GetMapping("/my-subscription")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getMySubscription() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();
        try {
            return ResponseEntity.ok().body(categorySubscriptionService.getBenefitByName(up, true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving subscription: " + e.getMessage());
        }
    }

    // Public APIs
    @GetMapping("/current-benefit")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getCurrentBenefit() {
        return categorySubscriptionService.getBenefitByRoles();
    }

    @GetMapping("/current-subscription")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getCurrentSubscription() {
        return categorySubscriptionService.getSubscriptionByRoles();
    }

    @GetMapping("/all-subscriptions")
    public ResponseEntity<?> getAllSubscriptions() {
        return categorySubscriptionService.getAll();
    }

    // Admin APIs
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createSubscription(@Valid @RequestBody CategorySubscriptionRequest request) {
        return categorySubscriptionService.createSubscription(request);
    }

    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateSubscription(@PathVariable Long id,
                                                @Valid @RequestBody CategorySubscriptionRequest request) {
        return categorySubscriptionService.updateSubscription(id, request);
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteSubscription(@PathVariable Long id) {
        return categorySubscriptionService.deleteSubscription(id);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getSubscriptionById(@PathVariable Long id) {
        return categorySubscriptionService.getSubscriptionById(id);
    }
}
