package com.redis.spring.user.dto;

import com.redis.spring.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RegisterUserRequestDto {

    private String username;

    public User toEntity() {
        return new User(this.username);
    }
}
