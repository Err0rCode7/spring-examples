package com.redis.spring.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RegisterUserRequest {

    private String username;
}
