package com.imos.pi.common;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Alok
 */
@Setter @Getter
public class MailProperty {

    private String mailSmtpHost, mailSmtpSslTrust, mailSmtpUser, mailSmtpPort, 
            mailSmtpAuth, to, from, subject, message;
    
    private boolean mailSmtpStarttlsEnable;
}
