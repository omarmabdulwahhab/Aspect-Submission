package com.bostman.aspect;


import com.bostman.Annotations.DistributedLock;
import com.bostman.Exceptions.LockAcquisitionException;
import com.bostman.Redis.RedisClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class LockingAspect {

    private static final Logger log = LoggerFactory.getLogger(LockingAspect.class);
    private static final String LOCK_KEY_FORMAT = "lock:%s:%s"; // Format: lock:<prefix>:<identifier>
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final ExpressionParser expressionParser = new SpelExpressionParser();


    @Autowired
    private RedisClient redisClient;

    @Around("@annotation(distributedLock)")
    public Object applyLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        String lockIdentifier = evaluateKeyIdentifier(distributedLock.keyIdentifierExpression(), method, args);
        if (lockIdentifier == null || lockIdentifier.trim().isEmpty()) {
            log.error("Failed to evaluate lock identifier expression '{}' for method {}",
                    distributedLock.keyIdentifierExpression(), method.getName());
            throw new LockAcquisitionException("Cannot acquire lock: Lock identifier could not be determined.");
        }

        String lockKey = String.format(LOCK_KEY_FORMAT, distributedLock.keyPrefix(), lockIdentifier);
        String lockValue = UUID.randomUUID().toString(); // Unique value for this lock attempt
        Duration leaseDuration = Duration.of(distributedLock.leaseTime(), distributedLock.timeUnit().toChronoUnit());

        boolean lockAcquired = false;
        try {
            log.debug("Attempting to acquire lock '{}' with value '{}' for {} {}",
                    lockKey, lockValue, distributedLock.leaseTime(), distributedLock.timeUnit().name());

            // Try to acquire the lock using setIfAbsent with expiration
            lockAcquired = redisClient.setIfAbsent(lockKey, lockValue, leaseDuration);

            if (lockAcquired) {
                log.info("Lock acquired successfully: '{}'", lockKey);
                // Proceed with the original method execution
                return joinPoint.proceed();
            } else {
                log.warn("Failed to acquire lock '{}', it is held by another process.", lockKey);
                throw new LockAcquisitionException("Resource is currently locked by another operation. Please try again later.");
            }
        } finally {
            // Release the lock ONLY if this specific attempt acquired it
            if (lockAcquired) {
                // Check if the lock value still matches ours before deleting
                // This prevents deleting a lock acquired by another process if our lease expired
                String currentValue = redisClient.get(lockKey);
                if (lockValue.equals(currentValue)) {
                    try {
                        Boolean deleted = redisClient.delete(lockKey);
                        if (Boolean.TRUE.equals(deleted)) {
                            log.info("Lock released successfully: '{}'", lockKey);
                        } else {
                            // This could happen if the key expired between the get and delete
                            log.warn("Attempted to release lock '{}', but it was not found (possibly expired).", lockKey);
                        }
                    } catch (Exception e) {
                        log.error("Failed to release lock '{}'. Manual cleanup might be required. Error: {}", lockKey, e.getMessage());
                        // Consider adding monitoring/alerting here
                    }
                } else {
                    log.warn("Did not release lock '{}' because the lock value changed (lease likely expired and lock re-acquired by another process). Current value: '{}', Expected: '{}'",
                            lockKey, currentValue, lockValue);
                }
            }
        }
    }

    /**
     * Evaluates the SpEL expression against the method arguments to get the lock identifier.
     */
    private String evaluateKeyIdentifier(String expressionString, Method method, Object[] args) {
        try {
            EvaluationContext context = new MethodBasedEvaluationContext(null, method, args, parameterNameDiscoverer);
            Object value = expressionParser.parseExpression(expressionString).getValue(context);
            return (value != null) ? value.toString() : null;
        } catch (Exception e) {
            log.error("Error evaluating SpEL expression '{}' for method {}: {}", expressionString, method.getName(), e.getMessage());
            return null;
        }
    }

    /**
     *
     * ParameterNameDiscoverer - Finding Argument Names
     * Core Issue: By default, Java method parameter names (like id, roomDTO) are often lost during compilation into bytecode.
     * What is ParameterNameDiscoverer?
     *  A Spring utility interface.
     *  DefaultParameterNameDiscoverer is the standard implementation.
     * Its Job: To discover the original parameter names of a method at runtime.
     * How?
     *  Looks for debug information embedded during compilation (requires compiling with the -parameters flag - standard in modern Spring Boot).
     *  Can use other strategies if debug info isn't available.
     * In the Aspect: Essential for linking the name used in the SpEL expression (e.g., #id) to the actual argument passed to the method.
     *
     * ExpressionParser (SpEL) - Accessing Argument Values
     * What is SpEL? Spring Expression Language - a powerful language for querying and manipulating objects at runtime.
     * What is ExpressionParser?
     * A Spring interface (SpelExpressionParser is the standard implementation).
     * Its Job: To parse a string containing a SpEL expression and allow it to be evaluated.
     * In the Aspect:
     *  Takes the keyIdentifierExpression string from the @DistributedLock annotation (e.g., "#id" or "#roomDTO.roomNumber").
     *  expressionParser.parseExpression(...): Creates an executable Expression object.
     *  expression.getValue(context): Executes the expression to retrieve the actual value (e.g., 123 or "101-A") from the method arguments.
     *
     * The Glue: EvaluationContext
     * Connecting the Dots: How does SpEL know what #id refers to?
     * MethodBasedEvaluationContext:
     *  Holds the context for evaluating SpEL against method arguments.
     *  Created with:
     *      The target Method.
     *      The actual argument values (Object[] args).
     *      The ParameterNameDiscoverer.
     *  How it Works:
     *      SpEL sees #id in the expression.
     *      It asks the EvaluationContext to resolve id.
     *      The context uses the ParameterNameDiscoverer to find which position in the args array corresponds to the parameter named id.
     *      It retrieves the value from that position in the args array.
     *      This value is returned as the result of the SpEL expression.
     *
     */
}
