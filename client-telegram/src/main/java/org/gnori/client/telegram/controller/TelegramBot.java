package org.gnori.client.telegram.controller;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.gnori.client.telegram.service.SendBotMessageService;

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
