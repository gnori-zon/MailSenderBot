package org.gnori.client.telegram.service.command.commands.callback.impl.back;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.account.updater.AccountUpdater;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandContainer;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.data.entity.Account;
import org.gnori.data.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BackCallbackCommand implements CallbackCommand {

    private final AccountUpdater accountUpdater;
    private final MenuStepCommandContainer menuStepCommandContainer;

    @Override
    public void execute(Account account, Update update) {

        accountUpdater.updateState(account.getId(), State.DEFAULT);

        final List<String> callbackParams = getCallbackParams(update);
        final MenuStepCommandType previousMenuStepCommandType = callbackParams.stream().findFirst()
                .map(MenuStepCommandType::valueOf)
                .map(MenuStepCommandType::getPreviousStep)
                .orElse(MenuStepCommandType.START);

        menuStepCommandContainer.get(previousMenuStepCommandType)
                .execute(account, update);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.BACK;
    }
}
