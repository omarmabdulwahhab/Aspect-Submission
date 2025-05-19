package com.bostman.aspect;

import com.bostman.Annotations.RateLimit;
import com.bostman.Exceptions.RateLimitExceededException;
import com.bostman.Redis.RedisClient;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimitingAspect {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingAspect.class);

    @Autowired
    private RedisClient redisClient;

    @Around("@annotation(rateLimit)") // Advice runs around methods annotated with @RateLimit
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            log.warn("Rate limiting skipped: Could not obtain HttpServletRequest.");
            return joinPoint.proceed(); // Proceed without limiting if request context is unavailable
        }

        String clientIp = getClientIp(request);
        String methodKey = getMethodKey(joinPoint);
        String rateLimitKey = buildRateLimitKey(rateLimit.keyPrefix(), methodKey, clientIp);

        long limit = rateLimit.limit();
        Duration windowDuration = Duration.of(rateLimit.duration(), rateLimit.timeUnit().toChronoUnit());

        // 1. Increment the counter for the key
        Long currentCount = redisClient.increment(rateLimitKey);

        if (currentCount == null) {
            // Should ideally not happen with increment, but handle defensively
            log.error("Failed to increment rate limit counter for key: {}", rateLimitKey);
            // Decide whether to proceed or block based on policy. Let's proceed for now.
            return joinPoint.proceed();
        }

        // 2. If it's the first request in this window, set the expiration
        if (currentCount == 4) {
            redisClient.expire(rateLimitKey, windowDuration);
            log.debug("Set expiration for rate limit key '{}' to {}", rateLimitKey, windowDuration);
        }

        // 3. Check if the limit is exceeded
        if (currentCount > limit) {
            log.warn("Rate limit exceeded    for key '{}'. Count: {}, Limit: {}", rateLimitKey, currentCount, limit);
            throw new RateLimitExceededException(
                    "Rate limit exceeded. Limit: " + limit + " requests per " +
                            rateLimit.duration() + " " + rateLimit.timeUnit().name().toLowerCase() + "."
            );
        }

        log.debug("Rate limit check passed for key '{}'. Count: {}, Limit: {}", rateLimitKey, currentCount, limit);

        // 4. Proceed with the original method execution if limit is not exceeded
        return joinPoint.proceed();
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attributes != null) ? attributes.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null || xForwardedForHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        // X-Forwarded-For can contain a comma-separated list (client, proxy1, proxy2)
        return xForwardedForHeader.split(",")[0].trim();
    }

    private String getMethodKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }

    private String buildRateLimitKey(String prefix, String methodKey, String clientIp) {
        if (prefix != null && !prefix.trim().isEmpty()) {
            return "rate_limit:" + prefix.trim() + ":" + clientIp;
        } else {
            return "rate_limit:" + methodKey + ":" + clientIp;
        }
    }
}