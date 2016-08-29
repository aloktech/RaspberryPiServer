/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
