package org.gnori.client.telegram.service.command.utils.preparers.button.data.back;

import org.gnori.client.telegram.service.bot.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.model.button.CallbackButtonData;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BackButtonDataPreparer implements ButtonDataPreparer<ButtonData, MenuStepCommandType> {

    @Override
    public List<ButtonData> prepare(MenuStepCommandType menuStep) {
        return Collections.singletonList(getBackCallbackButtonData(menuStep));
    }

    private CallbackButtonData getBackCallbackButtonData(MenuStepCommandType menuStep) {

        return new CallbackButtonData(
                "Back",
                "%s%s%s".formatted(CallbackCommandType.BACK, CallbackCommandType.DATA_DELIMITER, menuStep.name())
        );
    }
}
