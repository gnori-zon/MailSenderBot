package org.gnori.mailsenderbot.command;

public enum CommandName {
    REGISTRATION("/start"),
    BEGINNING("BEGINNING"),
    BACK("BACK"),
    MAILING_HISTORY("MAILING_HISTORY"),
    CREATE_MAILING("CREATE_MAILING"),
    CHANGE_MAIL("CHANGE_MAIL"),
    CHANGE_KEY("CHANGE_KEY"),
    KEY_FOR_MAIL_PENDING("KEY_FOR_MAIL_PENDING"),
    MAIL_PENDING("MAIL_PENDING"),
    PROFILE("PROFILE");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }
    public String getCommandName(){
        return commandName;
    }
}
