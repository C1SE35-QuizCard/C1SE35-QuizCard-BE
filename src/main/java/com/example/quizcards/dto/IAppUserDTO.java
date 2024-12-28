package com.example.quizcards.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IAppUserDTO {
    Long getUserId();
    String getAddress();
    String getAvatar();
    LocalDateTime getDateCreate();
    LocalDate getDateOfBirth();
    String getEmail();
    Boolean getEnable();
    String getFirstName();
    Boolean getGender();
    String getHashPassword();
    String getLastName();
    String getPhoneNumber();
    String getUserCode();
    String getUsername();
    Long getRoleId();
    String getRoleName();
}
