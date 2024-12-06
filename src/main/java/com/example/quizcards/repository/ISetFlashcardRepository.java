package com.example.quizcards.repository;
import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.response.SearchSetFlashResponse;
import com.example.quizcards.dto.response.TopCreatorsResponse;
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
            select f.card_id, f.question, f.answer, f.image_url, f.is_approved, f.created_at, f.updated_at, s.title
            from flashcards f
            join set_flashcards s on f.set_id = s.set_id
            where f.set_id = :set_id
            """, nativeQuery = true)
    List<IFlashcardDTO> findAllFlashcardsBySetId_2(@Param("set_id") Long setId); // cho Admin sử dụng

    /* Cho tất cả User sử dụng, chỉ những set card public hoặc của người sở hữu nó,
     * và các card đã phê duyệt
     *  */
    @Query(value = """
            select f.card_id, f.question, f.answer, f.image_url, f.is_approved, f.created_at, f.updated_at, s.title
            from flashcards f
            join set_flashcards s on f.set_id = s.set_id
            where s.set_id = :set_id and (s.user_id = :user_id or s.sharing_mode = true)
                and f.is_approved = true
            """, nativeQuery = true)
    List<IFlashcardDTO> findAllFlashcardsBySetId_3(@Param("set_id") Long setId, @Param("user_id") Long userId);


//    @Query(value = """
//            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as total_card
//            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
//            where s.user_id = a.user_id and s.category_id = c.category_id and f.set_id = s.set_id and s.sharing_mode = true
//            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
//            """, nativeQuery = true)
    @Query(value = """
            SELECT s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode,
                  a.last_name, a.first_name, a.user_name, a.user_id, a.avatar, c.category_name, COUNT(DISTINCT f.card_id) AS total_card
           FROM set_flashcards s
           JOIN app_users a ON s.user_id = a.user_id
           JOIN category_set_flashcards c ON s.category_id = c.category_id
           LEFT JOIN flashcards f ON f.set_id = s.set_id
           GROUP BY s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode,
                    a.last_name, a.first_name, a.user_name, a.avatar, c.category_name;
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findAllSetFlashcards(); // cho tất cả mọi người

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name,a.user_id, a.avatar, c.category_name, COUNT(f.card_id) as total_card
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and s.set_id = :set_id and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            """, nativeQuery = true)
    ISetFlashcardDTO findSetFlashcardsById(@Param("set_id") Long id);

    @Query(value = """

            SELECT s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode,
                                          a.last_name, a.first_name, a.user_name,a.user_id, a.avatar, c.category_name, COUNT(f.card_id) AS total_card
                                   FROM set_flashcards s
                                   JOIN app_users a ON s.user_id = a.user_id
                                   JOIN category_set_flashcards c ON s.category_id = c.category_id
                                   JOIN flashcards f ON f.set_id = s.set_id
                                   LEFT JOIN user_flashcard_settings ufs ON s.set_id = ufs.set_id AND ufs.user_id=:user_id
                                   WHERE s.user_id =:user_id\s
                                      OR ufs.user_id =:user_id
                                   GROUP BY s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode,
                                            a.last_name, a.first_name, a.user_name,a.user_id, a.avatar, c.category_name
                                   ORDER BY s.created_at DESC;
        """, nativeQuery = true)
    List<ISetFlashcardDTO> findAllSetByUserId(@Param("user_id") Long userId);

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as total_card
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and (s.user_id = :user_id or s.sharing_mode = true) and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findAllSetPublicByUserId(@Param("user_id") Long userId);

    @Query(value = """
            select count(s.set_id)
            from set_flashcards s
            where s.user_id = :user_id and s.sharing_mode = true
            """, nativeQuery = true)
    Integer countAllSetPublicByUserId(@Param("user_id") Long userId);

    @Query(value = """
            select count(s.set_id)
            from set_flashcards s
            join app_users au on s.user_id = au.user_id
            where au.user_name = :user_name and s.sharing_mode = true
            """, nativeQuery = true)
    Integer countAllSetPublicByUserName(@Param("user_name") String userName);

    @Modifying
    @Transactional
    @Query(value = """
            insert into set_flashcards(title, description_set, created_at, updated_at, is_approved, is_anonymous, sharing_mode, user_id, category_id)
            values (:title, :description_set, now(), now(), :is_approved, :is_anonymous, :sharing_mode, :user_id, :category_id)
            """, nativeQuery = true)
    void createSetFlashcard(@Param("title") String title,
                            @Param("description_set") String descriptionSet,
                            @Param("is_approved") Boolean isApproved,
                            @Param("is_anonymous") Boolean isAnonymous,
                            @Param("sharing_mode") Boolean sharingMode,
                            @Param("user_id") Long userId,
                            @Param("category_id") Long categoryId);

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
    void updateSetFlashcard(@Param("set_id") Long setId,
                            @Param("title") String title,
                            @Param("description_set") String descriptionSet,
                            @Param("is_approved") Boolean isApproved,
                            @Param("is_anonymous") Boolean isAnonymous,
                            @Param("sharing_mode") Boolean sharingMode,
                            @Param("user_id") Long userId,
                            @Param("category_id") Long categoryId);

    @Query(value = """
            SELECT
                s.set_id AS setId,
                s.title AS title,
                s.description_set AS descriptionSet,
                s.is_anonymous AS isAnonymous,
                a.user_name AS userName,
                a.user_id AS userId,
                a.avatar AS avatar,
                c.category_name AS categoryName,
                COUNT(f.card_id) AS totalCard
            FROM
                set_flashcards s
            JOIN
                app_users a ON s.user_id = a.user_id
            JOIN
                category_set_flashcards c ON s.category_id = c.category_id
            LEFT JOIN
                flashcards f ON f.set_id = s.set_id
            WHERE
                s.sharing_mode = TRUE
                AND ( 
                  MATCH(s.title) AGAINST(:title IN NATURAL LANGUAGE MODE)
                OR 
                MATCH(c.category_name) AGAINST(:title IN NATURAL LANGUAGE MODE)
                or
                s.title LIKE CONCAT('%', :title, '%')
                or c.category_name LIKE CONCAT('%', :title, '%')
                )
            GROUP BY
                s.set_id,
                s.title,
                s.description_set,
                s.is_anonymous,
                a.user_id,
                a.avatar,
                c.category_name
            """, nativeQuery = true)
    List<SearchSetFlashResponse> searchByTitleAndCategory(@Param("title") String title);

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as total_card
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and s.sharing_mode = true and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            order by s.updated_at DESC
            """, nativeQuery = true)
    List<ISetFlashcardDTO> sortByUpdatedDate();

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) as total_card
            from set_flashcards s, app_users a, category_set_flashcards c, flashcards f
            where s.user_id = a.user_id and s.category_id = c.category_id and s.sharing_mode = true and f.set_id = s.set_id
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findAllSetPublic();

    @Query(value = """
            select count(*)
            from set_flashcards s
            where user_id = :user_id
            """, nativeQuery = true)
    Integer countNumberOfSetCreated(@Param("user_id") Long user_id);

    @Query(value = """
            select count(*)
            from set_flashcards s            
            where user_id = :user_id and DATE(created_at) = CURDATE()
            """, nativeQuery = true)
    Integer countNumberOfSetCreatedInCurrentDay(@Param("user_id") Long user_id);

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, 
                   s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name,
                   COUNT(f.card_id) as total_card
            from set_flashcards s
            join app_users au on s.user_id = au.user_id
            join category_set_flashcards c on s.category_id = c.category_id
            join user_flashcard_settings ufs on s.set_id = ufs.set_id
            join flashcards f on f.set_id = s.set_id
            where ufs.user_id = :user_id 
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, 
                     s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            order by ufs.last_accessed desc
            limit 10;
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findTop10RecentSetFlashcards(@Param("user_id") Long userId);

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, 
                   s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name,
                   COUNT(f.card_id) as total_card
            from set_flashcards s
            join app_users au on s.user_id = au.user_id
            join category_set_flashcards c on s.category_id = c.category_id
            join flashcards f on f.set_id = s.set_id
            where s.category_id = :category_id and (s.user_id = :user_id or s.sharing_mode = true)
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, 
                     s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            order by (
                select count(ufs.user_id)
                from user_flashcard_settings ufs
                where ufs.set_id = s.set_id
            ) desc
            limit 10;
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findTop10RelevantByCategory(@Param("category_id") Long categoryId,
                                                       @Param("user_id") Long userId);

    @Query(value = """
            select s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, 
                   s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name,
                   COUNT(f.card_id) as total_card
            from set_flashcards s
            join app_users au on s.user_id = au.user_id
            join category_set_flashcards c on s.category_id = c.category_id
            join flashcards f on f.set_id = s.set_id
            LEFT JOIN
                (
                    SELECT set_id, COUNT(*) AS access_count
                    FROM user_flashcard_settings
                    GROUP BY set_id
                ) ac ON s.set_id = ac.set_id
            LEFT JOIN
                (
                    SELECT category_id, COUNT(*) AS category_count
                    FROM set_flashcards
                    GROUP BY category_id
                ) cc ON s.category_id = cc.category_id            
            where (s.user_id = :user_id or s.sharing_mode = true)
            group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, 
                     s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            order by (0.4 * IFNULL(ac.access_count, 0) + 0.1 * IFNULL(cc.category_count, 0)) DESC
            limit 10;
            """, nativeQuery = true)
    List<ISetFlashcardDTO> findTop10PopularFlashcardSets(@Param("user_id") Long userId);

    @Query(value = """
            select au.user_id, au.user_name, au.avatar, count(s.user_id) as set_count 
            from app_users au
            join set_flashcards s on s.user_id = au.user_id
            where s.sharing_mode = true
            group by au.user_id, au.user_name, au.avatar
            order by set_count desc
            limit 10;
            """, nativeQuery = true)
    List<TopCreatorsResponse> findTop10PopularCreators();


    @Query(value = """
            SELECT s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name, COUNT(f.card_id) AS total_card
            FROM set_flashcards s
            JOIN app_users a ON s.user_id = a.user_id
            JOIN category_set_flashcards c ON s.category_id = c.category_id
            JOIN flashcards f ON f.set_id = s.set_id
            JOIN user_flashcard_settings ufs on s.set_id = ufs.set_id
            WHERE s.user_id = :user_id
            GROUP BY s.set_id, s.title, s.description_set, s.created_at, s.updated_at, s.is_approved, s.is_anonymous, s.sharing_mode, a.last_name, a.first_name, a.user_name, a.avatar, c.category_name
            ORDER BY ufs.last_accessed desc
            LIMIT :limit
            """, nativeQuery = true)

    List<ISetFlashcardDTO> findAllSetPublicNearbySettings(@Param("user_id") Long userId,@Param("limit") Long limit);

    @Query(value = """
            SELECT COUNT(1)
            FROM set_flashcards sf
            WHERE sf.set_id = :setId
              AND sf.sharing_mode = true
            LIMIT 1;
            """, nativeQuery = true)
    Long existsBySetIdAndSharingModeTrue(@Param("setId") Long setId);
}
