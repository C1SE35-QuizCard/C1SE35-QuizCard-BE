package com.example.quizcards.repository;

import com.example.quizcards.entities._Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface _ISubscriptionRepository extends JpaRepository<_Subscription, Long> {
}
