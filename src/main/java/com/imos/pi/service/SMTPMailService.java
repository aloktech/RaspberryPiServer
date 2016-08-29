/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.service;

import com.imos.pi.utils.SMTPMailUtils;
import com.imos.pi.utils.TimeUtils;
import static com.imos.pi.common.RaspberryPiConstant.*;
import java.io.File;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import lombok.extern.java.Log;

/**
 *
 * @author Alok Ranjan
 */
@Log
public class SMTPMailService {

    private final TimeUtils timeUtils;

    private final Properties mailProperties;

    private final Authenticator authenticator;

    private File folder;

    private File[] files;

    private Transport transport;

    public SMTPMailService() {

        timeUtils = new TimeUtils();

        mailProperties = SMTPMailUtils.MAIL_PROPERTIES;

        authenticator = SMTPMailUtils.AUTHENTICATOR;
    }

    public boolean sendMailWithAttachment(String videoFilePath) {
        // Get the default Session object.
        Session session = Session.getDefaultInstance(mailProperties, authenticator);

        try {
            folder = new File(videoFilePath);
            files = folder.listFiles();
            for (File file : files) {
                if (file.exists()) {
                    attachFile(file, session);
                    String fileName = file.getName();
                    if (fileName.endsWith(H264)) {
                        file.delete();
                    }
                    if (fileName.endsWith(MP4)) {
                        file.delete();
                    }
                    if (fileName.endsWith(TXT)) {
                        file.delete();
                    }
                }
            }
        } catch (Exception mex) {
            log.severe(mex.getMessage());
            return false;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException ex) {
                    log.severe(ex.getMessage());
                }
            }
            for (File file : files) {
                if (file.exists()) {
                    String fileName = file.getName();
                    if (fileName.endsWith(H264)) {
                        file.delete();
                    }
                    if (fileName.endsWith(MP4)) {
                        file.delete();
                    }
                    if (fileName.endsWith(TXT)) {
                        file.delete();
                    }
                }
            }
        }
        return true;
    }

    public boolean sendMailWithAttachment(String header, String videoFilePath, String timeFile) {

        // Get the default Session object.
        Session session = Session.getDefaultInstance(mailProperties, authenticator);

        try {
            folder = new File(videoFilePath);
            files = folder.listFiles();
            for (File file : files) {
                if (file.exists()) {

                    attachFile(file, session);
                    if (file.getName().endsWith(H264)) {
                        file.delete();
                    }
                    if (file.getName().endsWith(MP4)) {
                        file.delete();
                    }
                    if (file.getName().endsWith(TXT)) {
                        file.delete();
                    }
                }
            }
        } catch (Exception mex) {
            log.severe(mex.getMessage());
            return false;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException ex) {
                    log.severe(ex.getMessage());
                }
            }
        }
        return true;
    }

    private void attachFile(File tfile, Session session) {
        try {
            MimeMessage message;
            BodyPart messageBodyPart;
            DataSource source;
            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();
            if (tfile.getName().endsWith(".mp4")) {
                double bytes = tfile.length();
                double kilobytes = (bytes / 1024);
                double megabytes = (kilobytes / 1024);
                if (megabytes < 24) {
                    message = configure(session);
                    messageBodyPart = new MimeBodyPart();
                    source = new FileDataSource(tfile);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(source.getName());
                    multipart.addBodyPart(messageBodyPart);
                    message.setContent(multipart);

                    // Send message
                    transport = session.getTransport("smtp");
                    transport.connect("smtp.gmail.com", "alok.r.meher@gmail.com", "gun1anew*point");
                    transport.sendMessage(message, message.getAllRecipients());
                }
            }
        } catch (MessagingException e) {
            log.severe(e.getMessage());
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException ex) {
                    log.severe(ex.getMessage());
                }
            }
        }

    }

    public MimeMessage configure(Session session) throws MessagingException {
        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);
        // Set From: header field of the header.
        message.setFrom(new InternetAddress("alok.r.meher@gmail.com"));
        // Set To: header field of the header.
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("meher.ranjan12@gmail.com"));
        // Set Subject: header field
        message.setSubject("Recording from Raspberry Pi at time " + timeUtils.getTimeWithDate());
        // Now set the actual message
        message.setText("This is actual message");
        return message;
    }
}
