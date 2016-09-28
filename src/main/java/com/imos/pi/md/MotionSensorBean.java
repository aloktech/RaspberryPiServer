/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.md;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 *
 * @author Alok Ranjan
 */
@Singleton
@DependsOn(value = {"AlarmPlayerBean"})
@Startup
public class MotionSensorBean {

    @Inject
    private MotionSensorController motionSensorController;

    @PostConstruct
    public void init() {
        motionSensorController.execute();
    }

}
