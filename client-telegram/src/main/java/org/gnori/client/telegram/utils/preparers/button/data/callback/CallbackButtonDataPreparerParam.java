package org.gnori.client.telegram.utils.preparers.button.data.callback;

import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandType;

public record CallbackButtonDataPreparerParam(
        CallbackButtonDataPresetType type,
        MenuStepCommandType menuStep,
        boolean withBack,
        boolean hasCurrentMail
) {

    public CallbackButtonDataPreparerParam(
            CallbackButtonDataPresetType type,
            boolean hasCurrentMail
    ) {
        this(type, null, false, hasCurrentMail);
    }

    public static CallbackButtonDataPreparerParam onlyBack(MenuStepCommandType menuStep) {
        return new CallbackButtonDataPreparerParam(null, menuStep, true, false);
    }
}
