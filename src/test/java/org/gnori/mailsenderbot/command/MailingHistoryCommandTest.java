package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.MailingHistoryCommand;
import org.gnori.mailsenderbot.dto.MailingHistoryDto;
import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareTextForMessage;

public class MailingHistoryCommandTest extends AbstractCommandTest {
    private ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);

    @Override
    String getCommandMessage() {
        var id = 12L; // id from abstractTest
        var mailingHistory = Mockito.mock(MailingHistory.class);
        Mockito.when(mailingHistory.getMailingList()).thenReturn(Collections.emptyList());
        var mailingHistoryDto = new MailingHistoryDto(mailingHistory);

        Mockito.when(modifyDataBaseService.getMailingHistoryById(id)).thenReturn(mailingHistoryDto);
        return prepareTextForMessage(mailingHistoryDto);
    }

    @Override
    List<List<String>> getCallbackData() {
        return Collections.emptyList();
    }

    @Override
    boolean withButton() {
        return true;
    }

    @Override
    Command getCommand() {
        return new MailingHistoryCommand(modifyDataBaseService,getSendBotMessageService());
    }
}
