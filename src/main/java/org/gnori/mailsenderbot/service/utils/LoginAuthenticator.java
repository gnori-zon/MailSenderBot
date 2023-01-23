package org.gnori.mailsenderbot.service.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class LoginAuthenticator extends Authenticator {
    PasswordAuthentication authentication = null;

    public LoginAuthenticator(String username, String password) {
        authentication = new PasswordAuthentication(username,password);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return authentication;
    }
}
