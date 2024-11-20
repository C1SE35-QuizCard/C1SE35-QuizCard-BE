package com.example.quizcards.helpers.CollectionHelpers;

import com.example.quizcards.dto.request.CollectionParamRequest;
import com.example.quizcards.entities.Folder;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.exception.AccessDeniedException;
import com.example.quizcards.exception.DuplicateValueException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.helpers.AuthenticationHelpers;
import com.example.quizcards.repository.ICollectionRepository;
import com.example.quizcards.repository.IFolderRepository;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ICustomUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CollectionHelpersImpl implements ICollectionHelpers {
    private IFolderRepository folderRepository;

    private AuthenticationHelpers authenticationHelpers;

    private ISetFlashcardRepository setFlashcardRepository;

    private ICollectionRepository collectionRepository;

    private ICustomUserDetailsService customUserDetailsService;

    public CollectionHelpersImpl(IFolderRepository folderRepository,
                                 AuthenticationHelpers authenticationHelpers,
                                 ISetFlashcardRepository setFlashcardRepository,
                                 ICustomUserDetailsService customUserDetailsService,
                                 ICollectionRepository collectionRepository) {
        this.folderRepository = folderRepository;
        this.authenticationHelpers = authenticationHelpers;
        this.setFlashcardRepository = setFlashcardRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.collectionRepository = collectionRepository;
    }

    private void checkFolderOwner(Long folderId, UserPrincipal up) throws AccessDeniedException, ResourceNotFoundException {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", folderId));

        if (!up.getId().equals(folder.getUser().getUserId())) {
            throw new AccessDeniedException("You do not have permission to access this folder");
        }
    }

    private void checkSetCanAdded(Long setId, Long folderId, UserPrincipal up) throws AccessDeniedException, ResourceNotFoundException {
        SetFlashcard set = setFlashcardRepository.findById(setId).
                orElseThrow(() -> new ResourceNotFoundException("Set", "id", setId));

        if (!up.getId().equals(set.getUser().getUserId()) && !set.getSharingMode()) {
            throw new AccessDeniedException("You do not have permission because set is not public for other");
        }

        if (collectionRepository.countSetsByFolderIdAndSetId(folderId, setId) > 0) {
            throw new DuplicateValueException("Set is valid on folder");
        }
    }

    @Override
    public void handleDeleteCollection(CollectionParamRequest request) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        checkFolderOwner(request.getFolderId(), up);
    }

    @Override
    public void handleAddCollection(CollectionParamRequest request) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        checkFolderOwner(request.getFolderId(), up);
        checkSetCanAdded(request.getSetId(), request.getFolderId(), up);
    }

    @Override
    public void handleAdminDeleteCollection(CollectionParamRequest request, Long userId) {

    }

    @Override
    public void handleAdminAddCollection(CollectionParamRequest request, Long userId) {

    }
}
