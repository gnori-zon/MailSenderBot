package org.gnori.mailsenderbot.service.impl;

import org.gnori.mailsenderbot.service.FileService;
import org.gnori.mailsenderbot.service.MailSenderService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.QueueManager;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.gnori.mailsenderbot.utils.forMail.BasicEmails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ThreadService {
    MailSenderService mailSenderService;
    @PostConstruct
    public void startThreads(){
        Thread mailSender = new Thread((Runnable) mailSenderService);
        mailSender.setDaemon(true);
        mailSender.setName("mailSenderService");
        mailSender.setPriority(1);
        mailSender.start();
    }

    public ThreadService(
        BasicEmails basicEmails,
        ModifyDataBaseService modifyDataBaseService,
        FileService fileService, CryptoTool cryptoTool,
        QueueManager queueManager) {

        mailSenderService = new MailSenderServiceImpl(basicEmails, modifyDataBaseService,
            fileService, cryptoTool, queueManager);
    }
}
