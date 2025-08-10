package com.devit.chatapp.service;

import java.time.LocalDateTime;

public interface PresenceService {

    void userConnected(String username);

    void userHeartbeat(String username);

    void userDisconnected(String username);

    boolean isUserOnline(String username);

    LocalDateTime getLastSeen(String username);
}
