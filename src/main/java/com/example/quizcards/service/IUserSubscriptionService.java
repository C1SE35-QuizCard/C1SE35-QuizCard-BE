package com.example.quizcards.service;

import com.example.quizcards.dto.response.RevenueStatisticsResponse;
import java.util.List;

public interface IUserSubscriptionService {
    List<RevenueStatisticsResponse> getRevenueStatisticsByYear(Integer year);
} 