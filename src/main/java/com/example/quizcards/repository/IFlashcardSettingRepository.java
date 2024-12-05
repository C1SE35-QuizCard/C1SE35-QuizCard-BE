package com.example.quizcards.repository;

import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.entities.UserFlashcardSetting;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IFlashcardSettingRepository extends JpaRepository<UserFlashcardSetting, Long> {
    Optional<UserFlashcardSetting> findByUserAndAndSetFlashcard(AppUser user, SetFlashcard setFlashcard);

    @Query(value = """
            SELECT 1
            FROM user_flashcard_settings ufs
            WHERE ufs.set_id =:setId
            AND ufs.user_id =:userId
            LIMIT 1;
            """, nativeQuery = true)
    Long existsUserFlashcardSetting(
            @Param("setId") Long setId,
            @Param("userId") Long userId
    );

    @Modifying
    @Transactional
    @Query(value = """
            insert into user_flashcard_settings(last_accessed,set_id,user_id) values(now(),:setId,:userId);
            """, nativeQuery = true)
    void save(Long userId, Long setId);

}
