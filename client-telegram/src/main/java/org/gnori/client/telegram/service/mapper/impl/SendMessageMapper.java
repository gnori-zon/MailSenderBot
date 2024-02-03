package org.gnori.client.telegram.service.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.bot.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.model.message.SendBotMessageParam;
import org.gnori.client.telegram.service.mapper.Mapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SendMessageMapper implements Mapper<SendBotMessageParam, SendMessage> {

    private final Mapper<List<ButtonData>, InlineKeyboardMarkup> inlineKeyboardMapper;

    @Override
    public SendMessage map(SendBotMessageParam param) {

        return SendMessage.builder()
                .chatId(param.chatId())
                .text(param.text())
                .replyMarkup(inlineKeyboardMapper.map(param.buttonDataList()))
                .build();
    }
}
