package com.example.quizcards.service.impl;

import com.example.quizcards.dto.FlashcardSetDTO;
import com.example.quizcards.dto.ICategorySetFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.response.FreeUserProfileResponse;
import com.example.quizcards.dto.response.HomeDataFreeUserResponse;
import com.example.quizcards.dto.response.HomeDataGuessUserResponse;
import com.example.quizcards.dto.response.ITopCreatorsResponse;
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

    public ResponseEntity<HomeDataFreeUserResponse> getFreeUserHomeData(UserPrincipal up) {
        Long userId = up.getId();
        List<ISetFlashcardDTO> setsRecentAccessed = setService.loadTop10RecentSetFlashcards(userId);
        String relevantCategory = null;
        List<ISetFlashcardDTO> setsRelevantCategory = new ArrayList<>();
        List<ICategorySetFlashcardDTO> categoryMostAccessed = categorySetFlashcardService.findTop1MostAccessedCategory(userId);
//        if (!categoryMostAccessed.isEmpty()) {
//            relevantCategory = categoryMostAccessed.get(0).getCategoryName();
//            setsRelevantCategory = setService.loadTop10RelevantByCategory(categoryMostAccessed.get(0).getCategoryId(),
//                    userId);
//        }
        List<FlashcardSetDTO> setsPopular = setService.loadTop10PopularFlashcardSets(userId);
        List<ITopCreatorsResponse> topCreators = setService.loadTop10PopularCreators();
//        List<IDeadlineReminderDTO> deadlines = deadlineReminderService.getDeadlineReminderByUserId(userId);


        FreeUserProfileResponse personalData = new FreeUserProfileResponse(up.getId(), up.getFirstName(),
                up.getLastName(), up.getEmail(), up.getUsername(), up.getAvatar());

        HomeDataFreeUserResponse homeDataFreeUserResponse = new HomeDataFreeUserResponse(setsRecentAccessed,
                setsRelevantCategory, setsPopular, topCreators, personalData,
                up.getRolesBaseAuthorities().get(0));
        return ResponseEntity.ok(homeDataFreeUserResponse);
    }

    public ResponseEntity<HomeDataGuessUserResponse> getGuestUserHomeData(Long userId) {
        HomeDataGuessUserResponse response = new HomeDataGuessUserResponse(setService.loadTop10PopularFlashcardSets(userId));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getHomeDataFreeUser(Authentication authentication, HttpServletResponse response) {
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        return getFreeUserHomeData(up);
    }

    @Override
    public ResponseEntity<?> getHomeDataPremiumUser(Authentication authentication, HttpServletResponse response) {
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        return getFreeUserHomeData(up);
    }

    @Override
    public ResponseEntity<?> getHomeDataAdmin(Authentication authentication, HttpServletResponse response) {
        return null;
    }

    @Override
    public ResponseEntity<?> getHomeDataGuest(HttpServletResponse response) {
        return getGuestUserHomeData(-1L);
    }
}
