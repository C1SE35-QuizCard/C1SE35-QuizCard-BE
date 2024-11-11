package com.example.quizcards.repository;

import com.example.quizcards.dto.IFolderDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.entities.Folder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFolderRepository extends JpaRepository<Folder, Long> {
    @Query(value = """
            select f.folder_id, f.title, f.created_at, f.updated_at, s.user_id ,s.first_name, s.last_name,
                   (select count(*) from collection c where c.folder_id = f.folder_id) as set_count
            from folders f
            join app_users s on f.user_id = s.user_id
            left join collection c on f.folder_id = c.folder_id
            where f.folder_id = :folder_id
            group by f.folder_id, s.first_name, s.last_name
            """, nativeQuery = true)
    IFolderDTO findFolderById(@Param("folder_id") Long folderId);


    @Query(value = """
    select f.folder_id, f.title, 
           f.created_at, f.updated_at, 
           a.user_id, a.first_name, a.last_name, 
           count(l.set_id) as setCount
    from folders f
    join app_users a on f.user_id = a.user_id
    left join collection l on f.folder_id = l.folder_id
    where f.user_id = :userId
    group by f.folder_id, f.title, f.created_at, f.updated_at, a.first_name, a.last_name
    """, nativeQuery = true)
    List<IFolderDTO> findFoldersByUserId(@Param("userId") Long userId);



    @Query(value = """
            select f.folder_id, f.title, f.created_at, f.updated_at, s.user_id, s.first_name, s.last_name,
                   (select count(*) from collection c where c.folder_id = f.folder_id) as set_count
            from folders f
            join app_users s on f.user_id = s.user_id
            left join collection c on f.folder_id = c.folder_id
            where MATCH(f.title) AGAINST(:title IN BOOLEAN MODE) and f.user_id = :userId
            group by f.folder_id, s.first_name, s.last_name
            """, nativeQuery = true)
    List<IFolderDTO> searchFolderByTitle(@Param("title") String title,
                                         @Param("userId") Long userId);



    @Query(value = """
    select s.set_id as setId, s.title as title, s.description_set as descriptionSet, 
           s.created_at as createdAt, s.updated_at as updatedAt, 
           s.is_approved as isApproved, s.is_anonymous as isAnonymous, 
           s.sharing_mode as sharingMode, 
           a.last_name as lastName, a.first_name as firstName, 
           m.category_name as categoryName, a.avatar as avatar, 
           a.user_name as userName, 
           COUNT(c.card_id) as cardCountg
    from set_flashcards s
    join collection l on l.set_id = s.set_id
    join folders f on f.folder_id = l.folder_id
    join app_users a on f.user_id = a.user_id
    join category_set_flashcards m on s.category_id = m.category_id
    left join flashcards c on c.set_id = s.set_id
    where f.folder_id = :folderId 
      and (a.user_id = :userId or s.sharing_mode = true)
    group by s.set_id, s.title, s.description_set, s.created_at, s.updated_at, 
             s.is_approved, s.is_anonymous, s.sharing_mode, 
             a.last_name, a.first_name, m.category_name, a.avatar, a.user_name
    """, nativeQuery = true)
    List<ISetFlashcardDTO> findSetByFolderIdAndUserId(@Param("folderId") Long folderId,
                                                      @Param("userId") Long userId);


    @Modifying
    @Transactional
    @Query(value = """
            insert into folders(title, created_at, updated_at, user_id)
            values (:title, now(), now(), :user_id)
            """, nativeQuery = true)
    void createFolder(@Param ("title") String title,
                      @Param ("user_id") Long userId);


    @Modifying
    @Transactional
    @Query(value = """
            delete from folders f
            where f.folder_id = :folder_id
            """, nativeQuery = true)
    void deleteFolderById(@Param("folder_id") Long folderId);

    @Modifying
    @Transactional
    @Query(value = """
            update folders f
            set f.title = :title, f.updated_at = now(), f.user_id = :user_id
            where f.folder_id = :folder_id
            """, nativeQuery = true)
    void updateFolder(@Param ("folder_id") Long folderId,
                      @Param ("title") String title,
                      @Param ("user_id") Long userId);
}
