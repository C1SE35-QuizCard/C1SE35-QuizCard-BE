package com.example.quizcards.repository;

import com.example.quizcards.dto.ICategorySetFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.entities.CategorySetFlashcard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICategorySetFlashcardRepository extends JpaRepository<CategorySetFlashcard, Long> {
    @Query(value = """
            select c.category_id, c.category_name
            from category_set_flashcards c
            where c.category_id = :category_id
            """, nativeQuery = true)
    ICategorySetFlashcardDTO findCategorySetFlashcardById(@Param("category_id") Long categoryId);

    List<ICategorySetFlashcardDTO> findByCategoryName(String categoryName);

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.avatar, a.user_name ,c.category_name, COUNT(f.card_id) as card_count
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.category_id = c.category_id and s.category_id = :category_id and s.user_id = a.user_id and s.sharing_mode = true and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, c.category_name
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findAllSetFlashcardsByCategoryId(@Param("category_id") Long categoryId);

    @Query(value = """
            select c.category_id, c.category_name
            from category_set_flashcards c
            """, nativeQuery = true)
    List<ICategorySetFlashcardDTO> findAllCategorySetFlashcard();


    @Query(value = """
            select c.category_id, c.category_name
            from category_set_flashcards c
            join set_flashcards s on s.category_id = c.category_id
            join user_flashcard_settings ufs on s.set_id = ufs.set_id
            where ufs.user_id = :user_id
            group by c.category_id, c.category_name
            order by (count(c.category_id)) desc, ufs.last_accessed desc
            limit 1;
            """, nativeQuery = true)
    List<ICategorySetFlashcardDTO> findTop1MostAccessedCategory(@Param("user_id") Long userId);

    @Modifying
    @Transactional
    @Query(value = """
            insert into category_set_flashcards(category_name)
            values (:category_name)
            """, nativeQuery = true)
    void createCategorySetFlashcard(@Param ("category_name") String categoryName);


    @Modifying
    @Transactional
    @Query(value = """
            delete from category_set_flashcards c
            where c.category_id = :category_id
            """, nativeQuery = true)
    void deleteCategorySetFlashcard(@Param("category_id") Long categoryId);

    @Modifying
    @Transactional
    @Query(value = """
            update category_set_flashcards c
            set c.category_name = :category_name
            where c.category_id = :category_id
            """, nativeQuery = true)
    void updateCategorySetFlashcard(@Param ("category_id") Long categoryId,
                                    @Param ("category_name") String categoryName);
}
