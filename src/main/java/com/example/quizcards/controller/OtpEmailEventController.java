package com.example.quizcards.controller;

import com.example.quizcards.dto.request.email.SenderTemplateEmailRequest;
import com.example.quizcards.service.IEmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpEmailEventController {
    IEmailService emailService;

    @KafkaListener(topics = "otp-email-change-password-delievery", groupId = "my-group")
    public void listenOtpEmailChangePasswordDelivery(SenderTemplateEmailRequest message){
        log.info("Message received: {}", message);
        emailService.sendOtpEmail(message);
    }
}
