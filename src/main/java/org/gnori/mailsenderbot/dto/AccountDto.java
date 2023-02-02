package org.gnori.mailsenderbot.dto;

import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.enums.State;

import java.util.Objects;


public class AccountDto {
    private final String email;
    private final Boolean keyPresent;
    private final State state;

    public AccountDto(Account account) {
        email = account.getEmail();
        keyPresent = !(account.getKeyForMail()==null || account.getKeyForMail().isEmpty());
        state = account.getState();
    }

    public String getEmail(){
        return email;
    }
    public boolean hasKey(){
        return keyPresent;
    }
    public State getState(){return state;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDto that = (AccountDto) o;
        return Objects.equals(getEmail(), that.getEmail()) && Objects.equals(keyPresent, that.keyPresent) && getState() == that.getState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), keyPresent, getState());
    }
}
