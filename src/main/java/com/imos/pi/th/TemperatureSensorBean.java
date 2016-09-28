/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.th;

import java.io.IOException;
import javax.ejb.DependsOn;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import lombok.extern.java.Log;

/**
 *
 * @author Alok Ranjan
 */
@Startup
@Singleton
@DependsOn(value = {"DatabaseList"})
@Log
public class TemperatureSensorBean {

    @Inject
    private TempAndHumidSensorController temperatureSensorBeanController;

    @Schedule(second = "0", minute = "*/1", hour = "*", persistent = false)
    public void detectSensorSignalInEveryMinutes() {
        temperatureSensorBeanController.executeTheSensor();
    }

    @Schedule(second = "0", minute = "0", hour = "0", persistent = false)
    public void saveDataAsJSONIn24Hours() {
        try {
            temperatureSensorBeanController.saveDataAsJSON();
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
    }

//    @Schedule(second = "0", minute = "*/30", hour = "*", persistent = false)
    public void saveDataAsJSONIn30Minutes() {
        try {
            temperatureSensorBeanController.updateLocalDB();
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
    }
}
