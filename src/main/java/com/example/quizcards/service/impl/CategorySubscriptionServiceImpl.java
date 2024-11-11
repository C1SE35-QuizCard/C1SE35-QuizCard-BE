package com.example.quizcards.service.impl;

import com.example.quizcards.dto.response.CategorySubscriptionResponse;
import com.example.quizcards.entities.CategorySubscription;
import com.example.quizcards.entities.plans.PlansName;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.repository.ICategorySubscriptionRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ICategorySubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorySubscriptionServiceImpl implements ICategorySubscriptionService {
    @Autowired
    private ICategorySubscriptionRepository categorySubscriptionRepository;

    @Override
    public CategorySubscriptionResponse getCategorySubscriptionBaseOfRoles() throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        return getCategorySubscriptionResponse(up);
    }

    @Override
    public CategorySubscriptionResponse getCategorySubscriptionBaseOfUserPrincipal(UserPrincipal up) throws ResourceNotFoundException {
        return getCategorySubscriptionResponse(up);
    }

    private CategorySubscriptionResponse getCategorySubscriptionResponse(UserPrincipal up) {
        List<String> roles = up.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String plansName;
        if (roles.contains(RoleName.ROLE_PREMIUM_USER.name()) || roles.contains(RoleName.ROLE_ADMIN.name())) {
            plansName = PlansName.PREMIUM_PLAN.getMessage();
        } else if (roles.contains(RoleName.ROLE_FREE_USER.name())) {
            plansName = PlansName.FREE_PLAN.getMessage();
        } else {
            plansName = null;
        }
        if (plansName == null) {
            return null;
        }
        CategorySubscription categorySubscription = categorySubscriptionRepository.findByName(plansName)
                .orElseThrow(() -> new ResourceNotFoundException("CategorySubscription", "id", plansName));
        return new CategorySubscriptionResponse(categorySubscription);
    }
}
