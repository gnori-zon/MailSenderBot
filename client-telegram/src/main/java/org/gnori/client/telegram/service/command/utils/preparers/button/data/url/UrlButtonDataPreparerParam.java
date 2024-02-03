package org.gnori.client.telegram.service.command.utils.preparers.button.data.url;

import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;

public record UrlButtonDataPreparerParam(
        UrlButtonDataPresetType type,
        MenuStepCommandType menuStep,
        boolean withBack
) {}
