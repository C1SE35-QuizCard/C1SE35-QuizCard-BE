package com.example.quizcards.helpers.FolderHelpers;

import com.example.quizcards.dto.request.FolderRequest;

public interface IFolderHelpers {
    void handleDeleteFolder(Long folderId);

    void handleUpdateFolder(FolderRequest request);

    void handleAdminDeleteFolder(Long folderId, Long userId);

    void handleAdminUpdateFolder(FolderRequest request, Long userId);
}
