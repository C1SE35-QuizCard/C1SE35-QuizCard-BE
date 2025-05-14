package com.example.quizcards.service.impl;

import com.example.quizcards.dto.response.RevenueStatisticsResponse;
import com.example.quizcards.entities.UserSubscription;
import com.example.quizcards.repository.UserSubscriptionRepository;
import com.example.quizcards.service.IUserSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserSubscriptionServiceImpl implements IUserSubscriptionService {

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Override
    public List<RevenueStatisticsResponse> getRevenueStatisticsByYear(Integer year) {
        // If year is not provided, use current year
        final int targetYear = year != null ? year : LocalDateTime.now().getYear();

        // Get all paid subscriptions for the specified year
        List<UserSubscription> subscriptions = userSubscriptionRepository.findAll().stream()
                .filter(sub -> sub.getStatusPaid() == UserSubscription.StatusPaid.PAID)
                .filter(sub -> {
                    LocalDateTime subDate = sub.getExpiredDate().toLocalDateTime();
                    return subDate.getYear() == targetYear;
                })
                .collect(Collectors.toList());

        // Group subscriptions by month
        Map<Integer, List<UserSubscription>> subscriptionsByMonth = subscriptions.stream()
                .collect(Collectors.groupingBy(sub -> 
                    sub.getExpiredDate().toLocalDateTime().getMonthValue()));

        List<RevenueStatisticsResponse> statistics = new ArrayList<>();

        // Calculate statistics for each month
        for (int month = 1; month <= 12; month++) {
            final int currentMonth = month;
            List<UserSubscription> monthSubscriptions = subscriptionsByMonth.getOrDefault(currentMonth, new ArrayList<>());
            
            BigDecimal totalRevenue = monthSubscriptions.stream()
                    .map(sub -> sub.getCategorySubscription().getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalSubscriptions = monthSubscriptions.size();
            
            // Count new subscriptions (first time subscriptions)
            long newSubscriptions = monthSubscriptions.stream()
                    .filter(sub -> {
                        LocalDateTime subDate = sub.getExpiredDate().toLocalDateTime();
                        return subDate.getMonthValue() == currentMonth;
                    })
                    .count();

            // Count renewed subscriptions
            long renewedSubscriptions = totalSubscriptions - newSubscriptions;

            statistics.add(new RevenueStatisticsResponse(
                    currentMonth,
                    targetYear,
                    totalRevenue,
                    totalSubscriptions,
                    newSubscriptions,
                    renewedSubscriptions
            ));
        }

        return statistics;
    }
} 