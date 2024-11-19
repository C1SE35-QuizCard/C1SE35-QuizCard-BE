package com.example.quizcards.helpers.CollectionHelpers;

import com.example.quizcards.dto.request.CollectionParamRequest;
import com.example.quizcards.dto.request.CollectionRequest;

public interface ICollectionHelpers {
    void handleDeleteCollection(Long folderId);

    void handleAddCollection(CollectionRequest request);

    void handleAdminDeleteCollection(CollectionParamRequest request, Long userId);

    void handleAdminAddCollection(CollectionParamRequest request, Long userId);
}
