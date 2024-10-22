package com.example.quizcards.repository;

import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.entities.UserFlashcardSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFlashcardSettingRepository extends JpaRepository<UserFlashcardSetting, Long> {
    Optional<UserFlashcardSetting> findByUserAndAndSetFlashcard(AppUser user, SetFlashcard setFlashcard);
}
