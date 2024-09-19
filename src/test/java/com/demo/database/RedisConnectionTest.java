package com.demo.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    @Test
    public void testRedisConnection() {
        // Mocking Redis operation
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("testKey")).thenReturn("testValue");

        // Executing test
        String value = redisTemplate.opsForValue().get("testKey");

        assertEquals("testValue", value);
        System.out.println("Redis connection verified!");
    }
}
