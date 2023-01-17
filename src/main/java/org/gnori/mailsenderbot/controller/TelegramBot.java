package org.gnori.mailsenderbot.controller;

import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;

    private final UpdateController updateController;
    private final SendBotMessageService sendBotMessageService;

    public TelegramBot(UpdateController updateController, SendBotMessageService sendBotMessageService) {
        this.updateController = updateController;
        this.sendBotMessageService = sendBotMessageService;
    }
    @PostConstruct
    public void init(){
        sendBotMessageService.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
            var originalMessage = update.getMessage();
            updateController.processUpdate(update);
    }
    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
