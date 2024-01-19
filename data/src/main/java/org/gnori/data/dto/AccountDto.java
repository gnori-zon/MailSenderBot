package org.gnori.data.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;

@Getter
@EqualsAndHashCode
public class AccountDto {

    private final State state;
    private final String email;
    private final boolean isPresentKey;

    public AccountDto(Account account) {

        state = account.getState();
        email = account.getEmail();
        isPresentKey = !(account.getKeyForMail() == null || account.getKeyForMail().isEmpty());
    }
}
