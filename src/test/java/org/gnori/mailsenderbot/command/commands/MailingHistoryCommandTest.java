package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.dto.MailingHistoryDto;
import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForMessage;

public class MailingHistoryCommandTest extends AbstractCommandTest {
    private final ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);

    @Override
    public String getCommandMessage() {
        var id = 12L; // id from abstractTest
        var mailingHistory = Mockito.mock(MailingHistory.class);
        Mockito.when(mailingHistory.getMailingList()).thenReturn(Collections.emptyList());
        var mailingHistoryDto = new MailingHistoryDto(mailingHistory);

        Mockito.when(modifyDataBaseService.getMailingHistoryById(id)).thenReturn(mailingHistoryDto);
        return prepareTextForMessage(mailingHistoryDto);
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
        return new MailingHistoryCommand(modifyDataBaseService,getSendBotMessageService());
    }
}
