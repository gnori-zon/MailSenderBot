package org.gnori.mailsenderbot.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.mail.PasswordAuthentication;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit-level testing for LoginAuthenticator")
class LoginAuthenticatorTest {

    @Test
    void getPasswordAuthenticationTest() {
        var username = "username12";
        var password = "password123";
        var authenticator = new LoginAuthenticator(username,password);
        var expectedPasswordAuth = new PasswordAuthentication(username,password);
        Assertions.assertEquals(expectedPasswordAuth.getPassword(), authenticator.getPasswordAuthentication().getPassword());
        Assertions.assertEquals(expectedPasswordAuth.getUserName(), authenticator.getPasswordAuthentication().getUserName());
    }
}