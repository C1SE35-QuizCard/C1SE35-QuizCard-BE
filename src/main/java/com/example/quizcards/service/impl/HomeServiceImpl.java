package com.example.quizcards.service.impl;

import com.example.quizcards.dto.ICategorySetFlashcardDTO;
import com.example.quizcards.dto.IDeadlineReminderDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.response.HomeDataFreeUserResponse;
import com.example.quizcards.dto.response.TopCreatorsResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomeServiceImpl implements IHomeService {
    @Autowired
    private ISetFlashcardService setService;

    @Autowired
    private ICategorySetFlashcardService categorySetFlashcardService;

    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private IDeadlineReminderService deadlineReminderService;

    private String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    @Override
    public ResponseEntity<HomeDataFreeUserResponse> getFreeUserHomeData(Long userId) {
        List<ISetFlashcardDTO> setsRecentAccessed = setService.loadTop10RecentSetFlashcards(userId);
        String relevantCategory = null;
        List<ISetFlashcardDTO> setsRelevantCategory = new ArrayList<>();
        List<ICategorySetFlashcardDTO> categoryMostAccessed =
                categorySetFlashcardService.findTop1MostAccessedCategory(userId);
        if (!categoryMostAccessed.isEmpty()) {
            relevantCategory = categoryMostAccessed.get(0).getCategoryName();
            setsRelevantCategory = setService.loadTop10RelevantByCategory(categoryMostAccessed.get(0).getCategoryId(),
                    userId);
        }
        List<ISetFlashcardDTO> setsPopular = setService.loadTop10PopularFlashcardSets(userId);
        List<TopCreatorsResponse> topCreators = setService.loadTop10PopularCreators();
        List<IDeadlineReminderDTO> deadlineReminderDTOS = deadlineReminderService.getDeadlineReminderByUserId(userId);

        HomeDataFreeUserResponse homeDataFreeUserResponse = new HomeDataFreeUserResponse(setsRecentAccessed,
                setsRelevantCategory, setsPopular, topCreators, relevantCategory);
        return ResponseEntity.ok(homeDataFreeUserResponse);
    }

    @Override
    public ResponseEntity<?> getHomeData(Authentication authentication, HttpServletResponse response) {
        String userRole = ROLE_ANONYMOUS;
        Long userId = null;
        if (authentication != null) {
            UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
            AppUser user = appUserService.findById(up.getId()).orElse(null);
            if (user != null) {
                userRole = user.getRole().getRoleName();
                userId = up.getId();
            }
        }
        if (userRole.equals(RoleName.ROLE_FREE_USER.name())) {
            return getFreeUserHomeData(userId);
        } else if (userRole.equals(RoleName.ROLE_PREMIUM_USER.name())) {
            throw new UnsupportedOperationException("Not implemented yet");
        } else if (userRole.equals(RoleName.ROLE_ADMIN.name())) {
            throw new UnsupportedOperationException("Not implemented yet");
        } else {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }
}
