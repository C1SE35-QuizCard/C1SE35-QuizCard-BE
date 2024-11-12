package com.example.quizcards.helpers.FlashcardHelpers;

import com.example.quizcards.dto.request.FlashcardCreateRequest;
import com.example.quizcards.dto.request.FlashcardRequest;
import com.example.quizcards.entities.CategorySubscription;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.exception.AccessDeniedException;
import com.example.quizcards.exception.BadRequestException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.helpers.AuthenticationHelpers;
import com.example.quizcards.repository.IFlashcardRepository;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ICategorySubscriptionService;
import com.example.quizcards.service.ICustomUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class FlashcardHelpersImpl implements IFlashcardHelpers {
    private IFlashcardRepository flashcardRepository;

    private AuthenticationHelpers authenticationHelpers;

    private ISetFlashcardRepository setFlashcardRepository;

    private ICategorySubscriptionService categorySubscriptionService;

    private ICustomUserDetailsService customUserDetailsService;

    public FlashcardHelpersImpl(IFlashcardRepository flashcardRepository,
                                AuthenticationHelpers authenticationHelpers,
                                ISetFlashcardRepository setFlashcardRepository,
                                ICategorySubscriptionService subscriptionService,
                                ICustomUserDetailsService customUserDetailsService) {
        this.flashcardRepository = flashcardRepository;
        this.authenticationHelpers = authenticationHelpers;
        this.setFlashcardRepository = setFlashcardRepository;
        this.categorySubscriptionService = subscriptionService;
        this.customUserDetailsService = customUserDetailsService;
    }

    private void checkSetFlashcardOwner(Long setId, UserPrincipal up) throws AccessDeniedException, ResourceNotFoundException {
        SetFlashcard set = setFlashcardRepository.findById(setId).
                orElseThrow(() -> new ResourceNotFoundException("Set", "id", setId));

        if (!up.getId().equals(set.getUser().getUserId())) {
            throw new AccessDeniedException("You do not have permission to access this set flashcard");
        }
    }

    private void checkFlashcardExists(Long cardId, Long setId) throws ResourceNotFoundException {
        Integer flashcardExists = flashcardRepository.countCardsByIdAndSetId(cardId, setId);

        if (flashcardExists == null || flashcardExists == 0) {
            throw new ResourceNotFoundException(String.format("Card id: %d", cardId), "set id", setId);
        }
    }

    private void checkLimitCardForSpecificUser(FlashcardCreateRequest request, CategorySubscription currentCs) {
        Integer numCards = flashcardRepository.countNumberOfCardsInSet(request.getSetId());
        if (numCards >= currentCs.getMaxFlashcardsPerSet()) {
            throw new BadRequestException(String.format("The maximum number of cards that can be created in a set is %d per user.",
                    currentCs.getMaxFlashcardsPerSet()));
        }
    }

    private void checkAddForFreeUser(FlashcardCreateRequest request, UserPrincipal up,
                                     CategorySubscription currentCs) {
        checkLimitCardForSpecificUser(request, currentCs);
        if (request.getImageData() != null && !request.getImageData().isEmpty()) {
            throw new BadRequestException("User cannot be update any images.");
        }
    }

    private void checkAddForRemainingUser(FlashcardCreateRequest request, UserPrincipal up,
                                          CategorySubscription currentCs) {
        checkLimitCardForSpecificUser(request, currentCs);
    }

    private void checkUpdateForFreeUser(FlashcardRequest request, UserPrincipal up) {
        checkFlashcardExists(request.getCardId(), request.getSetId());
        if (request.getImageLink() != null && !request.getImageLink().isEmpty()) {
            throw new BadRequestException("User cannot be update any images.");
        }
    }

    private void checkUpdateForRemainingUser(FlashcardRequest request, UserPrincipal up) {
        checkFlashcardExists(request.getCardId(), request.getSetId());
    }

    @Override
    public void handleAddFlashcard(FlashcardCreateRequest request) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        CategorySubscription currentCs = this.categorySubscriptionService.getCategorySubscriptionBaseOfRoles();
        if (up.getRolesBaseAuthorities().contains(RoleName.ROLE_FREE_USER.name())) {
            checkSetFlashcardOwner(request.getSetId(), up);
            checkAddForFreeUser(request, up, currentCs);
        } else if (up.getRolesBaseAuthorities().contains(RoleName.ROLE_PREMIUM_USER.name())) {
            checkSetFlashcardOwner(request.getSetId(), up);
            checkAddForRemainingUser(request, up, currentCs);
        }
    }

    @Override
    public void handleUpdateFlashcard(FlashcardRequest request) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        if (up.getRolesBaseAuthorities().contains(RoleName.ROLE_FREE_USER.name())) {
            checkSetFlashcardOwner(request.getSetId(), up);
            checkUpdateForFreeUser(request, up);
        } else if (up.getRolesBaseAuthorities().contains(RoleName.ROLE_PREMIUM_USER.name())) {
            checkSetFlashcardOwner(request.getSetId(), up);
            checkUpdateForRemainingUser(request, up);
        }
    }

    @Override
    public void handleDeleteFlashcard(Long cardId, Long setId) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        if (!up.getRolesBaseAuthorities().contains(RoleName.ROLE_ADMIN.name())) {
            checkSetFlashcardOwner(setId, up);
            checkFlashcardExists(cardId, setId);
        }
    }

    @Override
    public void handleAdminAddFlashcard(FlashcardCreateRequest request, Long userId) {

    }

    @Override
    public void handleAdminUpdateFlashcard(FlashcardRequest request, Long userId) {

    }

    @Override
    public void handleAdminDeleteFlashcard(Long cardId, Long setId, Long userId) {

    }

}
