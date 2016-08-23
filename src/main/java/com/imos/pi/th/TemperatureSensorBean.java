/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.th;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 *
 * @author Alok Ranjan
 */
@Startup
@Singleton
public class TemperatureSensorBean {

    @Inject
    private TempAndHumidSensorController temperatureSensorBeanController;
    
    @Schedule(second = "0", minute = "*/1", hour = "*", persistent = false)
    public void detectSensorSignalInEveryMinutes() {
        temperatureSensorBeanController.executeTheSensor();
    }

    @Schedule(second = "0", minute = "0", hour = "0", persistent = false)
    public void saveDataAsJSONIn24Hours() {
        temperatureSensorBeanController.saveDataAsJSON();
    }
}
