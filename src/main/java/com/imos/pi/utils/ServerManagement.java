/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Alok
 */
@Singleton
@Startup
public class ServerManagement {

    private final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

    private static final String URL_I = "http://192.168.1.32:8090/BasicRESTService/";
    private static final String URL_II = "http://192.168.1.33:8090/BasicRESTService/";
    private static final String URL_III = "http://192.168.1.34:8090/BasicRESTService/";
    private static final String URL_IV = "http://192.168.1.35:8090/BasicRESTService/";

    public static volatile String URL = URL_III;

//    @Schedule(second = "0", minute = "*/20", hour = "*", persistent = false)
//    void checkServerUrl() {
//        try {
//            setCurrentUrl(URL_I);
//        } catch (InterruptedException | ExecutionException ex) {
//            Logger.getLogger(ServerManagement.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
//            setCurrentUrl(URL_II);
//        } catch (InterruptedException | ExecutionException ex) {
//            Logger.getLogger(ServerManagement.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
//            setCurrentUrl(URL_III);
//        } catch (InterruptedException | ExecutionException ex) {
//            Logger.getLogger(ServerManagement.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
//            setCurrentUrl(URL_IV);
//        } catch (InterruptedException | ExecutionException ex) {
//            Logger.getLogger(ServerManagement.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    private void setCurrentUrl(String url) throws InterruptedException, ExecutionException {
//        CompletableFuture.supplyAsync(() -> {
//            return (getResponseCode(url) == 200) ? url : "";
//        }, EXECUTOR).whenComplete((a, b) -> {
//            URL = (a == null || a.trim().isEmpty()) ? "" : a;
//        });
//    }
//
//    private static int getResponseCode(String urlString) {
//        HttpURLConnection huc = null;
//        try {
//            URL u = new URL(urlString);
//            huc = (HttpURLConnection) u.openConnection();
//            huc.setRequestMethod("GET");
//            huc.connect();
//            return huc.getResponseCode();
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(ServerManagement.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(ServerManagement.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (huc != null) {
//                huc.disconnect();
//            }
//        }
//        return 0;
//    }
}
