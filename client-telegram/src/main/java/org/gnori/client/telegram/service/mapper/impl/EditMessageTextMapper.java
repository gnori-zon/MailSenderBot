package org.gnori.client.telegram.service.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.bot.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.mapper.Mapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EditMessageTextMapper implements Mapper<EditBotMessageParam, EditMessageText> {

    private final Mapper<List<ButtonData>, InlineKeyboardMarkup> inlineKeyboardMapper;

    @Override
    public EditMessageText map(EditBotMessageParam param) {

        return EditMessageText.builder()
                .messageId(param.messageId())
                .chatId(param.chatId())
                .text(param.text())
                .replyMarkup(inlineKeyboardMapper.map(param.buttonDataList()))
                .build();
    }
}
