package com.example.quizcards.repository;

import com.example.quizcards.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
} 