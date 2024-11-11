package com.example.quizcards.service;


import com.example.quizcards.dto.response.CategorySubscriptionResponse;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.security.UserPrincipal;

public interface ICategorySubscriptionService {
    CategorySubscriptionResponse getCategorySubscriptionBaseOfRoles() throws ResourceNotFoundException;

    CategorySubscriptionResponse getCategorySubscriptionBaseOfUserPrincipal(UserPrincipal up) throws ResourceNotFoundException;
}
