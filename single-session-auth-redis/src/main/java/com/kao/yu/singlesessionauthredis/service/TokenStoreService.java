package com.kao.yu.singlesessionauthredis.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenStoreService {
    private final RedisTemplate<String, String> redisTemplate;

    public void storeToken(String username, String token, long expirationMs) {
        redisTemplate.opsForValue().set("login:" + username, token, expirationMs, TimeUnit.MILLISECONDS);
    }

    public String getStoredToken(String username) {
        return redisTemplate.opsForValue().get("login:" + username);
    }

    public void removeToken(String username) {
        redisTemplate.delete("login:" + username);
    }
}
