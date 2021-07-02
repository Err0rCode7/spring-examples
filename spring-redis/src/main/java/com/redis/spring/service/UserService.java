package com.redis.spring.service;

import com.redis.spring.domain.User;
import com.redis.spring.repository.RedisUserDAO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final RedisUserDAO redisUserDAO;

    public UserService(RedisUserDAO redisUserDAO) {
        this.redisUserDAO = redisUserDAO;
    }

    public User registerUser(String userName) throws IOException {
        User user = new User();
        user.setUsername(userName);
        user.setCreatedAt(LocalDateTime.now());

        redisUserDAO.setUser(user);

        return redisUserDAO.getUser(userName);
    }

    public User getUser(String userName) throws IOException {
        return redisUserDAO.getUser(userName);
    }

    public void deleteUser(String userName) {
        redisUserDAO.deleteUser(userName);
    }

    public List<String> getUserNameList() {
        return redisUserDAO.getAllUsers();
    }

    public boolean isUserBlocked(String userName) {
        return redisUserDAO.isUserBlocked(userName);
    }

    public long getUserBlockedSecondsLeft(String userName) {
        return redisUserDAO.getUserBlockedSecondsLeft(userName);
    }

    public void setBlockUser(String userName) {
        redisUserDAO.setUserBlocked(userName);
    }

    public void unblockUser(String userName) {
        redisUserDAO.deleteUserBlocked(userName);
    }
}
