package org.dnd.timeet.meeting.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

/**
 * 활성 사용자 수 추적 및 세션 관리
 */
@Service
public class WebSocketSessionManager {

    private final AtomicInteger activeUserCount = new AtomicInteger(0);
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    public void addUserSession(String sessionId, Long userId) {
        sessionUserMap.put(sessionId, userId);
        activeUserCount.incrementAndGet();
    }

    public void removeUserSession(String sessionId) {
        if (sessionUserMap.remove(sessionId) != null) {
            activeUserCount.decrementAndGet();
        }
    }

    public int getActiveUserCount() {
        return activeUserCount.get();
    }
}
