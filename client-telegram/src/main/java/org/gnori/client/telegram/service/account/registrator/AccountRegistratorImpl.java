package org.gnori.client.telegram.service.account.registrator;

import lombok.RequiredArgsConstructor;
import org.gnori.data.service.account.AccountService;
import org.gnori.data.entity.Account;
import org.gnori.data.entity.enums.State;
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


        final Message message = extractMessage(update);
        final User user = message.getFrom();
        final Chat chat = message.getChat();

        final Account account = Account.builder()
                .chatId(chat.getId())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .username(user.getUserName())
                .state(State.DEFAULT)
                .build();

        return accountService.save(account);
    }

    private Message extractMessage(Update update) {

        if (update.hasCallbackQuery()) {

            return update.getCallbackQuery()
                    .getMessage();
        }

        return update.getMessage();
    }
}
