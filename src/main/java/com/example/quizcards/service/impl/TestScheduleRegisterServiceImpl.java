package com.example.quizcards.service.impl;

import com.example.quizcards.helpers.SchedulerExecutorHelpers.IScheduleHelpers;
import com.example.quizcards.helpers.TestHelpers.TestExecutorSession;
import com.example.quizcards.helpers.TestHelpers.TestSocketSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TestScheduleRegisterServiceImpl {
    @Autowired
    private TestSubmitServiceImpl submitService;

    @Autowired
    private IScheduleHelpers scheduleHelper;

    public void setupSubmitExecutor(Long testId, LocalDateTime endAt) {
        if (TestExecutorSession.getTestExecutorByTestId(testId) != null) {
            TestExecutorSession.shutdownExecutorByTestId(testId);
        }
        TestExecutorSession.addTestExecutor(testId,
                scheduleHelper.addScheduleTasks(() -> {
                    try {
                        TestExecutorSession.removeTestExecutor(testId);
                        submitService.submitTest(testId);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    } finally {
                        TestSocketSession.shutdownTest(testId);
                    }
                }, endAt));
    }
}
