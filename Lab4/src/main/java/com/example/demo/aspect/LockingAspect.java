package com.example.demo.aspect;

import com.example.demo.annotation.Locked;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class LockingAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(locked)")
    public Object lockMethod(ProceedingJoinPoint joinPoint, Locked locked) throws Throwable {
        String key = "lock:" + joinPoint.getSignature().toShortString();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "LOCKED", Duration.ofSeconds(10));

        if (Boolean.FALSE.equals(success)) {
            throw new IllegalStateException("Method is already locked");
        }

        try {
            return joinPoint.proceed();
        } finally {
            redisTemplate.delete(key);
        }
    }
}
