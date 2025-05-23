package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.CategorySubscriptionRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.CategorySubscription;
import com.example.quizcards.entities.plans.PlansName;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.repository.ICategorySubscriptionRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ICategorySubscriptionService;
import com.example.quizcards.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CategorySubscriptionServiceImpl implements ICategorySubscriptionService {
    @Autowired
    private ICategorySubscriptionRepository categorySubscriptionRepository;

    @Autowired
    private RedisUtils redisUtils;

    private final Map<String, String> mappingData =
            Map.of(
                    RoleName.ROLE_FREE_USER.name(), "Free",
                    RoleName.ROLE_PREMIUM_USER.name(), "Premium",
                    RoleName.ROLE_ADMIN.name(), "Admin"
            );

    @Override
    public ResponseEntity<?> getBenefitByRoles() {
        try {
            CategorySubscription subscription = getCategorySubscriptionBaseOfRoles();
            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved benefits", 
                HttpStatus.OK, subscription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving benefits: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> getSubscriptionByRoles() {
        try {
            CategorySubscription subscription = getCategorySubscriptionBaseOfRoles();
            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved subscription", 
                HttpStatus.OK, subscription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving subscription: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<CategorySubscription> subscriptions = categorySubscriptionRepository.findAll();
            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved all subscriptions", 
                HttpStatus.OK, subscriptions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving subscriptions: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> createSubscription(CategorySubscriptionRequest request) {
        try {
            CategorySubscription subscription = new CategorySubscription();
            subscription.setName(request.getName());
            subscription.setPrice(request.getPrice());
            subscription.setDescription(request.getDescription());
            subscription.setMaxSetsPerDay(request.getMaxSetsPerDay());
            subscription.setMaxSetsFlashcards(request.getMaxSetsFlashcards());
            subscription.setMaxFlashcardsPerSet(request.getMaxFlashcardsPerSet());
            subscription.setMaxRoomsCreatePerDay(request.getMaxRoomsCreatePerDay());
            subscription.setMaxTermsPerRoom(request.getMaxTermsPerRoom());
            subscription.setExpiredMonth(request.getExpiredMonth());

            CategorySubscription savedSubscription = categorySubscriptionRepository.save(subscription);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Successfully created subscription", 
                        HttpStatus.CREATED, savedSubscription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error creating subscription: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> updateSubscription(Long id, CategorySubscriptionRequest request) {
        try {
            CategorySubscription subscription = categorySubscriptionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));

            subscription.setName(request.getName());
            subscription.setPrice(request.getPrice());
            subscription.setDescription(request.getDescription());
            subscription.setMaxSetsPerDay(request.getMaxSetsPerDay());
            subscription.setMaxSetsFlashcards(request.getMaxSetsFlashcards());
            subscription.setMaxFlashcardsPerSet(request.getMaxFlashcardsPerSet());
            subscription.setMaxRoomsCreatePerDay(request.getMaxRoomsCreatePerDay());
            subscription.setMaxTermsPerRoom(request.getMaxTermsPerRoom());
            subscription.setExpiredMonth(request.getExpiredMonth());

            CategorySubscription updatedSubscription = categorySubscriptionRepository.save(subscription);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated subscription", 
                HttpStatus.OK, updatedSubscription));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), HttpStatus.NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating subscription: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> deleteSubscription(Long id) {
        try {
            CategorySubscription subscription = categorySubscriptionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
            
            categorySubscriptionRepository.delete(subscription);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted subscription", 
                HttpStatus.OK, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), HttpStatus.NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error deleting subscription: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> getSubscriptionById(Long id) {
        try {
            CategorySubscription subscription = categorySubscriptionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
            
            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved subscription", 
                HttpStatus.OK, subscription));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), HttpStatus.NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving subscription: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public CategorySubscription getCategorySubscriptionBaseOfRoles() throws ResourceNotFoundException {
        return categorySubscriptionRepository.findByName("Free Plan")
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "name", "Free Plan"));
    }

    @Override
    public CategorySubscription getBenefitByName(UserPrincipal up, Boolean searchingRegex) {
        // cache is get user id and category subscription on redis and cache on 15 minutes
        String benefitName = mappingData.getOrDefault(up.getRolesBaseAuthorities().getFirst(), "unknown");
        if (!searchingRegex) {
            benefitName = "^" + benefitName + "$";
        }
        String cacheKey = "user:" + up.getId() + ":category_subscription:" + benefitName;
        Set<CategorySubscription> cachedSubscription = redisUtils.getFromSet(cacheKey, CategorySubscription.class);
        if (!cachedSubscription.isEmpty()) {
            return cachedSubscription.stream().toList().getFirst();
        }
        CategorySubscription cs = categorySubscriptionRepository.findByPatternName(benefitName);
        if (cs != null) {
            redisUtils.saveToSet(cacheKey, cs, 15 * 60, TimeUnit.SECONDS);
            return cs;
        }
        return null;
    }
}
