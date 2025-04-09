package com.example.quizcards.service.impl;

import com.example.quizcards.dto.StreakStatus;
import com.example.quizcards.dto.response.StreakAnalysisResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.StreakAnalysis;
import com.example.quizcards.entities.StreakDetails;
import com.example.quizcards.exception.BadRequestException;
import com.example.quizcards.repository.IStreakAnalysisRepository;
import com.example.quizcards.repository.IStreakDetailsRepository;
import com.example.quizcards.utils.IbmTimezoneUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StreakServiceImpl {
    IStreakDetailsRepository learningRepository;

    IStreakAnalysisRepository analysisRepository;

    IbmTimezoneUtils ibmTimezoneUtils;

    @Transactional
    public boolean generateStreak(AppUser user, OffsetDateTime clientDateLearned) {
        int offsetClient = clientDateLearned.getOffset().getTotalSeconds() / 3600;
        Instant instant = Instant.now();
        LocalDate currentDateInClient =
                instant.atZone(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(offsetClient))).toLocalDate();

        if (clientDateLearned.toLocalDate().isAfter(currentDateInClient)) {
            throw new RuntimeException("Invalid date");
        }

        Optional<StreakDetails> streakData = learningRepository.findByUserAndDateLearned(user, currentDateInClient);

        if (streakData.isPresent()) {
            return false;
        }

        StreakDetails streakDetails = StreakDetails.builder()
                .user(user)
                .dateLearned(currentDateInClient)
                .build();

        learningRepository.save(streakDetails);
        handleUpdateStreakAnalysis(user, currentDateInClient);
        return true;
    }

    private void handleUpdateStreakAnalysis(AppUser user, LocalDate currentDateInClient) {
        StreakAnalysis streakAnalysis = analysisRepository.findByUser(user)
                .orElse(StreakAnalysis.builder()
                        .user(user)
                        .longestStreak(1L)
                        .dayLearned(0L)
                        .build());

        boolean yDayLearned = learningRepository.existsByUserAndDateLearned(user, currentDateInClient.minusDays(1));

        if (!yDayLearned) {
            streakAnalysis.setCurrentStreak(1L);
        } else {
            streakAnalysis.setCurrentStreak(streakAnalysis.getCurrentStreak() + 1);
            streakAnalysis.setLongestStreak(Math.max(streakAnalysis.getLongestStreak(), streakAnalysis.getCurrentStreak()));
        }

        streakAnalysis.setDayLearned(streakAnalysis.getDayLearned() + 1L);

        analysisRepository.save(streakAnalysis);
    }

    public StreakAnalysisResponse getAnalysisStreak(AppUser user, int offsetHours, int offsetMinutes, String localeStr) {
        Instant instant = Instant.now();

        LocalDate currentDateInClient = instant.atZone(ZoneId.ofOffset("UTC",
                        ZoneOffset.ofHoursMinutes(offsetHours, offsetMinutes)))
                .toLocalDate();

        Locale locale = ibmTimezoneUtils.getLocale(localeStr);

        return getAnalysisLearningBaseOnLocale(user, locale, currentDateInClient);
    }

    public StreakAnalysisResponse getAnalysisStreakV2(AppUser user, String localeStr) {
        Instant instant = Instant.now();

        int[] offsetData = ibmTimezoneUtils.getOffsetHoursBaseOnLocale(localeStr);

        Locale locale = ibmTimezoneUtils.getLocale(localeStr);

        LocalDate currentDateInClient = instant.atZone(
                ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(offsetData[0], offsetData[1])))
                .toLocalDate();

        // Get rules base on Locale
        return getAnalysisLearningBaseOnLocale(user, locale, currentDateInClient);
    }

    private StreakAnalysisResponse getAnalysisLearningBaseOnLocale(AppUser user, Locale locale, LocalDate currentDateInClient) {
        WeekFields weekFields = WeekFields.of(locale);
        DayOfWeek firstDayOfWeek = weekFields.getFirstDayOfWeek();

        // Calculate the first day of week
        LocalDate startOfWeek = currentDateInClient.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));

        // Calculate the last day of week
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        boolean currentDateLearned = learningRepository.existsByUserAndDateLearned(user, currentDateInClient);

        boolean yesterdayLearned = learningRepository.existsByUserAndDateLearned(user, currentDateInClient.minusDays(1));

        StreakAnalysis streakAnalysis = analysisRepository.findByUser(user)
                .orElse(StreakAnalysis.builder()
                        .user(user)
                        .longestStreak(0L)
                        .currentStreak(0L)
                        .dayLearned(0L)
                        .build());

        List<StreakDetails> streaksOneWeek = learningRepository.findByUserAndDateLearnedBetween(user,
                startOfWeek, endOfWeek);

        List<StreakStatus> streaksStatuses = streaksOneWeek.stream()
                .map(streakDetails -> {
                    String dayFull = streakDetails.getDateLearned().getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
                    String dayShort = streakDetails.getDateLearned().getDayOfWeek().getDisplayName(TextStyle.SHORT, locale);
                    return StreakStatus.builder()
                            .date(streakDetails.getDateLearned())
                            .dayFull(dayFull)
                            .dayShort(dayShort)
                            .dayFullEn(streakDetails.getDateLearned().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                            .dayShortEn(streakDetails.getDateLearned().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                            .dayRank(streakDetails.getDateLearned().getDayOfWeek().getValue())
                            .build();
                })
                .toList();

        return StreakAnalysisResponse.builder()
                .longestStreak(streakAnalysis.getLongestStreak())
                .currentStreak(currentDateLearned || yesterdayLearned ? streakAnalysis.getCurrentStreak() : 0)
                .isCurrentDateLearned(currentDateLearned)
                .dayLearned(streakAnalysis.getDayLearned())
                .streakOneWeek(streaksStatuses)
                .build();
    }

    public List<StreakStatus> getLearnedDetails(AppUser user, int month, int year, String localeCode) {
        List<StreakStatus> statuses = learningRepository.getLearnedByMonthAndYear(user.getUserId(), month, year);

        Iterator<StreakStatus> statusIterator = statuses.iterator();

        Iterator<StreakStatus> transformedIterator = transformDateToDayOfWeek(statusIterator, localeCode);

        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(transformedIterator, Spliterator.ORDERED), false)
                .toList();
    }

    public List<StreakStatus> getAllLearnedDate(AppUser user, String localeCode) {
        List<StreakStatus> statuses = learningRepository.findAllStreakStatusByUserId(user.getUserId());

        Iterator<StreakStatus> statusIterator = statuses.iterator();

        Iterator<StreakStatus> transformedIterator = transformDateToDayOfWeek(statusIterator, localeCode);

        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(transformedIterator, Spliterator.ORDERED), false)
                .toList();
    }

    public Page<StreakStatus> getLearnedByDateRange(AppUser user, LocalDate startDate, LocalDate endDate,
                                                    String localeCode,
                                                    int page,
                                                    int size) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Invalid date range");
        }

        if (page < 0) {
            throw new BadRequestException("Invalid page: cannot zero");
        }

        if (size <= 40 || size > 100) {
            throw new BadRequestException("Invalid size: in range [40, 100]");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<StreakStatus> statuses = learningRepository.getLearnedByDateRange(user.getUserId(), startDate, endDate, pageable);

        Iterator<StreakStatus> statusIterator = statuses.iterator();

        Iterator<StreakStatus> transformedIterator = transformDateToDayOfWeek(statusIterator, localeCode);

        List<StreakStatus> transformedStatuses = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(transformedIterator, Spliterator.ORDERED), false)
                .toList();

        return new PageImpl<>(transformedStatuses, pageable, statuses.getTotalElements());
    }

    private Iterator<StreakStatus> transformDateToDayOfWeek(Iterator<StreakStatus> streaksStatuses, String localeCode) {
        if (!StringUtils.hasText(localeCode)) {
            localeCode = "en-US";
        }
        Locale locale = ibmTimezoneUtils.getLocale(localeCode);
        Stream<StreakStatus> transformedStream = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(streaksStatuses, Spliterator.ORDERED), false)
                .map(streakStatus -> {
                    String dayFull = streakStatus.getDate().getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
                    String dayShort = streakStatus.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, locale);
                    return StreakStatus.builder()
                            .date(streakStatus.getDate())
                            .dayFull(dayFull)
                            .dayShort(dayShort)
                            .dayFullEn(streakStatus.getDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                            .dayShortEn(streakStatus.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                            .dayRank(streakStatus.getDate().getDayOfWeek().getValue())
                            .build();
                });
        return transformedStream.iterator();
    }
}
