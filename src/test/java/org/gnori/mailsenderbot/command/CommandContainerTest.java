package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.UnknownCommand;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.FileService;
import org.gnori.mailsenderbot.service.QueueManager;
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
        var sendBotMessageService = Mockito.mock(SendBotMessageServiceImpl.class);
        var modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
        var messageRepository = Mockito.mock(MessageRepository.class);
        var fileService = Mockito.mock(FileService.class);
        var queueManager = Mockito.mock(QueueManager.class);
        var cryptoTool = Mockito.mock(CryptoTool.class);
        var messageTypesDistributorService = Mockito.mock(MessageTypesDistributorServiceImpl.class);

        commandContainer = new CommandContainer(sendBotMessageService,
                                                modifyDataBaseService,
                                                messageRepository,
                                                queueManager,
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