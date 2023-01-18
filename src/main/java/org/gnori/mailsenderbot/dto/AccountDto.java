package org.gnori.mailsenderbot.dto;

import lombok.*;
import org.gnori.mailsenderbot.entity.Account;


public class AccountDto {
    private final String email;
    private final Boolean keyPresent;

    public AccountDto(Account account) {
        email = account.getEmail();
        keyPresent = !(account.getKeyForMail()==null || account.getKeyForMail().isEmpty());
    }

    public String getEmail(){
        return email;
    }
    public boolean hasKey(){
        return keyPresent;
    }
}
