package org.gnori.client.telegram.service.account.registrator;

import lombok.RequiredArgsConstructor;
import org.gnori.data.entity.Account;
import org.gnori.data.entity.MailingHistory;
import org.gnori.data.entity.enums.State;
import org.gnori.data.service.account.AccountService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountRegistratorImpl implements AccountRegistrator {

    private final AccountService accountService;

    @Override
    public Optional<Account> getRegisterAccount(Long chatId) {
        return accountService.findByChatId(chatId);
    }

    @Override
    public Account registrateBy(Update update) {

        final User user = extractUser(update);
        final Chat chat = extractChat(update);

        final Account account = Account.builder()
                .chatId(chat.getId())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .username(user.getUserName())
                .state(State.DEFAULT)
                .mailingHistory(new MailingHistory())
                .build();

        return accountService.save(account);
    }

    private User extractUser(Update update) {

        if (update.hasCallbackQuery()) {

            return update.getCallbackQuery()
                    .getFrom();
        }

        return update.getMessage()
                .getFrom();
    }

    private Chat extractChat(Update update) {

        return extractMessage(update)
                .getChat();
    }

    private Message extractMessage(Update update) {

        if (update.hasCallbackQuery()) {

            return update.getCallbackQuery()
                    .getMessage();
        }

        return update.getMessage();
    }
}
