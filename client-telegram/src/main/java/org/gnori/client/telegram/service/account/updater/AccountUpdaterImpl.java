package org.gnori.client.telegram.service.account.updater;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.command.utils.command.UtilsCommand;
import org.gnori.shared.crypto.CryptoTool;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUpdaterImpl implements AccountUpdater {

    private final CryptoTool cryptoTool;
    private final AccountService accountService;

    @Override
    public void updateState(long accountId, State state) {
        accountService.updateStateById(accountId, state);
    }

    @Override
    public Result<Empty, AccountUpdateFailure> updateMail(long accountId, String mail) {

        return UtilsCommand.validateMail(mail.trim())
                .mapFailure(failure -> AccountUpdateFailure.NOT_VALID_MAIL)
                .flatMapSuccess(validMail ->
                        accountService.updateMailById(accountId, validMail)
                                .mapFailure(failure -> AccountUpdateFailure.ALREADY_EXIST_MAIL)
                )
                .mapSuccess(validMail -> Empty.INSTANCE);
    }

    @Override
    public Result<Empty, AccountUpdateFailure> updateMailKey(long accountId, String mailKey) {

        return cryptoTool.encrypt(mailKey.trim())
                .doIfSuccess(encryptedKey -> accountService.updateKeyForMailById(accountId, encryptedKey))
                .mapSuccess(encryptedKey -> Empty.INSTANCE)
                .mapFailure(failure -> AccountUpdateFailure.BAD_ENCRYPT);
    }
}
