package com.example.quizcards.helpers.UserProgressHelpers;

import com.example.quizcards.dto.request.CollectionParamRequest;
import com.example.quizcards.dto.request.CollectionRequest;
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
public class ProgressHelpersImpl implements IProgressHelpers {

}
