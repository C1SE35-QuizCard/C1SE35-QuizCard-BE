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
import java.util.stream.Collectors;

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

    //    public ResponseEntity<HomeDataGuessUserResponse> getGuestUserHomeData(Long userId) {
//        HomeDataGuessUserResponse response = new HomeDataGuessUserResponse(setService.loadTop10PopularFlashcardSets(userId));
//        return ResponseEntity.ok(response);
//List<ISetFlashcardDTO> allSets = setService.getAll();
//    List<ISetFlashcardDTO> dataList = new ArrayList<>();
//    Map<String, List<ISetFlashcardDTO>> dataMap = new HashMap<>();
//    for (ISetFlashcardDTO set : allSets) {
//        List<ISetFlashcardDTO> datas = dataMap.computeIfAbsent(set.getCategoryName(), k -> new ArrayList<>());
//        if (datas.size() < 10) {
//            datas.add(set);
//            dataList.add(set);
//        }
//    }
//    HomeDataGuessUserResponse response = HomeDataGuessUserResponse.builder()
//            .listSets(dataList).build();
//    return ResponseEntity.ok(response);
//    }
    public ResponseEntity<HomeDataGuessUserResponse> getGuestUserHomeData(Long userId) {
        List<ISetFlashcardDTO> dataList = setService.getAll().stream()
                .collect(Collectors.groupingBy(ISetFlashcardDTO::getCategoryName))
                .values().stream()
                .flatMap(list -> list.stream().limit(10))
                .collect(Collectors.toList());
        return ResponseEntity.ok(HomeDataGuessUserResponse.builder().listSets(dataList).build());
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
