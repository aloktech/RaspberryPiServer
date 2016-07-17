/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.alarm;

import com.imos.pi.common.ScheduledExecution;
import com.imos.pi.utils.ProcessExecutor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Setter;

/**
 *
 * @author Alok
 */
public class ScheduledTask extends TimerTask implements ScheduledExecution {

    private ProcessExecutor executor;
    @Setter
    private AlarmBean alarm;

    @Override
    public void run() {
        playMusic();
    }

    private void playMusic() {
        List<String> cmd = new ArrayList<>();
        cmd.add("sudo");
        cmd.add("omxplayer");
        cmd.add(alarm.getSong().getSongPath());
        
        executeCommand(cmd);
        cancel();
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
