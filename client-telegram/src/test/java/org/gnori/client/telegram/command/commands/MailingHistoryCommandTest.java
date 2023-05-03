package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForMessage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.gnori.client.telegram.command.Command;
import org.gnori.data.dto.MailingHistoryDto;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.gnori.store.entity.MailingHistory;
import org.mockito.Mockito;

public class MailingHistoryCommandTest extends AbstractCommandTest {
    private final ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);

    @Override
    public String getCommandMessage() {
        var id = 12L; // id from abstractTest
        var mailingHistory = Mockito.mock(MailingHistory.class);
        Mockito.when(mailingHistory.getMailingList()).thenReturn(Collections.emptyList());
        var mailingHistoryDto = new MailingHistoryDto(mailingHistory);

        Mockito.when(modifyDataBaseService.getMailingHistoryById(id)).thenReturn(Optional.of(mailingHistory));
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
