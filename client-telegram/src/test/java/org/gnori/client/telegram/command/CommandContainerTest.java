package org.gnori.client.telegram.command;

import java.util.Arrays;
import org.gnori.client.telegram.command.commands.UnknownCommand;
import org.gnori.client.telegram.service.FileService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.client.telegram.service.impl.MessageTypesDistributorServiceImpl;
import org.gnori.client.telegram.service.impl.SendBotMessageServiceImpl;
import org.gnori.shared.utils.CryptoTool;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@DisplayName("Unit-level testing for CommandContainer")
public class CommandContainerTest {
    private CommandContainer commandContainer;

    @BeforeEach
    public void init() {
        var sendBotMessageService = Mockito.mock(SendBotMessageServiceImpl.class);
        var modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
        var messageRepository = Mockito.mock(MessageRepositoryService.class);
        var fileService = Mockito.mock(FileService.class);
        var rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        var cryptoTool = Mockito.mock(CryptoTool.class);
        var messageTypesDistributorService = Mockito.mock(MessageTypesDistributorServiceImpl.class);

        commandContainer = new CommandContainer(sendBotMessageService,
                                                modifyDataBaseService,
                                                messageRepository,
                                                rabbitTemplate,
                                                cryptoTool,
                                                fileService,
                                                "ex-name",
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