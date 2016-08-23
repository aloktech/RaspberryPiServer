/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.utils;

import com.imos.pi.service.SMTPMailService;
import javax.ejb.Stateless;

/**
 *
 * @author Alok
 */
@Stateless
public class SentMailEvent {

    private final SMTPMailService mailService;

    public SentMailEvent() {
        this.mailService = new SMTPMailService();
    }

    public void sendMail() {
        mailService.sendMailWithAttachment("./");
    }
}
