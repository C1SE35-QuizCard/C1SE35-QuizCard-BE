package com.example.quizcards.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatisticsResponse {
    private Integer month;
    private Integer year;
    private BigDecimal totalRevenue;
    private Long totalSubscriptions;
    private Long newSubscriptions;
    private Long renewedSubscriptions;
} 