package com.imos.pi.utils;


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
        MAIL_PROPERTIES.setProperty("mail.smtp.host", "smtp.gmail.com");
        MAIL_PROPERTIES.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        MAIL_PROPERTIES.setProperty("mail.smtp.user", "RaspberryPi");
        MAIL_PROPERTIES.setProperty("mail.smtp.port", "587");
        MAIL_PROPERTIES.setProperty("mail.smtp.auth", "true");
        MAIL_PROPERTIES.setProperty("mail.smtp.starttls.enable", "true");

        AUTHENTICATOR = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("alok.r.meher@gmail.com", "gun1anew*point");
            }
        };
    }
}
