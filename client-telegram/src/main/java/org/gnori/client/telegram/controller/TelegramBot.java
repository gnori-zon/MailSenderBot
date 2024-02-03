package org.gnori.client.telegram.controller;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.controller.update.UpdateController;
import org.gnori.client.telegram.service.bot.message.BotMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final UpdateController updateController;
    private final BotMessageService botMessageService;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @PostConstruct
    public void init() {
        botMessageService.register(this);
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
