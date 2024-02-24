package org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep;

public enum MenuStepCommandType {

    START,
    PROFILE_INFO,
    SETUP_SETTINGS_ITEM,
    CREATE_MAILING,
    SETUP_ACTION_MAILING_ITEM,
    CHANGE_MESSAGE_ITEM,
    SETUP_CREATE_MAILING_ITEM;

    public MenuStepCommandType getPreviousStep() {

        return switch (this) {
            case START, PROFILE_INFO, CREATE_MAILING -> START;
            case SETUP_SETTINGS_ITEM -> PROFILE_INFO;
            case CHANGE_MESSAGE_ITEM, SETUP_ACTION_MAILING_ITEM -> CREATE_MAILING;
            case SETUP_CREATE_MAILING_ITEM -> CHANGE_MESSAGE_ITEM;
        };
    }
}
