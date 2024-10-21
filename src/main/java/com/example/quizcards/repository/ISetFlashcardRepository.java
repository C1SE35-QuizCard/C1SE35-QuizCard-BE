package com.example.quizcards.repository;

import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.entities.SetFlashcard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISetFlashcardRepository extends JpaRepository<SetFlashcard, Long> {
    @Query(value = """
            select f.card_id, f.question, f.answer, f.image_url, f.is_approved, f.created_at, f.updated_at, s.title
            from flashcards f, set_flashcards s
            where f.set_id = s.set_id and s.set_id = :set_id
            """, nativeQuery = true)
    List<IFlashcardDTO> findAllFlashcardsBySetId(@Param("set_id") Long id); // cho tất cả mọi người

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as card_count
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and f.set_id = s.set_id and s.sharing_mode = true
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findAllSetFlashcards(); // cho tất cả mọi người

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as card_count
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and s.set_id = :set_id and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            """, nativeQuery = true)
    ISetFlashcardDTO findSetFlashcardsById(@Param("set_id") Long id);

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as card_count
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and s.user_id = :user_id and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findAllSetByUserId(@Param("user_id") Long userId);

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as card_count
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and s.sharing_mode = true and s.user_id = :user_id and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findAllSetPublicByUserId(@Param("user_id") Long userId);

    @Modifying
    @Transactional
    @Query(value = """
            insert into set_flashcards(title, description_set, created_at, updated_at, is_approved, is_anonymous, sharing_mode, user_id, category_id)
            values (:title, :description_set, now(), now(), :is_approved, :is_anonymous, :sharing_mode, :user_id, :category_id)
            """, nativeQuery = true)
    void createSetFlashcard(@Param ("title") String title,
                          @Param ("description_set") String descriptionSet,
                          @Param ("is_approved") Boolean isApproved,
                          @Param ("is_anonymous") Boolean isAnonymous,
                          @Param ("sharing_mode") Boolean sharingMode,
                          @Param ("user_id") Long userId,
                          @Param ("category_id") Long categoryId);

    @Modifying
    @Transactional
    @Query(value = """
            delete from set_flashcards s
            where s.set_id = :set_id
            """, nativeQuery = true)
    void deleteSetFlashcardById(@Param("set_id") Long setId);

    @Modifying
    @Transactional
    @Query(value = """
            update set_flashcards s
            set s.title = :title, s.description_set = :description_set, s.updated_at = now(), s.is_approved = :is_approved, s.is_anonymous = :is_anonymous, s.sharing_mode = :sharing_mode, s.user_id = :user_id, s.category_id = :category_id
            where s.set_id = :set_id
            """, nativeQuery = true)
    void updateSetFlashcard(@Param ("set_id") Long setId,
                          @Param ("title") String title,
                          @Param ("description_set") String descriptionSet,
                          @Param ("is_approved") Boolean isApproved,
                          @Param ("is_anonymous") Boolean isAnonymous,
                          @Param ("sharing_mode") Boolean sharingMode,
                          @Param ("user_id") Long userId,
                          @Param ("category_id") Long categoryId);

    @Query(value = """
        select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as card_count
        from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
        where s.user_id = a.user_id and s.category_id = c.category_id and s.sharing_mode = true and MATCH(s.title) AGAINST(:title IN BOOLEAN MODE) and f.set_id = s.set_id
        group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
        """, nativeQuery = true)
    List<ISetFlashcardDTO> searchByTitle(@Param("title") String title);

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as card_count
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and s.sharing_mode = true and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            order by s.updated_at DESC
            """, nativeQuery = true)
    List<ISetFlashcardDTO> sortByUpdatedDate();

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as card_count
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and s.sharing_mode = true and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findAllSetPublic();
}
