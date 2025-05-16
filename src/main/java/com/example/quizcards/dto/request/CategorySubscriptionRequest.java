package com.example.quizcards.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySubscriptionRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Max sets per day is required")
    @Min(value = 0, message = "Max sets per day must be greater than or equal to 0")
    private Integer maxSetsPerDay;

    @NotNull(message = "Max sets flashcards is required")
    @Min(value = 0, message = "Max sets flashcards must be greater than or equal to 0")
    private Integer maxSetsFlashcards;

    @NotNull(message = "Max flashcards per set is required")
    @Min(value = 0, message = "Max flashcards per set must be greater than or equal to 0")
    private Integer maxFlashcardsPerSet;

    @NotNull(message = "Max rooms create per day is required")
    @Min(value = 0, message = "Max rooms create per day must be greater than or equal to 0")
    private Integer maxRoomsCreatePerDay;

    @NotNull(message = "Max terms per room is required")
    @Min(value = 0, message = "Max terms per room must be greater than or equal to 0")
    private Integer maxTermsPerRoom;

    @NotNull(message = "Expired month is required")
    @Min(value = 0, message = "Expired month must be greater than or equal to 0")
    private Integer expiredMonth;
} 