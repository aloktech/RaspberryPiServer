/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.md;

import com.imos.pi.utils.SentMailEvent;
import com.imos.pi.utils.ProcessExecutor;
import static com.imos.pi.utils.RaspberryPiConstant.KILL;
import com.imos.pi.utils.TimeUtils;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;

/**
 *
 * @author Alok
 */
public class MotionSensorListener implements GpioPinListenerDigital {

    private GpioPinDigitalOutput led;
    private MotionSensorController motionSensorCtrl;
    private ProcessExecutor executor;
    private boolean recordingStarted;
    private final String endWithVideo = ".h264";
    private List<String> captureVideoForTime;
    private final int fileNameIndex = 12;
    private final int timeIndex = 14;

    private TimeUtils timeUtils;

    public MotionSensorListener() {
    }

    public MotionSensorListener(GpioPinDigitalOutput led, MotionSensorController motionSensorCtrl) {
        this.led = led;
        this.motionSensorCtrl = motionSensorCtrl;
        
        timeUtils = new TimeUtils();
        
        captureVideoForTime = new ArrayList<>();
        captureVideoForTime.add("raspivid");
        captureVideoForTime.add("-vf");
        captureVideoForTime.add("-hf");
        captureVideoForTime.add("-f");
        captureVideoForTime.add("-drc");
        captureVideoForTime.add("high");
        captureVideoForTime.add("-md");
        captureVideoForTime.add("1");
        captureVideoForTime.add("-a");
        captureVideoForTime.add("12");
        captureVideoForTime.add("-s");
        captureVideoForTime.add("-o");
        captureVideoForTime.add("");
        captureVideoForTime.add("-t");
        captureVideoForTime.add("");
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        if (event.getState().isHigh()) {
            led.high();
            if (!recordingStarted) {
                recordingStarted = true;
                captureVideoForTime(600000);
            }
        }

        if (event.getState().isLow() && recordingStarted) {
            recordingStarted = false;
            String value;

            List<String> command = new ArrayList<>();
            command.add("pgrep");
            command.add("raspivid");
            value = executeCommand(command);

            if (value.contains(" ")) {
                for (String pidValuel : value.split(" ")) {
                    command = new ArrayList<>();
                    command.add(KILL);
                    command.add(pidValuel);
                    executeCommand(command);
                }
            } else {
                command = new ArrayList<>();
                command.add(KILL);
                command.add(value);
                executeCommand(command);
            }

            led.low();

            motionSensorCtrl.canSendMail.set(true);
        }
    }

    public void sendMail(@Observes SentMailEvent sentMailEvent) {
        sentMailEvent.sendMail();
    }

    public ProcessExecutor captureVideoForTime(long time) {
        captureVideoForTime.set(fileNameIndex, createFileName());
        captureVideoForTime.set(timeIndex, String.valueOf(time));

        executeCommand(captureVideoForTime);
        return executor;
    }

    private String executeCommand(List<String> command) {
        String value = "";
        try {
            executor = new ProcessExecutor(command);
            value = executor.startExecution().getInputMsg().trim();
        } catch (IOException ex) {
            Logger.getLogger(MotionSensorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    private String createFileName() {
        StringBuilder fileName = new StringBuilder();
        fileName.append(timeUtils.getCurrentTime());
        fileName.append(endWithVideo);

        return fileName.toString();
    }
}
