package org.gnori.client.telegram.utils.preparers.button.data.url;

import com.google.common.collect.ImmutableList;
import org.gnori.client.telegram.service.bot.model.UrlButtonData;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UrlButtonDataPreparer implements ButtonDataPreparer<UrlButtonData, UrlButtonDataPresetType> {

    @Override
    public List<UrlButtonData> prepare(UrlButtonDataPresetType type) {

        return switch (type){
            case HELP_MAIL_CONFIGURATION -> getHelpMailConfigurationUrlButtonDataList();
        };
    }

    private List<UrlButtonData> getHelpMailConfigurationUrlButtonDataList() {

        return ImmutableList.of(
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
        );
    }
}
