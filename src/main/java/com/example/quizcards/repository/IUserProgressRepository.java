package com.example.quizcards.repository;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.response.ProgressResponse;
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

    // What !?
    // Cái này cho admin đúng không ?
    // Chỉ có trả về các trường như username, avatar, ..., mới cho admin thôi, cho user thì chắc không cần
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
                        up.marked_for_attention as status_mark,
                        f.image_url
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

    @Modifying
    @Transactional
    @Query(value = """
            delete up
            FROM user_progress up
            join flashcards f on up.card_id = f.card_id
            where up.user_id = :user_id and f.set_id = :set_id
            """, nativeQuery = true)
    void deleteUserProgressByUserAndSetId(@Param("user_id") Long userId,
                                          @Param("set_id") Long setId);


    @Query(value = """
            select u.progress_type, u.marked_for_attention, u.user_id, u.card_id
            from user_progress u
            where u.progress_id = :progress_id
            """, nativeQuery = true)
    IProgressDTO findUserProgressById(@Param("progress_id") Long progressId);

    @Query(value = """
            select u.progress_id,
                   u.progress_type as progress_type,
                   u.marked_for_attention as is_attention,
                   u.user_id, u.card_id
            from user_progress u
            join flashcards f on u.card_id = f.card_id
            where u.user_id = :user_id and f.set_id = :set_id
            """, nativeQuery = true)
    List<IProgressDTO> findUserProgressBySetIdAndUserId(@Param("set_id") Long setId,
                                                        @Param("user_id") Long userId);


    @Query(value = """
            select count(u.progress_id)
            from user_progress u
            where u.user_id = :user_id and u.card_id = :card_id
            """, nativeQuery = true)
    int existsByUserIdAndCardId(@Param("user_id") Long userId, @Param("card_id") Long cardId);

    @Query(value = """
            SELECT CASE WHEN EXISTS (
                SELECT 1
                FROM user_progress u
                WHERE u.user_id = :user_id AND u.card_id = :card_id
            ) THEN 1 ELSE 0 END AS result;
            """, nativeQuery = true)
    int existsByUserIdAndCardId_2(@Param("user_id") Long userId, @Param("card_id") Long cardId);

    @Query(value = """
            select COUNT(u.progress_id)
            from user_progress u
            where u.user_id = :user_id and u.card_id = :card_id and u.progress_id <> :progress_id
            """, nativeQuery = true)
    int existsByUserIdAndCardIdAndNotId(@Param("user_id") Long userId, @Param("card_id") Long cardId, @Param("progress_id") Long progressId);
}
