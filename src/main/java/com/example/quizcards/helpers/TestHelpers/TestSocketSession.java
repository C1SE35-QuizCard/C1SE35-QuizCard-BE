package com.example.quizcards.helpers.TestHelpers;

import com.example.quizcards.service.impl.WebSocketHandlerFactoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestSocketSession {
    private static final Map<Long, List<String>> testSessions = new ConcurrentHashMap<>();

    public static void addNewTestSessions(Long testId, String sessionId) {
        testSessions.computeIfAbsent(testId, k -> new ArrayList<>()).add(sessionId);
    }

    public static void shutdownTest(Long testId) {
        if (testSessions.get(testId) != null) {
            List<String> sessions = new ArrayList<>(testSessions.get(testId));
            for (String sessionId : sessions) {
                try {
                    WebSocketHandlerFactoryService.closeSession(sessionId);
                } catch (Exception e) {
                    //
                }
            }
            testSessions.remove(testId);
        }
    }

    public static void removeSessionTest(Long testId, String sessionId) {
        List<String> listSessions = testSessions.get(testId);
        if (listSessions != null) {
            listSessions.remove(sessionId);
        }
    }
}
