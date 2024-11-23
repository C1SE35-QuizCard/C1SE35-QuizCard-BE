package com.example.quizcards.repository;

import com.example.quizcards.dto.ICollectionDTO;
import com.example.quizcards.entities.Collection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICollectionRepository extends JpaRepository<Collection, Long> {
    @Query(value = """
            select c.id, c.folder_id, c.set_id, c.created_at, c.updated_at
            from folders f, set_flashcards s, collection c
            where f.folder_id = c.folder_id and s.set_id = c.set_id and c.id = :id
            """, nativeQuery = true)
    ICollectionDTO findCollectionById(@Param("id") Long id);

    @Query(value = """
            select c.id, c.folder_id, c.set_id, c.created_at, c.updated_at
            from folders f, set_flashcards s, collection c
            where f.folder_id = c.folder_id and s.set_id = c.set_id
            """, nativeQuery = true)
    List<ICollectionDTO> findAllCollection();

    @Query(value = """
            select c.id, c.folder_id, c.set_id, c.created_at, c.updated_at
            from folders f, set_flashcards s, collection c
            where f.folder_id = c.folder_id and s.set_id = c.set_id and c.set_id = :set_id
            """, nativeQuery = true)
    List<ICollectionDTO> findCollectionBySetId(@Param("set_id") Long setId);

    @Query(value = """
            select c.id, c.folder_id, c.set_id, c.created_at, c.updated_at
            from folders f, set_flashcards s, collection c
            where f.folder_id = c.folder_id and s.set_id = c.set_id and c.folder_id = :folder_id
            """, nativeQuery = true)
    List<ICollectionDTO> findCollectionByFolderId(@Param("folder_id") Long folderId);

    @Modifying
    @Transactional
    @Query(value = """
            insert into collection(folder_id, set_id, created_at, updated_at)
            values (:folder_id, :set_id, now(), now())
            """, nativeQuery = true)
    void createCollection(@Param("folder_id") Long folderId,
                          @Param("set_id") Long setId);


    @Modifying
    @Transactional
    @Query(value = """
            delete from collection c
            where c.id = :id
            """, nativeQuery = true)
    void deleteCollectionById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = """
            update collection c
            set c.folder_id = :folder_id, c.set_id = :set_id, c.updated_at = now()
            where c.id = :id
            """, nativeQuery = true)
    void updateCollection(@Param("id") Long id,
                          @Param("folder_id") Long folderId,
                          @Param("set_id") Long setId);


    @Query(value = """
            select count(1)
            from collection c
            where c.folder_id = :folder_id and c.set_id = :set_id
            """, nativeQuery = true)
    Integer countSetsByFolderIdAndSetId(@Param("folder_id") Long folderId,
                                        @Param("set_id") Long setId);
}
