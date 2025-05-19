package com.bostman.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLayerExceptionLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLayerExceptionLoggingAspect.class);

    // Pointcut for all public methods in classes within the com.bostman.service package
    @Pointcut("within(com.bostman.service..*)")
    public void serviceLayerMethods() {}

    /**
     * Advice that logs exceptions thrown from methods in the service layer.
     *
     * @param joinPoint The join point representing the method execution.
     * @param ex        The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "serviceLayerMethods()", throwing = "ex")
    public void logServiceLayerException(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.error("Exception in service method: {}.{}() with cause = '{}' and exception = '{}'", 
                     className, methodName, ex.getCause() != null ? ex.getCause() : "NULL", ex.getMessage(), ex);
        
        // Example: You could add more specific logging or even notifications here based on exception type.
        // if (ex instanceof MyCriticalServiceException) {
        //     // send urgent notification
        // }
    }
} 