package com.example.quizcards.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGeneralService<T> {
    /**
     * Retrieves all entities of type T.
     *
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * Retrieves all entities of type T.
     *
     * @return a list of all entities
     */
    Page<T> findAll(Pageable pageable);


    /**
     * Retrieves an entity by its ID.
     *
     * @param id the ID of the entity to retrieve
     * @return the entity with the given ID, or null if not found
     */
    T findById(Long id);

    /**
     * Saves an entity.
     *
     * @param t the entity to save
     */
    void save(T t);

    /**
     * Removes an entity by its ID.
     *
     * @param id the ID of the entity to remove
     */
    void remove(Long id);
}