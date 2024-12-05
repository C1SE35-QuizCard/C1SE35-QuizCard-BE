package com.example.quizcards.helpers.TestHelpers;

import com.example.quizcards.entities.Test;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.exception.AccessDeniedException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.helpers.AuthenticationHelpers;
import com.example.quizcards.repository.ITestRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ICustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class TestHelpersImpl implements ITestHelpers {
    @Autowired
    private ICustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationHelpers authenticationHelpers;

    @Autowired
    private ITestRepository testRepository;

    public TestHelpersImpl(ICustomUserDetailsService customUserDetailsService,
                             AuthenticationHelpers authenticationHelpers,
                           ITestRepository testRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationHelpers = authenticationHelpers;
        this.testRepository = testRepository;
    }

    private void checkTestOwner(Long testId, UserPrincipal up) throws AccessDeniedException, ResourceNotFoundException {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));

        if (!up.getId().equals(test.getUser().getUserId())) {
            throw new AccessDeniedException("You do not have permission to access this test");
        }
    }

    private void checkCurrentUserOwnerTest(Long testId) {
        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        checkTestOwner(testId, up);
    }

    private void checkTestExists(Long testId) throws ResourceNotFoundException {
        Integer testExists = testRepository.countTestsById(testId);

        if (testExists == null || testExists == 0) {
            throw new ResourceNotFoundException("Test", "id", testId);
        }
    }

    public void handleDeleteTest(Long testId){

        Authentication authentication = authenticationHelpers.getAuthenticationAuthenticated();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        if (!up.getRolesBaseAuthorities().contains(RoleName.ROLE_ADMIN.name())) {
            checkTestOwner(testId, up);
            checkTestExists(testId);
        }
        checkCurrentUserOwnerTest(testId);
    }

    public void handleAccessTest(Long testId) {
        checkCurrentUserOwnerTest(testId);
    }

    public void handleAdminDeleteTest(Long testId, Long userId){

    }

}
