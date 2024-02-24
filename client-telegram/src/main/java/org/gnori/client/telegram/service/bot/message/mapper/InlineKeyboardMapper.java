package org.gnori.client.telegram.service.bot.message.mapper;

import org.gnori.client.telegram.service.bot.message.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.message.model.button.CallbackButtonData;
import org.gnori.client.telegram.service.bot.message.model.button.UrlButtonData;
import org.gnori.client.telegram.service.mapper.Mapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class InlineKeyboardMapper implements Mapper<List<ButtonData>, InlineKeyboardMarkup> {

    @Override
    public InlineKeyboardMarkup map(List<ButtonData> buttonDataList) {

        final List<List<InlineKeyboardButton>> buttonRows = buttonDataList.stream()
                .map(this::map)
                .map(List::of)
                .toList();

        return new InlineKeyboardMarkup(buttonRows);
    }

    private InlineKeyboardButton map(ButtonData buttonData) {

        final InlineKeyboardButton inlineButton = new InlineKeyboardButton();
        inlineButton.setText(buttonData.text());

        if (buttonData instanceof CallbackButtonData callbackButtonData) {
            inlineButton.setCallbackData(callbackButtonData.callbackData());

        } else if (buttonData instanceof UrlButtonData urlButtonData) {
            inlineButton.setUrl(urlButtonData.url());
        }

        return inlineButton;
    }
}
