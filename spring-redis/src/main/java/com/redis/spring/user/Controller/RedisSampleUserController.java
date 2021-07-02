package com.redis.spring.user.Controller;

import com.google.common.collect.ImmutableMap;
import com.redis.spring.user.domain.User;
import com.redis.spring.user.dto.BlockUserRequestDto;
import com.redis.spring.user.dto.RegisterUserRequestDto;
import com.redis.spring.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/redis-sample/v1")
public class RedisSampleUserController {

    private final UserService userService;

    public RedisSampleUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<String> users = userService.getUserNameList();

        return new ResponseEntity<>(ImmutableMap.of("users", users), HttpStatus.OK);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUser(
            @PathVariable("username") String userName
    ) throws IOException {
        User user = userService.getUser(userName);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterUserRequestDto requestDto
    ) throws IOException {

        User user = userService.registerUser(requestDto);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(
            @PathVariable("username") String username
    ) {
        userService.deleteUser(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/blocked-users/{username}")
    public ResponseEntity<?> isUserBlocked(
            @PathVariable("username") String username
    ) {
        boolean blocked = userService.isUserBlocked(username);

        long secondsLeft = userService.getUserBlockedSecondsLeft(username);

        return new ResponseEntity<>(ImmutableMap.of("unblock_after_seconds", secondsLeft), HttpStatus.OK);
    }

    @PostMapping("/blocked-users")
    public ResponseEntity<?> setBlockUser (
            @RequestBody BlockUserRequestDto requestDto
    ) {
        userService.setBlockUser(requestDto.getUsername());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/blocked-users/{username}")
    public ResponseEntity<?> unblockUser(
            @PathVariable("username") String username
    ) {
        userService.unblockUser(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
