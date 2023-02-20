package org.gnori.mailsenderbot.utils.forMail;

import lombok.Getter;
import lombok.Setter;

public class BasicEmailAddress {
    private String login;
    private String password;
    private StateEmail state;

    public BasicEmailAddress(String login, String password) {
        this.login = login;
        this.password = password;
        this.state = StateEmail.FREE;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public synchronized StateEmail getState() {
        return state;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public synchronized void setState(StateEmail state) {
        this.state = state;
    }
}
