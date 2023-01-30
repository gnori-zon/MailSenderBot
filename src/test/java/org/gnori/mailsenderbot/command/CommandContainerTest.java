package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.UnknownCommand;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.FileService;
import org.gnori.mailsenderbot.service.impl.MailSenderServiceImpl;
import org.gnori.mailsenderbot.service.impl.MessageTypesDistributorServiceImpl;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.gnori.mailsenderbot.service.impl.SendBotMessageServiceImpl;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

@DisplayName("Unit-level testing for CommandContainer")
public class CommandContainerTest {
    private CommandContainer commandContainer;

    @BeforeEach
    public void init() {
        SendBotMessageServiceImpl sendBotMessageService = Mockito.mock(SendBotMessageServiceImpl.class);
        ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
        MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
        FileService fileService = Mockito.mock(FileService.class);
        MailSenderServiceImpl mailSenderService = Mockito.mock(MailSenderServiceImpl.class);
        CryptoTool cryptoTool = Mockito.mock(CryptoTool.class);
        MessageTypesDistributorServiceImpl messageTypesDistributorService = Mockito.mock(MessageTypesDistributorServiceImpl.class);

        commandContainer = new CommandContainer(sendBotMessageService,
                                                modifyDataBaseService,
                                                messageRepository,
                                                mailSenderService,
                                                cryptoTool,
                                                fileService,
                                                messageTypesDistributorService);

    }

    @Test
    public void shouldGetAllTheExistingCommands() {
        //when-then
        Arrays.stream(CommandName.values())
                .forEach(commandName -> {
                    Command command = commandContainer.retrieveCommand(commandName.getCommandName());
                    Assertions.assertNotEquals(UnknownCommand.class, command.getClass());
                });

    }
    @Test
    public void shouldReturnUnknownCommand(){
        //given
        String nameCommand = "/blabla";
        //when
        Command command = commandContainer.retrieveCommand(nameCommand);
        //then
        Assertions.assertEquals(UnknownCommand.class,command.getClass());
    }
}