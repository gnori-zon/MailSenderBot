package org.gnori.client.telegram.service.account.updater;

import org.gnori.data.flow.Empty;
import org.gnori.data.flow.Result;
import org.gnori.data.entity.enums.State;

public interface AccountUpdater {

    void updateState(long accountId, State state);

    Result<Empty, AccountUpdateFailure> updateMail(long accountId, String mail);

    Result<Empty, AccountUpdateFailure> updateMailKey(long accountId, String mailKey);
}
