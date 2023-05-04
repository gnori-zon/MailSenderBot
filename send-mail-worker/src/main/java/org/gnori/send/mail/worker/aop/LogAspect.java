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
    private static final String ERROR_TEXT_FILE_SERVICE = "::ASPECT:: FileServiceImpl exception: ";
    private static final String ERROR_TEXT_MAIL_SENDER_SERVICE = "::ASPECT:: MailSenderServiceImpl exception: ";


    @Around("@annotation(org.gnori.send.mail.worker.aop.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        log.info("::ASPECT:: "+joinPoint.getSignature() + "executed in " + executionTime + "ms");

        return proceed;
    }

    @AfterThrowing(pointcut = "execution(* org.gnori.send.mail.worker.service.impl.FileServiceImpl.*(..))", throwing = "ex")
    public void afterThrowingFileServiceImplMethods(Exception ex) throws Throwable {
        log.error(ERROR_TEXT_FILE_SERVICE,ex);
    }
    @AfterThrowing(pointcut = "execution(* org.gnori.send.mail.worker.service.impl.MailSenderServiceImpl.*(..))", throwing = "ex")
    public void afterThrowingMailSenderServiceImplMethods(Exception ex) throws Throwable {
        log.error(ERROR_TEXT_MAIL_SENDER_SERVICE,ex);
    }

    @AfterReturning(pointcut = "execution(* org.gnori.send.mail.worker.service.impl.FileServiceImpl.*(..))", returning = "file")
    public void afterThrowingFileDownloaderByUrlServiceImplMethods(JoinPoint joinPoint, FileSystemResource file) {
        log.info("::ASPECT:: "+joinPoint.getSignature().getName()+"-> file size:"+file.getFile().length()+"B");
    }


}
