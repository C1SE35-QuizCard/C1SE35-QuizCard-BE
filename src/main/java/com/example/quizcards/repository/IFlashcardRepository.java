package com.example.quizcards.repository;

import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.entities.Flashcard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFlashcardRepository extends JpaRepository<Flashcard, Long> {
    @Query(value = """
            select f.card_id, f.question, f.answer, f.image_url, f.is_approved, f.created_at, f.updated_at, s.title
            from flashcards f, set_flashcards s
            where f.set_id = s.set_id and s.set_id = :set_id
            """, nativeQuery = true)
    List<IFlashcardDTO> findAllFlashcardsBySetId(@Param("set_id") Long id);

    @Query(value = """
            select f.card_id, f.question, f.answer, f.image_url, f.is_approved, f.created_at, f.updated_at, s.title
            from flashcards f, set_flashcards s
            where f.set_id = s.set_id and f.card_id = :card_id
            """, nativeQuery = true)
    IFlashcardDTO findFlashcardByCardId(@Param("card_id") Long cardId);

    @Query(value = """
            select f.card_id, f.question, f.answer, f.image_url, f.is_approved, f.created_at, f.updated_at, s.title
            from flashcards f, set_flashcards s
            where f.set_id = s.set_id
            """, nativeQuery = true)
    List<IFlashcardDTO> findAllFlashcards();

    @Modifying
    @Transactional
    @Query(value = """
            insert into flashcards(question, answer, image_url, is_approved, created_at, updated_at, set_id)
            values (:question, :answer, :image_url, :is_approved, now(), now(),:set_id )
            """, nativeQuery = true)
    void createFlashcards(@Param("question") String question,
                          @Param("answer") String answer,
                          @Param("image_url") String imageLink,
                          @Param("is_approved") Boolean isApproved,
                          @Param("set_id") Long setId);


    @Modifying
    @Transactional
    @Query(value = """
            delete from flashcards f
            where f.card_id = :card_id
            """, nativeQuery = true)
    void deleteFlashcardById(@Param("card_id") Long cardId);

    @Modifying
    @Transactional
    @Query(value = """
            update flashcards f
            set f.question = :question, f.answer = :answer, f.image_url = :image_url, f.is_approved = :is_approved, f.updated_at = now(), f.set_id = :set_id
            where f.card_id = :card_id
            """, nativeQuery = true)
    void updateFlashcards(@Param("card_id") Long cardId,
                          @Param("question") String question,
                          @Param("answer") String answer,
                          @Param("image_url") String imageLink,
                          @Param("is_approved") Boolean isApproved,
                          @Param("set_id") Long setId);

    @Query(value = """
            select count(f.card_id) 
            from flashcards f
            where f.set_id = :set_id
            """, nativeQuery = true)
    Integer countNumberOfCardsInSet(@Param("set_id") Long setId);

    @Query(value = """
            select count(1)
            from flashcards f
            where f.card_id = :card_id and f.set_id = :set_id
            """, nativeQuery = true)
    Integer countCardsByIdAndSetId(@Param("card_id") Long cardId, @Param("set_id") Long setId);

    @Query(value = """
        SELECT f.card_id, f.question, f.answer, f.image_url, f.is_approved, 
               f.created_at, f.updated_at, s.title
        FROM flashcards f, set_flashcards s
        WHERE f.set_id = s.set_id 
          AND s.set_id = :set_id
        ORDER BY RAND()
        LIMIT 15
        """, nativeQuery = true)
    List<IFlashcardDTO> findRandomFlashcardsBySetId(@Param("set_id") Long id);

}

