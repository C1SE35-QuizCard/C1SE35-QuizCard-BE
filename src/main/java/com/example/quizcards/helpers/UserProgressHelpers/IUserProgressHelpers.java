package com.example.quizcards.helpers.UserProgressHelpers;

public interface IUserProgressHelpers {

    void handleDeleteUserProgress(Long testId);

    void handleAdminDeleteUserProgress(Long testId, Long userId);
}
