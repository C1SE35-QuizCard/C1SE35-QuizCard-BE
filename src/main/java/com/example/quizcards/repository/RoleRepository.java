package com.example.quizcards.repository;

import com.example.quizcards.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<AppRole, Long> {
    @Query(value = "SELECT * FROM app_roles WHERE role_name = :name", nativeQuery = true)
    Optional<AppRole> findByName(@Param("name") String name);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM app_roles WHERE role_name = :name", nativeQuery = true)
    boolean existsByName(@Param("name") String name);

    @Query(value = "SELECT role_id, role_name FROM app_roles", nativeQuery = true)
    List<Object[]> findAllBasicRoles();
} 