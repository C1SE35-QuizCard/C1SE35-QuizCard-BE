package com.example.quizcards.helpers.UserProgressHelpers;

import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.entities.UserProgress;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.exception.AccessDeniedException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.helpers.AuthenticationHelpers;
import com.example.quizcards.repository.IUserProgressRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ICustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserProgressHelpersImpl implements IUserProgressHelpers {
    @Autowired
    private ICustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationHelpers authenticationHelpers;

    @Autowired
    private IUserProgressRepository userProgressRepository;

    public UserProgressHelpersImpl(ICustomUserDetailsService customUserDetailsService,
                                   AuthenticationHelpers authenticationHelpers,
                                   IUserProgressRepository userProgressRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationHelpers = authenticationHelpers;
        this.userProgressRepository = userProgressRepository;
    }

    private void checkUserProgressOwner(Long progressId, UserPrincipal up) throws AccessDeniedException, ResourceNotFoundException {
        UserProgress userProgress = userProgressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("User Progress", "id", progressId));

        if (!up.getId().equals(userProgress.getAppUser().getUserId())) {
            throw new AccessDeniedException("You do not have permission to access this user progress");
        }
    }

    private void checkCurrentUserOwnerUserProgress(Long progressId) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        checkUserProgressOwner(progressId, up);
    }

    private void checkUserProgressExists(Long progressId) throws ResourceNotFoundException {
        IProgressDTO userProgressExists = userProgressRepository.findUserProgressById(progressId);

        if (userProgressExists == null) {
            throw new ResourceNotFoundException("User Progress", "id", progressId);
        }
    }

    public void handleDeleteUserProgress(Long progressId){

        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        if (!up.getRolesBaseAuthorities().contains(RoleName.ROLE_ADMIN.name())) {
            checkUserProgressOwner(progressId, up);
            checkUserProgressExists(progressId);
        }
        checkCurrentUserOwnerUserProgress(progressId);
    }

    public void handleAdminDeleteUserProgress(Long testId, Long userId){

    }

}
