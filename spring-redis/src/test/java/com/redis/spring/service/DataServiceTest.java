package com.redis.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataServiceTest {

    @Autowired
    DataService dataService;

    @Test
    @DisplayName("MyData 테스트")
    public void myData테스트() {
        dataService.test();
    }

}