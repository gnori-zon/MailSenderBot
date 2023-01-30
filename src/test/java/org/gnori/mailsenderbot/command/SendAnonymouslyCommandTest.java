package org.gnori.mailsenderbot.command;

import lombok.SneakyThrows;
import org.gnori.mailsenderbot.command.commands.SendAnonymouslyCommand;
import org.gnori.mailsenderbot.command.commands.SendCurrentMailCommand;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.impl.MailSenderServiceImpl;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.mail.AuthenticationFailedException;
import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForBeginningMessage;

public class SendAnonymouslyCommandTest extends AbstractCommandTest{
    private MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    private MailSenderServiceImpl mailSenderService = Mockito.mock(MailSenderServiceImpl.class);
    private ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    @SneakyThrows
    @Override
    String getCommandMessage() {
    var id = 12L; // id from abstractTest

    var message = Mockito.mock(Message.class);
        Mockito.when(messageRepository.getMessage(id)).thenReturn(message);
        Mockito.when(mailSenderService.sendAnonymously(id, message)).thenReturn(0);

        return "неотправлено:❌"+"\nВыберите необходимый пункт👇🏿";
}

    @Override
    List<List<String>> getCallbackData() {
        return prepareCallbackDataForBeginningMessage();
    }

    @Override
    boolean withButton() {
        return false;
    }

    @Override
    Command getCommand() {
        return new SendAnonymouslyCommand(getSendBotMessageService(),
                                          modifyDataBaseService,
                                          messageRepository,
                                          mailSenderService);
    }

    @SneakyThrows
    @Test
    public void SuccessSend() throws AuthenticationFailedException {
        var id = 12L; // id from abstractTest
        int messageId = 12;
        var message = Mockito.mock(Message.class);
        var text = "отправлено✔" + "\nВыберите необходимый пункт👇🏿";
        var newCallbackData = prepareCallbackDataForBeginningMessage();
        var countMessages = message.getRecipients().size() * message.getCountForRecipient();
        var messageSentRecord = MessageSentRecord.builder().countMessages(countMessages).build();
        Mockito.when(messageRepository.getMessage(id)).thenReturn(message);
        Mockito.when(mailSenderService.sendAnonymously(id, message)).thenReturn(1);
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        org.telegram.telegrambots.meta.api.objects.Message messageTelegram = Mockito.mock(org.telegram.telegrambots.meta.api.objects.Message.class);
        Mockito.when(messageTelegram.getChatId()).thenReturn(id);
        Mockito.when(messageTelegram.getMessageId()).thenReturn(messageId);

        callbackQuery.setMessage(messageTelegram);
        update.setCallbackQuery(callbackQuery);
        var command = new SendAnonymouslyCommand(getSendBotMessageService(),modifyDataBaseService,messageRepository,mailSenderService);

        command.execute(update);

        Mockito.verify(sendBotMessageService).executeEditMessage(id, messageId, "Производится отправка...🛫", Collections.emptyList(), false);
        Mockito.verify(sendBotMessageService).executeEditMessage(id, messageId, text, newCallbackData, false);
        Mockito.verify(messageRepository).removeMessage(id);
        Mockito.verify(modifyDataBaseService).addMessageSentRecord(id, messageSentRecord);

    }
}
