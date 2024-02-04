package org.gnori.client.telegram.service.command.utils.preparers.text.param;

public record WithOptionalPartTextPreparerParam(
        String text,
        String optionalPart,
        boolean includeOptionalPart
) implements TextPreparerParam {

    public static WithOptionalPartTextPreparerParam selectSendMessageMode(boolean existCurrentMail) {

        return new WithOptionalPartTextPreparerParam(
                "*Choose a sending method:*",
                "\nYou can add your mail to send",
                existCurrentMail
        );
    }
}
