package com.example.quizcards.repository;

import com.example.quizcards.entities.CategorySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategorySubscriptionRepository extends JpaRepository<CategorySubscription, Long> {
    Optional<CategorySubscription> findByName(String name);

    @Query(value = """
        select *
        from category_subscriptions cs
        where cs.subscriptions_description REGEXP :subscriptionName COLLATE utf8mb4_unicode_ci
        """, nativeQuery = true)
    CategorySubscription findByPatternName(
            @Param("subscriptionName") String name);
}
