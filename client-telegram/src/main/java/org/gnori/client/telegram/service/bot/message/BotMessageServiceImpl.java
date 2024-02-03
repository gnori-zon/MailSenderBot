package org.gnori.client.telegram.service.bot.message;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.controller.TelegramBot;

import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.service.bot.message.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.bot.message.model.message.SendBotMessageParam;
import org.gnori.client.telegram.service.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
@Service
@RequiredArgsConstructor
public class BotMessageServiceImpl implements BotMessageService, BotMessageSender, BotMessageEditor {

    static final String ERROR_TEXT = "Error occurred: {}";
    private static final String DEFAULT_PARSE_MODE = "Markdown";

    private final Mapper<SendBotMessageParam, SendMessage> sendMessageMapper;
    private final Mapper<EditBotMessageParam, EditMessageText> editMessageTextMapper;

    private TelegramBot bot;

    @Override
    public void register(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public void edit(EditBotMessageParam param) {

        final EditMessageText editMessageText = editMessageTextMapper.map(param);
        editMessageText.setParseMode(DEFAULT_PARSE_MODE);
    }

    @Override
    public void send(SendBotMessageParam param) {

        final SendMessage sendMessage = sendMessageMapper.map(param);
        sendMessage.setParseMode(DEFAULT_PARSE_MODE);

        execute(sendMessage);
    }

    private void execute(BotApiMethod<?> botApiMethod) {

        try {
            bot.execute(botApiMethod);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT, e.getMessage());
        }
    }
}
