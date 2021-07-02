package com.redis.spring.service;

import com.redis.spring.domain.MyData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DataService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void test() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        MyData data = new MyData();
        data.setStudentId("1234566");
        data.setName("HongGilDong");
        valueOperations.set("key", data);
        MyData data2 = (MyData) valueOperations.get("key");
        System.out.println(data2);
    }
}
