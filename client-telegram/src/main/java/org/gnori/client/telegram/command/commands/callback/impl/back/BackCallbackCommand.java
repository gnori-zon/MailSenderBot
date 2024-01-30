package org.gnori.client.telegram.command.commands.callback.impl.back;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandContainer;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BackCallbackCommand implements CallbackCommand {

    private final AccountService accountService;
    private final MenuStepCommandContainer menuStepCommandContainer;

    @Override
    public void execute(Account account, Update update) {

        final List<String> callbackParams = getCallbackParams(update);

        final MenuStepCommandType previousMenuStepCommandType = callbackParams.stream().findFirst()
                .map(MenuStepCommandType::valueOf)
                .map(MenuStepCommandType::getPreviousStep)
                .orElse(MenuStepCommandType.START);

        menuStepCommandContainer.get(previousMenuStepCommandType)
                .execute(account, update);

        accountService.updateStateById(account.getId(), State.DEFAULT);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.BACK;
    }
}
