package org.gnori.mailsenderbot.command;

public enum CommandName {
    REGISTRATION("/start"),
    BEGINNING("BEGINNING"),
    MAILING_HISTORY("MAILING_HISTORY"),
    CREATE_MAILING("CREATE_MAILING"),
    CHANGE_MAIL("CHANGE_MAIL"),
    CHANGE_PASSWORD("CHANGE_PASSWORD"),
    PROFILE("PROFILE");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }
    public String getCommandName(){
        return commandName;
    }
}
