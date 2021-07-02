package com.redis.spring.user.service;

import com.redis.spring.user.domain.User;
import com.redis.spring.user.dto.RegisterUserRequestDto;
import com.redis.spring.user.repository.RedisUserDAO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    private final RedisUserDAO redisUserDAO;

    public UserService(RedisUserDAO redisUserDAO) {
        this.redisUserDAO = redisUserDAO;
    }

    public User registerUser(RegisterUserRequestDto requestDto) throws IOException {

        redisUserDAO.setUser(requestDto.toEntity());

        return redisUserDAO.getUser(requestDto.getUsername());
    }

    public User getUser(String userName) throws IOException {
        if (isUserBlocked(userName)) {
            return null;
        }
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
