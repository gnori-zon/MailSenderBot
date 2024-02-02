package org.gnori.client.telegram.service.registrator;

import lombok.RequiredArgsConstructor;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.gnori.store.repository.AccountRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountRegistratorImpl implements AccountRegistrator {

    private final AccountRepository accountRepository;

    @Override
    public Optional<Account> getRegisterAccount(Long chatId) {
        return accountRepository.findByChatId(chatId);
    }

    @Override
    public Account registrateBy(Update update) {

        final ChatMemberUpdated chatMember = update.getChatMember();
        final User user = chatMember.getFrom();

        final Account account = Account.builder()
                .chatId(chatMember.getChat().getId())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .username(user.getUserName())
                .state(State.DEFAULT)
                .build();

        return accountRepository.saveAndFlush(account);
    }
}
