package com.example.quizcards.repository;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.entities.UserProgress;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserProgressRepository extends JpaRepository<UserProgress, Long> {

    @Query(value = """
            select 
                s.set_id, 
                s.title,
                a.avatar,
                a.user_name,
                count(f.card_id) as total_cards,
                sum(case when up.progress_type = 1 then 1 else 0 end) as completed_cards,
                sum(case when up.progress_type = 0 or up.progress_type is null then 1 else 0 end) as uncompleted_cards
            from 
                set_flashcards s
            join 
                app_users a on a.user_id = s.user_id
            join 
                flashcards f on s.set_id = f.set_id
            left join 
                user_progress up on f.card_id = up.card_id and up.user_id = :user_id
            where 
                s.user_id = :user_id
            group by 
                s.set_id, s.title, a.avatar, a.user_name
            """, nativeQuery = true)
    List<IUserProgressDTO> findUserSetProgress(@Param("user_id") Long userId);

    @Query(value = """
            select
                s.set_id,
                s.title,
                a.avatar,
                a.user_name,
                f.card_id,
                f.question,
                f.answer,
                up.progress_type as status_progress,
                up.marked_for_attention as status_mark
            from
                flashcards f
            join
                set_flashcards s on s.set_id = f.set_id
            join
                app_users a on a.user_id = s.user_id
            left join
                user_progress up on f.card_id = up.card_id and up.user_id =:userId
            where f.set_id =:setId
    """, nativeQuery = true)
    List<IFlashcardProgressDTO> findFlashcardsProgressBySetId(@Param("setId") Long setId, @Param("userId") Long userId);


    @Modifying
    @Transactional
    @Query(value = """
            insert into user_progress(progress_type, marked_for_attention, user_id, card_id)
            values (:progress_type, :marked_for_attention, :user_id, :card_id)
            """, nativeQuery = true)
    void createUserProgress(@Param("progress_type") Boolean progressType,
                            @Param("marked_for_attention") Boolean isAttention,
                            @Param("user_id") Long userId,
                            @Param("card_id") Long cardId);

    @Modifying
    @Transactional
    @Query(value = """
            delete from user_progress u
            where u.progress_id = :progress_id
            """, nativeQuery = true)
    void deleteUserProgressById(@Param("progress_id") Long progressId);

    @Modifying
    @Transactional
    @Query(value = """
            update user_progress u
            set u.progress_type = :progress_type, u.marked_for_attention = :marked_for_attention, u.marked_for_attention = :marked_for_attention, u.user_id = :user_id, u.card_id = :card_id
            where u.progress_id = :progress_id
            """, nativeQuery = true)
    void updateUserProgress(@Param("progress_id") Long progressId,
                            @Param("progress_type") Boolean progressType,
                            @Param("marked_for_attention") Boolean isAttention,
                            @Param("user_id") Long userId,
                            @Param("card_id") Long cardId);


    @Query(value = """
            select u.progress_type, u.marked_for_attention, u.user_id, u.card_id
            from user_progress u
            where u.progress_id = :progress_id
            """, nativeQuery = true)
    IProgressDTO findUserProgressById(@Param("progress_id") Long progressId);

    @Query(value = """
            select count(u.progress_id)
            from user_progress u
            where u.user_id = :user_id and u.card_id = :card_id
            """, nativeQuery = true)
    int existsByUserIdAndCardId(@Param("user_id") Long userId, @Param("card_id") Long cardId);

    @Query(value = """
            select COUNT(u.progress_id)
            from user_progress u
            where u.user_id = :user_id and u.card_id = :card_id and u.progress_id <> :progress_id
            """, nativeQuery = true)
    int existsByUserIdAndCardIdAndNotId(@Param("user_id") Long userId, @Param("card_id") Long cardId, @Param("progress_id") Long progressId);

}
