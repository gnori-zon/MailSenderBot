package org.gnori.mailsenderbot.dto;

import lombok.*;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.enums.State;


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
}
