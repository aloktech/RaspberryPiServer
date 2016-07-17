/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.media;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Alok
 */
@Startup
@Singleton
public class MediaPlayerBean {
    
    @Schedule(second = "*/1", minute = "*", hour = "*", persistent = false)
    public void checkStatus() {
        
    }
}
