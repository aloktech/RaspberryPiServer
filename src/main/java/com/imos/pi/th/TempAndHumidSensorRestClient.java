/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.th;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.java.Log;

/**
 *
 * @author Alok Ranjan
 */
@Stateless
@Log
@Path("rasp")
public class TempAndHumidSensorRestClient {
    @Inject
    private TempAndHumidSensorController controller;
    
    @Path("currentTAH")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCurentTemperaturAndHumidity() {
        return controller.executeCommand(controller.getCommand());
    }
}
