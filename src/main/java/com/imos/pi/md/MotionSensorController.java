/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.md;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 *
 * @author Alok Ranjan
 */
@Stateless
@Log
public class MotionSensorController {

    @Setter @Getter
    public AtomicBoolean canSendMail, cameraEnable;
    
    private ScheduledTask task;

    public MotionSensorController() {
        canSendMail = new AtomicBoolean(false);
        cameraEnable = new AtomicBoolean(true);
    }

    public void execute() {
        new Thread(() -> {
            try {
                gpioConfiguration();
            } catch (InterruptedException ex) {
                log.severe(ex.getMessage());
            }
        }).start();
    }

    public void gpioConfiguration() throws InterruptedException {

        System.out.println("<--Pi4J--> GPIO Control Example ... started.");
        GpioController gpio = null;
        GpioPinDigitalOutput led = null;
        try {
            // create gpio controller
            gpio = GpioFactory.getInstance();

            // provision gpio pin #01 as an output pin and turn on
            led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.LOW);

            motionSensor(gpio, led);

        } catch (InterruptedException e) {
            log.severe(e.getMessage());
            throw e;
        } finally {
            if (led != null) {
                led.low();
            }

            // stop all GPIO activity/threads by shutting down the GPIO controller
            // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
            if (gpio != null) {
                gpio.shutdown();
            }
        }
    }

    @Schedule(second = "0", minute = "*/1", hour = "*", persistent = false)
    public void statusUpdate() {
        if (canSendMail.get()) {
            try {
                Timer timer = new Timer();
                task = new ScheduledTask();
                timer.schedule(task, 0, 60000);
            } catch (Exception e) {
                log.severe(e.getMessage());
            } finally {
                canSendMail.set(false);
            }
        }
    }

    private void motionSensor(final GpioController gpio, final GpioPinDigitalOutput led) throws InterruptedException {
        // provision gpio pin #01 as an output pin and turn on
        final GpioPinDigitalInput sensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, "MotionSensor", PinPullResistance.PULL_DOWN);
        System.out.println("started");
        sensor.addListener(new MotionSensorListener(led, this));

        while (true) {
            Thread.sleep(300000);
        }
    }
}
