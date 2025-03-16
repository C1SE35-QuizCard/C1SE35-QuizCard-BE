package com.example.quizcards.service;


import com.example.quizcards.dto.request.email.SenderTemplateEmailRequest;
import com.example.quizcards.dto.request.email.TemplateEmailRequest;
import com.example.quizcards.dto.response.email.EmailResponse;

import java.util.List;

public interface IEmailService {
    void sendEmail(String to, String subject, String content);

    EmailResponse sendOtpEmail(SenderTemplateEmailRequest request);
}