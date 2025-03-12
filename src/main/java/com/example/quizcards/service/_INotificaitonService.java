package com.example.quizcards.service;

import java.util.Map;

public interface _INotificaitonService {
    void saveSubscription(Map<String, Object> subscription);

    void sendNotification(String title, String body) throws Exception;
}
