package org.gnori.send.mail.worker.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class LogAspect {

    private static final String PROFILING_LOG_PATTERN = "[ASPECT] - {} executed in {}ms\"";

    @Around("@annotation(org.gnori.send.mail.worker.aop.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable{

        final long startTime = System.currentTimeMillis();

        final Object proceed = joinPoint.proceed();

        final long executionTime = System.currentTimeMillis() - startTime;

        log.info(PROFILING_LOG_PATTERN, joinPoint.getSignature(), executionTime);

        return proceed;
    }
}
