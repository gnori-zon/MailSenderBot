package org.gnori.send.mail.worker.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public final class LoginAuthenticator extends Authenticator {
    private final PasswordAuthentication authentication;

    public LoginAuthenticator(String username, String password) {
        authentication = new PasswordAuthentication(username,password);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return authentication;
    }
}
