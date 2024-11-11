package com.example.quizcards.dto.response;

import com.example.quizcards.entities.CategorySubscription;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySubscriptionResponse {
    private String name;
    private BigDecimal price;
    private String description;
    private Integer maxSetsFlashcards;
    private Integer maxFlashcardsPerSet;
    private Integer maxSetsPerDay;
    private Integer maxRoomsCreatePerDay;
    private Integer maxTermsPerRoom;
    private int expiredMonth;

    public CategorySubscriptionResponse(CategorySubscription categorySubscription) {
        this.name = categorySubscription.getName();
        this.price = categorySubscription.getPrice();
        this.description = categorySubscription.getDescription();
        this.maxSetsFlashcards = categorySubscription.getMaxSetsFlashcards();
        this.maxFlashcardsPerSet = categorySubscription.getMaxFlashcardsPerSet();
        this.maxSetsPerDay = categorySubscription.getMaxSetsPerDay();
        this.maxRoomsCreatePerDay = categorySubscription.getMaxRoomsCreatePerDay();
        this.maxTermsPerRoom = categorySubscription.getMaxTermsPerRoom();
        this.expiredMonth = categorySubscription.getExpiredMonth();
    }
}
