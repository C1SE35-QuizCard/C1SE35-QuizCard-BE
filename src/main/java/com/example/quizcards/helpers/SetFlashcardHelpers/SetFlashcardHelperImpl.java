package com.example.quizcards.helpers.SetFlashcardHelpers;

import com.example.quizcards.dto.request.FlashcardInitializeRequest;
import com.example.quizcards.dto.request.SetFlashcardInitializeRequest;
import com.example.quizcards.dto.request.SetFlashcardRequest;
import com.example.quizcards.dto.response.CategorySubscriptionResponse;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.exception.AccessDeniedException;
import com.example.quizcards.exception.BadRequestException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.helpers.AuthenticationHelpers;
import com.example.quizcards.repository.ICategorySubscriptionRepository;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ICategorySubscriptionService;
import com.example.quizcards.service.ICustomUserDetailsService;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SetFlashcardHelperImpl implements ISetFlashcardHelpers {

    private ISetFlashcardRepository setFlashcardRepository;

    private AuthenticationHelpers authenticationHelpers;

    private ICustomUserDetailsService customUserDetailsService;

    private ICategorySubscriptionService categorySubscriptionService;

    public SetFlashcardHelperImpl(ISetFlashcardRepository setFlashcardRepository,
                                  AuthenticationHelpers authenticationHelpers,
                                  ICategorySubscriptionRepository categorySubscriptionRepository,
                                  ICategorySubscriptionService categorySubscriptionService) {
        this.setFlashcardRepository = setFlashcardRepository;
        this.authenticationHelpers = authenticationHelpers;
        this.categorySubscriptionService = categorySubscriptionService;
        this.customUserDetailsService = customUserDetailsService;
    }

    private void checkSetFlashcardOwner(Long setId, UserPrincipal up) throws AccessDeniedException, ResourceNotFoundException {
        SetFlashcard set = setFlashcardRepository.findById(setId).
                orElseThrow(() -> new ResourceNotFoundException("Set", "id", setId));

        if (!up.getId().equals(set.getUser().getUserId())) {
            throw new AccessDeniedException("You do not have permission to access this set flashcard");
        }
    }

    private void checkAddForFreeUser(SetFlashcardInitializeRequest request, Long userId,
                                     CategorySubscriptionResponse currentCs) {
        if (setFlashcardRepository.countNumberOfSetCreated(userId) >= currentCs.getMaxSetsFlashcards()) {
            throw new BadRequestException(String.format("The number of card sets is %d sets per user.",
                    currentCs.getMaxSetsFlashcards()));
        }
        if (setFlashcardRepository.countNumberOfSetCreatedInCurrentDay(userId) >= currentCs.getMaxSetsPerDay()) {
            throw new BadRequestException(String.format("Maximum of %d sets can be created in one day per user.",
                    currentCs.getMaxSetsPerDay()));
        }
        if (request.getIsAnonymous()) {
            throw new BadRequestException("Self-created card set must be public creator identity, cannot be anonymous.");
        }
        if (request.getFlashcards().size() > currentCs.getMaxFlashcardsPerSet()) {
            throw new BadRequestException(String.format("The maximum number of cards that can be created in a set is %d per user.",
                    currentCs.getMaxFlashcardsPerSet()));
        }
        for (FlashcardInitializeRequest flashcards : request.getFlashcards()) {
            if (flashcards.getImageData() != null && !flashcards.getImageData().isEmpty()) {
                throw new BadRequestException("User cannot be update any images.");
            }
        }
    }

    private void checkAddForRemainingUser(SetFlashcardInitializeRequest request, Long userId,
                                          CategorySubscriptionResponse currentCs) {
        if (request.getFlashcards().size() > currentCs.getMaxFlashcardsPerSet()) {
            throw new BadRequestException(String.format("The maximum number of cards that can be created in a set is %d per user.",
                    currentCs.getMaxFlashcardsPerSet()));
        }
    }

    private void checkUpdateForFreeUser(SetFlashcardRequest request, UserPrincipal up) {
        if (request.getIsAnonymous()) {
            throw new BadRequestException("Self-created card set must be public creator identity, cannot be anonymous.");
        }
    }

    @Override
    public void handleAddSetFlashcard(SetFlashcardInitializeRequest request) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        CategorySubscriptionResponse currentCs = this.categorySubscriptionService.getCategorySubscriptionBaseOfRoles();
        if (up.getRolesBaseAuthorities().contains(RoleName.ROLE_FREE_USER.name())) {
            checkAddForFreeUser(request, up.getId(), currentCs);
        } else if (up.getRolesBaseAuthorities().contains(RoleName.ROLE_PREMIUM_USER.name())) {
            checkAddForRemainingUser(request, up.getId(), currentCs);
        }
    }

    @Override
    public void handleUpdateSetFlashcard(SetFlashcardRequest request) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        if (up.getRolesBaseAuthorities().contains(RoleName.ROLE_FREE_USER.name())) {
            checkSetFlashcardOwner(request.getSetId(), up);
            checkUpdateForFreeUser(request, up);
        } else if (up.getRolesBaseAuthorities().contains(RoleName.ROLE_PREMIUM_USER.name())) {
            checkSetFlashcardOwner(request.getSetId(), up);
        }
    }

    @Override
    public void handleDeleteSetFlashcard(Long setId) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        if (!up.getRolesBaseAuthorities().contains(RoleName.ROLE_ADMIN.name())) {
            checkSetFlashcardOwner(setId, up);
        }
    }

    @Override
    public void handleAdminAddSetFlashcard(SetFlashcardInitializeRequest request, Long userId) {

    }

    @Override
    public void handleAdminUpdateSetFlashcard(SetFlashcardRequest request, Long userId) {

    }

    @Override
    public void handleAdminDeleteSetFlashcard(Long setId, Long userId) {

    }


}
