package com.devit.chatapp.service.impl;

import com.devit.chatapp.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceServiceImpl implements PresenceService {



    private final RedisTemplate<String, String> redisTemplate;
    private static final long TTL_SECONDS = 60;

    @Override
    public void userConnected(String username) {
        String key = "online:" + username;
        redisTemplate.opsForValue().set(key, LocalDateTime.now().toString());
        redisTemplate.expire(key, TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void userHeartbeat(String username) {
        String key = "online:" + username;
        redisTemplate.expire(key, TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void userDisconnected(String username) {
        String onlineKey = "online:" + username;
        String lastSeenKey = "lastseen:" + username;



        redisTemplate.delete(onlineKey);
        log.info("DELETED IN USER DISCONNECT: {}", onlineKey);
        redisTemplate.opsForValue().set(lastSeenKey, LocalDateTime.now().toString());
        log.info("SEEN IN USER DISCONNECT: {}", lastSeenKey);
    }

    @Override
    public boolean isUserOnline(String username) {
        String key = "online:" + username;
        return redisTemplate.hasKey(key);
    }

    @Override
    public LocalDateTime getLastSeen(String username) {
        String lastSeenKey = "lastseen:" + username;
        String lastSeen = redisTemplate.opsForValue().get(lastSeenKey);
        if (lastSeen == null) return null;
        return LocalDateTime.parse(lastSeen);
    }
}
