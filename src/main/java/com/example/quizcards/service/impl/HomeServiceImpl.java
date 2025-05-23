package com.example.quizcards.service.impl;

import com.example.quizcards.dto.FlashcardSetDTO;
import com.example.quizcards.dto.ICategorySetFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.response.FreeUserProfileResponse;
import com.example.quizcards.dto.response.HomeDataGuessUserResponse;
import com.example.quizcards.dto.response.HomeDataUserResponse;
import com.example.quizcards.dto.response.ITopCreatorsResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    @Qualifier("securityContextExecutor")
    @Autowired
    private AsyncTaskExecutor taskExecutor;

    private String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    //    private ResponseEntity<HomeDataUserResponse> getHomeData(Long userId) {
//        AppUser au = appUserService.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
//
//        List<ISetFlashcardDTO> setsRecentAccessed = setService.loadTop10RecentSetFlashcards(userId);
//        List<ISetFlashcardDTO> setsRelevantCategory = new ArrayList<>();
//        List<ICategorySetFlashcardDTO> categoryMostAccessed = categorySetFlashcardService.findTop1MostAccessedCategory();
//
//        if (!categoryMostAccessed.isEmpty()) {
//            setsRelevantCategory = setService.loadTop10RelevantByCategory(categoryMostAccessed.get(0).getCategoryId(), userId);
//        }
//
//        List<FlashcardSetDTO> setsPopular = setService.loadTop10PopularFlashcardSets(userId);
//        List<ITopCreatorsResponse> topCreators = setService.loadTop10PopularCreators();
//
//        FreeUserProfileResponse personalData = new FreeUserProfileResponse(
//                au.getUserId(),
//                au.getFirstName(),
//                au.getLastName(),
//                au.getEmail(),
//                au.getUsername(),
//                au.getAvatar()
//        );
//
//        HomeDataUserResponse response = HomeDataUserResponse
//                .builder()
//                .setsRecentAccessed(setsRecentAccessed)
//                .setsRelevantCategory(setsRelevantCategory)
//                .setsPopular(setsPopular)
//                .topCreators(topCreators)
//                .personData(personalData)
//                .roleName(au.getRole().getRoleName())
//                .relevantCategory(categoryMostAccessed.isEmpty() ? "" : categoryMostAccessed.get(0).getCategoryName())
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
    private ResponseEntity<HomeDataUserResponse> getHomeData(Long userId) {
        // 1. Lấy user ngay lập tức (ít tốn thời gian)
        AppUser au = appUserService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 2. Khởi tạo các Future để chạy song song
        Future<List<ISetFlashcardDTO>> futRecent = taskExecutor.submit(
                () -> setService.loadTop10RecentSetFlashcards(userId)
        );
        Future<List<ICategorySetFlashcardDTO>> futCategories = taskExecutor.submit(
                () -> categorySetFlashcardService.findTop1MostAccessedCategory()
        );
        Future<List<FlashcardSetDTO>> futPopular = taskExecutor.submit(
                () -> setService.loadTop10PopularFlashcardSets(userId)
        );
        Future<List<ITopCreatorsResponse>> futCreators = taskExecutor.submit(
                () -> setService.loadTop10PopularCreators()
        );

        List<ISetFlashcardDTO> setsRecent;
        List<ICategorySetFlashcardDTO> categories;
        List<FlashcardSetDTO> setsPopular;
        List<ITopCreatorsResponse> topCreators;
        try {
            setsRecent = futRecent.get();
            categories = futCategories.get();
            setsPopular = futPopular.get();
            topCreators = futCreators.get();
        } catch (InterruptedException | ExecutionException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lỗi khi tải dữ liệu Home page", ex);
        }

        // 3. Dựa vào category để lấy setsRelevant (có thể chạy tiếp song song nếu cần)
        List<ISetFlashcardDTO> setsRelevant = new ArrayList<>();
        if (!categories.isEmpty()) {
            Long catId = categories.get(0).getCategoryId();
            try {
                Future<List<ISetFlashcardDTO>> futRelevant = taskExecutor.submit(
                        () -> setService.loadTop10RelevantByCategory(catId, userId)
                );
                setsRelevant = futRelevant.get();
            } catch (InterruptedException | ExecutionException ex) {
                Thread.currentThread().interrupt();
                // Log lỗi và để trống danh sách relevant
            }
        }

        // 4. Xây dựng DTO cá nhân
        FreeUserProfileResponse personalData = new FreeUserProfileResponse(
                au.getUserId(),
                au.getFirstName(),
                au.getLastName(),
                au.getEmail(),
                au.getUsername(),
                au.getAvatar()
        );

        // 5. Build và trả về response
        HomeDataUserResponse dto = HomeDataUserResponse.builder()
                .setsRecentAccessed(setsRecent)
                .setsRelevantCategory(setsRelevant)
                .setsPopular(setsPopular)
                .topCreators(topCreators)
                .personData(personalData)
                .roleName(au.getRole().getRoleName())
                .relevantCategory(categories.isEmpty() ? "" : categories.get(0).getCategoryName())
                .build();

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<HomeDataGuessUserResponse> getGuestUserHomeData(Long userId) {
        List<ISetFlashcardDTO> dataList = setService.getAllLimit(10);
        return ResponseEntity.ok(HomeDataGuessUserResponse.builder().listSets(dataList).build());
//        HomeDataGuessUserResponse response = new HomeDataGuessUserResponse(setService.loadTop10PopularFlashcardSets(userId));
//        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getHomeData(Long userId, HttpServletResponse response) {
        return getHomeData(userId);
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
