package org.gnori.client.telegram.service.command.utils.preparers.button.data.url;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.bot.message.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.message.model.button.UrlButtonData;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UrlButtonDataPreparer implements ButtonDataPreparer<ButtonData, UrlButtonDataPreparerParam> {

    private final ButtonDataPreparer<ButtonData, MenuStepCommandType> backButtonDataPreparer;

    @Override
    public List<ButtonData> prepare(UrlButtonDataPreparerParam param) {

        final List<ButtonData> buttonDataList = Optional.of(param.type())
                .map(type -> switch (type) {

                            case HELP_MAIL_CONFIGURATION -> getHelpMailConfigurationUrlButtonDataList();
                        })
                .orElseGet(ArrayList::new);

        if (param.withBack()) {
            buttonDataList.addAll(backButtonDataPreparer.prepare(param.menuStep()));
        }

        return buttonDataList.stream().toList();
    }

    private List<ButtonData> getHelpMailConfigurationUrlButtonDataList() {

        return new ArrayList<>(List.of(
                new UrlButtonData(
                        "Yandex",
                        "https://yandex.ru/support/id/authorization/app-passwords.html"
                ),
                new UrlButtonData(
                        "Gmail",
                        "https://support.google.com/accounts/answer/185833?hl=ru"
                ),
                new UrlButtonData(
                        "Mail",
                        "https://help.mail.ru/mail/security/protection/external"
                )
        ));
    }
}
