package com.example.demo.aspect;

import com.example.demo.annotation.RateLimited;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingAspect {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_REQUESTS = 5;

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = "ratelimit:" + joinPoint.getSignature().toShortString();
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofSeconds(10));

        if (count > MAX_REQUESTS) {
            throw new RuntimeException("Rate limit exceeded");
        }

        return joinPoint.proceed();
    }
}
