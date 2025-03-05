package com.example.quizcards.service.impl;

import com.example.quizcards.dto.GoogleInfoUser;
import com.example.quizcards.dto.request.GoogleLoginRequest;
import com.example.quizcards.dto.response.GoogleAccessTokenResponse;
import com.example.quizcards.dto.response.GoogleUserInfoResponse;
import com.example.quizcards.service.IGoogleHandleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class GoogleHandleServiceImpl implements IGoogleHandleService {
    private ClientRegistrationRepository clientRegistrationRepository;

    private ClientRegistration clientRegistration = null;

    public GoogleHandleServiceImpl(ClientRegistrationRepository repository) {
        clientRegistrationRepository = repository;
        clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
    }

    private Map<String, String> getGoogleOAuth2Params(String code) {
        String clientId = clientRegistration.getClientId();
        String clientSecret = clientRegistration.getClientSecret();

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", "postmessage");
        params.put("grant_type", "authorization_code");

        return params;
    }

    private String getAccessTokenFromCode(String code) throws JsonProcessingException {
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();

        Map<String, String> params = getGoogleOAuth2Params(code);

        StringBuilder requestBody = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!requestBody.isEmpty()) {
                requestBody.append("&");
            }
            requestBody.append(entry.getKey()).append("=").append(entry.getValue());
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenUri, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();

            GoogleAccessTokenResponse response = mapper.readValue(responseEntity.getBody(), GoogleAccessTokenResponse.class);

            return response.getAccess_token();
        }

        return null;
    }

    private GoogleUserInfoResponse getInfoUserFromAccessToken(String accessToken) throws JsonProcessingException {
        String userInfoUri = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();

        String urlUserInfoBody = userInfoUri + "?access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> userDataJsonString = restTemplate.getForEntity(urlUserInfoBody, String.class);

        if (userDataJsonString.getStatusCode().is2xxSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(userDataJsonString.getBody(), GoogleUserInfoResponse.class);
        }

        return null;
    }

    @Override
    public GoogleInfoUser extractDataFromCode(GoogleLoginRequest request) throws Exception {
        if (clientRegistration == null) {
            throw new Exception("ERROR: Cannot find Google service data.");
        }

        String code = request.getCode();

        GoogleUserInfoResponse user = getInfoUserFromAccessToken(getAccessTokenFromCode(code));

        GoogleInfoUser userInfo = new GoogleInfoUser();

        assert user != null;
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatarUrl(user.getPicture());
        userInfo.setFirstName(user.getGiven_name());
        userInfo.setLastName(user.getFamily_name());
        userInfo.setUserCode(user.getSub());

        int atIndex = user.getEmail().indexOf("@");
        if (atIndex != -1) {
            userInfo.setUserName(user.getEmail().substring(0, atIndex));
        } else {
            userInfo.setUserName(user.getEmail());
        }

        userInfo.setEnabled(user.isEmail_verified());

        return userInfo;
    }
}
