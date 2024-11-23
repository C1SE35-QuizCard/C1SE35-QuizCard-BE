package com.example.quizcards.helpers.TestHelpers;

public interface ITestHelpers {

    void handleDeleteTest(Long testId);

    void handleAccessTest(Long testId);

    void handleAdminDeleteTest(Long testId, Long userId);
}
