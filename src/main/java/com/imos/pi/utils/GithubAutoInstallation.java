/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alok
 */
public class GithubAutoInstallation {
    private static final String url = "https://github.com/aloktech/RaspberryPiServer";
    public static void main(String[] args) {
        try {
            URL u = new URL(url);
            InputStream input = u.openStream();
            BufferedInputStream inStream = new BufferedInputStream(input);
            String line;
//            while((line = inStream.read()) != null) {
//                
//            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(GithubAutoInstallation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GithubAutoInstallation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
