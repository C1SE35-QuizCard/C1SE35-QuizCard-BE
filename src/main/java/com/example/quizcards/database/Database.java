package com.example.quizcards.database;

import com.example.quizcards.dto.ICategorySetFlashcardDTO;
import com.example.quizcards.dto.ITestModeDTO;
import com.example.quizcards.entities.AppRole;
import com.example.quizcards.entities.CategorySetFlashcard;
import com.example.quizcards.entities.CategorySubscription;
import com.example.quizcards.entities.TestMode;
import com.example.quizcards.entities.plans.PlansName;
import com.example.quizcards.entities.questionTypes.QTypes;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.repository.IAppRoleRepository;
import com.example.quizcards.repository.ICategorySetFlashcardRepository;
import com.example.quizcards.repository.ICategorySubscriptionRepository;
import com.example.quizcards.repository.ITestModeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Configuration
public class Database {
    // logger
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    @Bean
    CommandLineRunner initRoles(IAppRoleRepository repo) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                List<String> roles = List.of(
                        RoleName.ROLE_FREE_USER.name(),
                        RoleName.ROLE_PREMIUM_USER.name(),
                        RoleName.ROLE_ADMIN.name());
                for (String role : roles) {
                    Optional<AppRole> chkRole = repo.findByRoleName(role);
                    if (chkRole.isPresent()) {
                        logger.info(String.format("Role: %s valid", role));
                    } else {
                        AppRole roleEntity = new AppRole();
                        roleEntity.setRoleName(role);
                        logger.info("Insert role: " + repo.save(roleEntity));
                    }
                }
            }
        };
    }

    @Bean
    CommandLineRunner initCategory(ICategorySetFlashcardRepository repo) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                List<String> categories = List.of(
                        "Mathematics",
                        "Physics",
                        "Chemistry",
                        "Biology",
                        "History",
                        "Geography",
                        "English Literature",
                        "Computer Science",
                        "Physical Education",
                        "Economics",
                        "Sociology",
                        "Psychology",
                        "Music",
                        "Art",
                        "Philosophy",
                        "Political Science",
                        "Law",
                        "Vietnamese Language",
                        "Environmental Science",
                        "Astronomy",
                        "Language"
                );
                for (String category : categories) {
                    List<ICategorySetFlashcardDTO> chkCategory = repo.findByCategoryName(category);
                    if (!chkCategory.isEmpty()) {
                        logger.info(String.format("Category: %s valid", category));
                    } else {
                        CategorySetFlashcard categoryEntity = new CategorySetFlashcard();
                        categoryEntity.setCategoryName(category);
                        logger.info("Insert category: " + repo.save(categoryEntity));
                    }
                }
            }
        };
    }


    @Bean
    CommandLineRunner initCategorySubscription(ICategorySubscriptionRepository repo) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
//                new CategorySubscription(null,
//                        PlansName.FREE_PLAN.getMessage(),
//                        BigDecimal.ZERO,
//                        "Free plans",
//                        1,
//                        10,
//                        1,
//                        2,
//                        20,
//                        0,
//                        null,
//                        null),
                List<CategorySubscription> subscriptions = List.of(
                        new CategorySubscription(null,
                                PlansName.FREE_PLAN.getMessage(),
                                BigDecimal.ZERO,
                                "Free plans",
                                50,
                                500,
                                5,
                                2,
                                20,
                                0,
                                null,
                                null),
                        new CategorySubscription(null,
                                PlansName.PREMIUM_PLAN.getMessage(),
                                new BigDecimal("50000"),
                                "Premium plans",
                                Integer.MAX_VALUE,
                                2000,
                                Integer.MAX_VALUE,
                                Integer.MAX_VALUE,
                                50,
                                1,
                                null,
                                null)
                );
                for (CategorySubscription category : subscriptions) {
                    Optional<CategorySubscription> data = repo.findByName(category.getName());
                    if (data.isPresent()) {
                        logger.info(String.format("Subscription: %s valid", category.getName()));
                    } else {
                        logger.info("Insert subscription: " + repo.save(category));
                    }
                }
            }
        };
    }

    @Bean
    CommandLineRunner initTestModeTypes(ITestModeRepository repo) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                List<TestMode> subscriptions = List.of(
                        TestMode.builder()
                                .testModeName(QTypes.MULTIPLE.name())
                                .build(),
                        TestMode.builder()
                                .testModeName(QTypes.ESSAY.name())
                                .build()
                );
                for (TestMode test : subscriptions) {
                    List<ITestModeDTO> data = repo.findByTestModeName(test.getTestModeName());
                    if (!data.isEmpty()) {
                        logger.info(String.format("Test mode: %s valid", test.getTestModeName()));
                    } else {
                        logger.info("Insert test mode: " + repo.save(test));
                    }
                }
            }
        };
    }

}