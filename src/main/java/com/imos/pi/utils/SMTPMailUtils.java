/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.utils;

import static com.imos.pi.common.JSONConstant.*;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import lombok.extern.java.Log;

/**
 *
 * @author Alok Ranjan
 */
@Log
public class SMTPMailUtils {

    public static final Properties MAIL_PROPERTIES;

    public static final Authenticator AUTHENTICATOR;

    static {
        MAIL_PROPERTIES = System.getProperties();

        // Setup mail server
        MAIL_PROPERTIES.setProperty(MAIL_SMTP_HOST, "smtp.gmail.com");
        MAIL_PROPERTIES.setProperty(MAIL_SMTP_SSL_TRUST, "smtp.gmail.com");
        MAIL_PROPERTIES.setProperty(MAIL_SMTP_USER, "RaspberryPi");
        MAIL_PROPERTIES.setProperty(MAIL_SMTP_PORT, "587");
        MAIL_PROPERTIES.setProperty(MAIL_SMTP_AUTH, "true");
        MAIL_PROPERTIES.setProperty(MAIL_SMTP_START_TLS_ENABLE, "true");

        AUTHENTICATOR = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("alok.r.meher@gmail.com", "gun1anew*point");
            }
        };
    }
}
