/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.alarm;

import com.imos.common.utils.ProcessExecutor;
import com.imos.pi.common.ScheduledExecution;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

/**
 *
 * @author Alok
 */
public class ScheduledTask extends TimerTask implements ScheduledExecution {

    private ProcessExecutor executor;
    @Getter
    private final AlarmBean alarm;
    private final Timer timer;

    public ScheduledTask(Timer timer, AlarmBean alarm) {
        this.timer = timer;
        this.alarm = alarm;
        alarm.setActive(true);
        alarm.setExecuted(false);
        AlarmRepository.getInstance().updateAlarm(alarm);
    }

    @Override
    public void run() {
        playMusic();
        cancel();
        timer.cancel();
        alarm.setExecuted(true);
        alarm.setActive(false);
        AlarmRepository.getInstance().updateAlarm(alarm);
    }

    private void playMusic() {
        List<String> cmd = new ArrayList<>();
        cmd.add("/usr/bin/sudo");
        cmd.add("omxplayer");
        cmd.add("-o");
        cmd.add("local");
        cmd.add(alarm.getSong().getSongPath());

        executeCommand(cmd);
    }

    @Override
    public String executeCommand(List<String> command) {
        String value = "";
        try {
            executor = new ProcessExecutor(command);
            value = executor.startExecution().getInputMsg();
        } catch (IOException ex) {
            Logger.getLogger(com.imos.pi.md.ScheduledTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }
}
