package com.redis.spring.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.redis.spring.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisUserDAO {

    private static final String BLOCKED_USER_KEY = "CACHES:BLOCKED_USERS:${USERNAME}";
    private static final String USER_KEY = "USERS:${USERNAME}";
    private final RedisConnectionFactory redisConnectionFactory;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, byte[]> messagePackRedisTemplate;
    private final ObjectMapper messagePackObjectMapper;

    public RedisUserDAO(
            RedisConnectionFactory redisConnectionFactory,
            StringRedisTemplate stringRedisTemplate,
            RedisTemplate<String, byte[]> messagePackRedisTemplate,
            ObjectMapper messagePackObjectMapper) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.stringRedisTemplate = stringRedisTemplate;
        this.messagePackRedisTemplate = messagePackRedisTemplate;
        this.messagePackObjectMapper = messagePackObjectMapper;
    }

    public Boolean isUserBlocked(String userName) {
        String key = StringSubstitutor.replace(
                BLOCKED_USER_KEY,
                ImmutableMap.of("USERNAME", userName)
        );

        Boolean hasKey = stringRedisTemplate.hasKey(key);

        return Objects.requireNonNullElse(hasKey, false);
    }

    public Long getUserBlockedSecondsLeft(String userName) {
        String key = StringSubstitutor.replace(
                BLOCKED_USER_KEY,
                ImmutableMap.of("USERNAME", userName)
        );

        Long secondsLeft = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);

        return Objects.requireNonNullElse(secondsLeft, 0L);
    }

    public void setUserBlocked(String userName) {
        String key = StringSubstitutor.replace(
                BLOCKED_USER_KEY,
                ImmutableMap.of("USERNAME", userName)
        );

        stringRedisTemplate.opsForValue().set(key, StringUtils.EMPTY, 5, TimeUnit.MILLISECONDS);
    }

    public void deleteUserBlocked(String userName) {
        String key = StringSubstitutor.replace(
                BLOCKED_USER_KEY,
                ImmutableMap.of("USERNAME", userName)
        );

        stringRedisTemplate.delete(key);
    }

    public User getUser(String userName) throws IOException {
        String key = StringSubstitutor.replace(
                USER_KEY,
                ImmutableMap.of("USERNAME", userName)
        );

        byte[] message = messagePackRedisTemplate.opsForValue().get(key);

        if (message == null) {
            return null;
        }

        return messagePackObjectMapper.readValue(message, User.class);
    }

    public void setUser(User user) throws JsonProcessingException {
        String key = StringSubstitutor.replace(
                USER_KEY,
                ImmutableMap.of("USERNAME", user.getUsername())
        );

        byte[] message = messagePackObjectMapper.writeValueAsBytes(user);

        messagePackRedisTemplate.opsForValue().set(key, message, 1, TimeUnit.HOURS);
    }

    public void deleteUser(String userName) {
        String key = StringSubstitutor.replace(
                USER_KEY,
                ImmutableMap.of("USERNAME", userName)
        );

        messagePackRedisTemplate.delete(key);
    }

    public List<String> getAllUsers() {
        String key = StringSubstitutor.replace(
                USER_KEY,
                ImmutableMap.of("USERNAME", "*")
        );

        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        ScanOptions options = ScanOptions.scanOptions().count(50).match(key).build();

        List<String> users = new ArrayList<>();
        Cursor<byte[]> cursor = redisConnection.scan(options);

        while (cursor.hasNext()) {
//            System.out.println(new String(cursor.next()));
            String user = StringUtils.replace(
                    new String(cursor.next()),
                    "USERS:",
                    ""
                    );

            users.add(user);
        }

        return users;
    }
}
