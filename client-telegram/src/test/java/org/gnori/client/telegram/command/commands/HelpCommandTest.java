package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForHelpMessage;
import static org.gnori.client.telegram.utils.preparers.UrlDatePreparer.prepareUrlsForHelpMessage;

import java.util.Collections;
import java.util.List;
import org.gnori.client.telegram.command.Command;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpCommandTest extends AbstractCommandTest{
    @Override
    public String getCommandMessage() {
        return prepareTextForHelpMessage();
    }

    @Override
    public List<List<String>> getCallbackData() {
        return Collections.emptyList();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new HelpCommand(getSendBotMessageService());
    }
    @Test
    @Override
    public void shouldProperlyExecuteCommand() {
        var chatId = 12L;
        var messageId = 12;
        var urls = prepareUrlsForHelpMessage();
        var text = getCommandMessage();
        var newCallbackData = getCallbackData();
        var withButton = withButton();
        Update update = prepareUpdate(chatId, messageId);

        getCommand().execute(update);

        Mockito.verify(sendBotMessageService).executeEditMessage(chatId, messageId, text, newCallbackData, urls, withButton);


    }
}
