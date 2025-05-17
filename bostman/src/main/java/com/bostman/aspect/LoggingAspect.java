package com.bostman.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut for all public methods in the controller package
    @Pointcut("within(com.bostman.controller..*)")
    public void controllerMethods() {}

    // Pointcut for methods related to delivery status updates
    @Pointcut("execution(* com.bostman.service.DeliveryServiceImpl.updateDeliveryStatus(..))")
    public void deliveryStatusUpdateMethods() {}

    @Before("controllerMethods()")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logger.info("API CALL --- Method: {}, URI: {}, Arguments: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    Arrays.toString(joinPoint.getArgs()));
        } else {
            logger.info("API CALL --- Class: {}, Method: {}, Arguments: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterControllerMethod(JoinPoint joinPoint, Object result) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            // In a real scenario, you might get status from HttpServletResponse if available post-invocation
            // For simplicity, logging result as part of the response.
            logger.info("API RESPONSE --- Method: {}, URI: {}, Response: {}",
                    attributes.getRequest().getMethod(),
                    attributes.getRequest().getRequestURI(),
                    result != null ? result.toString() : "N/A");
        } else {
             logger.info("API RESPONSE --- Class: {}, Method: {}, Response: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    result != null ? result.toString() : "N/A");
        }
    }

    @Before("deliveryStatusUpdateMethods()")
    public void logBeforeDeliveryStatusUpdate(JoinPoint joinPoint) {
        logger.info("DELIVERY STATUS UPDATE --- Method: {}, Arguments: {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "deliveryStatusUpdateMethods()", returning = "result")
    public void logAfterDeliveryStatusUpdate(JoinPoint joinPoint, Object result) {
        logger.info("DELIVERY STATUS UPDATE --- Method: {}, Result: {}",
                joinPoint.getSignature().toShortString(),
                result != null ? result.toString() : "N/A");
    }
}
