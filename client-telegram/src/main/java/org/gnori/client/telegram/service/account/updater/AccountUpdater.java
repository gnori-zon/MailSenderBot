package org.gnori.client.telegram.service.account.updater;

import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.store.entity.enums.State;

public interface AccountUpdater {

    void updateState(long accountId, State state);

    Result<Empty, AccountUpdateFailure> updateMail(long accountId, String mail);

    Result<Empty, AccountUpdateFailure> updateMailKey(long accountId, String mailKey);
}
