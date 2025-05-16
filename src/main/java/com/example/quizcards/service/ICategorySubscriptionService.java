package com.example.quizcards.service;

import com.example.quizcards.dto.request.CategorySubscriptionRequest;
import com.example.quizcards.entities.CategorySubscription;
import com.example.quizcards.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

public interface ICategorySubscriptionService {
    ResponseEntity<?> getBenefitByRoles();
    ResponseEntity<?> getSubscriptionByRoles();
    ResponseEntity<?> getAll();
    ResponseEntity<?> createSubscription(CategorySubscriptionRequest request);
    ResponseEntity<?> updateSubscription(Long id, CategorySubscriptionRequest request);
    ResponseEntity<?> deleteSubscription(Long id);
    ResponseEntity<?> getSubscriptionById(Long id);
    CategorySubscription getCategorySubscriptionBaseOfRoles() throws ResourceNotFoundException;
}
