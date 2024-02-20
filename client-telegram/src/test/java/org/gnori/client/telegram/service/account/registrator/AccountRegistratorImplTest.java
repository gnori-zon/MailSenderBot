package org.gnori.client.telegram.service.account.registrator;

import org.gnori.data.entity.Account;
import org.gnori.data.service.account.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@DisplayName("Unit-level testing for AccountRegistratorImpl")
class AccountRegistratorImplTest {

    private final AccountService mockAccountService = Mockito.mock(AccountService.class);
    private final AccountRegistrator registrator = new AccountRegistratorImpl(mockAccountService);

    @Test
    void successGetRegisterAccount() {

        final Long chatId = 1L;
        registrator.getRegisterAccount(chatId);

        Mockito.verify(mockAccountService).findByChatId(chatId);
    }

    @Test
    void successRegistrateBy() {

        final Chat mockChat = Mockito.mock(Chat.class);
        final User mockUser = Mockito.mock(User.class);
        final Update update = updateFrom(mockChat, mockUser);

        final long expectedChatId = 3L;
        Mockito.when(mockChat.getId()).thenReturn(expectedChatId);

        final String expectedFirstname = "firstname";
        Mockito.when(mockUser.getFirstName()).thenReturn(expectedFirstname);

        final String expectedLastname = "lastname";
        Mockito.when(mockUser.getLastName()).thenReturn(expectedLastname);

        final String expectedUsername = "username";
        Mockito.when(mockUser.getUserName()).thenReturn(expectedUsername);

        Mockito.when(mockAccountService.save(Mockito.any())).thenAnswer(invocation -> invocation.getArguments()[0]);


        final Account account = registrator.registrateBy(update);

        Assertions.assertEquals(expectedChatId, account.getChatId());
        Assertions.assertEquals(expectedFirstname, account.getFirstname());
        Assertions.assertEquals(expectedLastname, account.getLastname());
        Assertions.assertEquals(expectedUsername, account.getUsername());
    }

    private Update updateFrom(Chat chat, User user) {

        final ChatMemberUpdated chatMember = new ChatMemberUpdated();
        chatMember.setFrom(user);
        chatMember.setChat(chat);

        final Update update = new Update();
        update.setChatMember(chatMember);

        return update;
    }
}