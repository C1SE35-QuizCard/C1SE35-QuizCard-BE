package com.example.quizcards.repository;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.entities.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
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
                app_users a ON a.user_id = s.user_id
            join
                flashcards f ON s.set_id = f.set_id
            left join
                user_progress up ON f.card_id = up.card_id AND up.user_id = 6
            group by
                s.set_id, s.title, a.avatar, a.user_name
            having 
                count(up.card_id) > 0
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
                case when up.progress_type = 1 then 'completed' else 'uncompleted' end as status
            from 
                flashcards f
            join
                set_flashcards s on s.set_id = f.set_id
            join
                app_users a on a.user_id = s.user_id
            left join 
                user_progress up on f.card_id = up.card_id and up.user_id = :user_id
            where 
                f.set_id = :set_id
            """, nativeQuery = true)
    List<IFlashcardProgressDTO>findFlashcardsProgressBySetId(@Param("set_id") Long setId, @Param("user_id") Long userId);

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
            select count(1)
            from user_progress u
            where u.progress_id = :id
            """, nativeQuery = true)
    Integer countUserProgressesById(@Param("id") Long progressId);

    @Query(value = """
            select * from user_progress u
            where u.user_id = :user_id and u.card_id = :card_id
            """, nativeQuery = true)
    UserProgress findUserProgressByUserIdAndCardId(@Param("user_id") Long userId,
                                                   @Param("card_id") Long cardId);

    @Query(value = """
        SELECT f.card_id, 
               f.question, 
               f.answer, 
               f.image_url, 
               f.is_approved, 
               f.created_at, 
               f.updated_at,
        CASE WHEN up.progress_type = 1 THEN 'completed' ELSE 'uncompleted' END AS status
        FROM flashcards f
        LEFT JOIN user_progress up ON f.card_id = up.card_id AND up.user_id = :userId
        WHERE f.set_id = :setId
        """, nativeQuery = true)
    List<Object[]> findFlashcardsByUserIdAndSetId(Long userId, Long setId);
}
