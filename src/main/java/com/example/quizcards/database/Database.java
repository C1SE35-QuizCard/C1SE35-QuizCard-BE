package com.example.quizcards.database;

import com.example.quizcards.entities.AppRole;
import com.example.quizcards.entities.CategorySetFlashcard;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.repository.IAppRoleRepository;
import com.example.quizcards.repository.ICategorySetFlashcardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
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
                        logger.info("Insert data: " + repo.save(roleEntity));
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
                        "Math",
                        "LOL",
                        "Valorant",
                        "CSGO",
                        "Pubg",
                        "Dota2");
                for (String category : categories) {
                    Optional<CategorySetFlashcard> chkCategory = repo.findByCategoryName(category);
                    if (chkCategory.isPresent()) {
                        logger.info(String.format("Category: %s valid", category));
                    } else {
                        CategorySetFlashcard categoryEntity = new CategorySetFlashcard();
                        categoryEntity.setCategoryName(category);
                        logger.info("Insert data: " + repo.save(categoryEntity));
                    }
                }
            }
        };
    }
}