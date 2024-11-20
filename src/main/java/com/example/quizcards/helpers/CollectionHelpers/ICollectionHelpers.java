package com.example.quizcards.helpers.CollectionHelpers;

import com.example.quizcards.dto.request.CollectionParamRequest;

public interface ICollectionHelpers {
    void handleDeleteCollection(CollectionParamRequest request);

    void handleAddCollection(CollectionParamRequest request);

    void handleAdminDeleteCollection(CollectionParamRequest request, Long userId);

    void handleAdminAddCollection(CollectionParamRequest request, Long userId);
}
