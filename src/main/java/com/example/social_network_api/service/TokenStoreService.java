package com.example.social_network_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenStoreService {
    private final StringRedisTemplate redis;

    // whitelist refresh token
    public void whitelistRefreshJti(String jti, String username, long ttlSeconds) {
        String key = "rt:wl:" + jti;
        redis.opsForValue().set(key, username, ttlSeconds, TimeUnit.SECONDS ); // key - value(username)
    }

    public String getUsernameByRefreshJti(String jti) {
        return redis.opsForValue().get("rt:wl:" + jti);
    }

    public void removeRefreshJti(String jti) {
        redis.delete( "rt:wl:" + jti );
    }




    // refresh token blacklist
    public void blacklistRefreshJti(String jti, long ttlSeconds) {
        String key = "rt:bl:" + jti;
        redis.opsForValue().set(key, "1", ttlSeconds, TimeUnit.SECONDS ); // "1": giá trị tùy ý
    }

    public boolean isRefreshJtiBlacklisted(String jti) {
        String key = "rt:bl:" + jti;
        return Boolean.TRUE.equals(redis.hasKey(key)) ;
    }



    // access token blacklist
    public void blacklistAccessJti(String jti, long ttlSeconds) {
        String key = "at:bl:" + jti;
        redis.opsForValue().set(key, "1", ttlSeconds, TimeUnit.SECONDS );
    }

    public boolean isAccessJtiBlacklisted(String jti) {
        String key = "at:bl:" + jti;
        return Boolean.TRUE.equals(redis.hasKey(key)) ;
    }
}
