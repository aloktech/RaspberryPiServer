/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.alarm;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 *
 * @author Alok
 */
@Log
public class AlarmRepository {

    private static AlarmRepository INSTANCE;

    @Getter
    private final Set<AlarmBean> alarms;

    private AlarmRepository() {
        alarms = new HashSet<>();

        populateRepository();
    }

    private void populateRepository() {
        SongBean songBean = new SongBean();
        songBean.setId(0);
        songBean.setSongName("Vishnu Sahasranamam Stotram-M S Subbulakshmi.mp3");
        songBean.setSongPath("/home/pi/Music/Vishnu Sahasranamam Stotram-M S Subbulakshmi.mp3");

        AlarmBean alarmBean = new AlarmBean();
        alarmBean.setSong(songBean);
        alarmBean.setAlarmName("");
        alarmBean.setAlarmType(AlarmType.RECURRING);
        alarmBean.setIncrementByDays(1);
        alarmBean.setEnable(true);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarmBean.setDateAndTime(calendar.getTime());
        alarmBean.setId(1);

        alarms.add(alarmBean);

        songBean = new SongBean();
        songBean.setId(1);
        songBean.setSongName("Defrost_the_freeze.mp3");
        songBean.setSongPath("/home/pi/Music/Defrost_the_freeze.mp3");

        alarmBean = new AlarmBean();
        alarmBean.setSong(songBean);
        alarmBean.setAlarmType(AlarmType.RECURRING);
        alarmBean.setEnable(true);
        alarmBean.setAlarmName("");
        alarmBean.setIncrementByDays(3);
        calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 55);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarmBean.setDateAndTime(calendar.getTime());
        alarmBean.setId(2);

        alarms.add(alarmBean);
    }

    public static AlarmRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AlarmRepository();
        }

        return INSTANCE;
    }

    public void addAlarm(AlarmBean alarmBean) {
        if (alarms.add(alarmBean)) {
            int index = alarms.size();
            alarmBean.setId(index);
            try {
                File file = new File(alarmBean.getSong().getSongPath());
                if (!file.exists()) {
                    Files.write(alarmBean.getSong().getSongData(), new File(alarmBean.getSong().getSongPath()));
                    log.log(Level.INFO, "{0} is added", alarmBean.getAlarmName());
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, "{0} failed to added", alarmBean.getAlarmName());
            }
        }
    }

    public void deleteAlarmById(int index) {
        AlarmBean ab = searchAndFindById(index);
        alarms.remove(ab);
        log.log(Level.INFO, "{0} is deleted", ab.getAlarmName());
    }

    public void deleteAlarmByName(String name) {
        AlarmBean ab = searchAndFindByName(name);
        alarms.remove(ab);
        log.log(Level.INFO, "{0} is deleted", ab.getAlarmName());
    }

    public void updateAlarm(AlarmBean alarmBean) {
        alarms.add(alarmBean);
        log.log(Level.INFO, "{0} is edited", alarmBean.getAlarmName());
    }

    public Set<AlarmBean> getAllAlarms() {
        return alarms;
    }

    public AlarmBean getAlarmById(int id) {
        return searchAndFindById(id);
    }

    AlarmBean searchAndFindById(int index) {
        for (AlarmBean ab : alarms) {
            if (ab.getId() == index) {
                return ab;
            }
        }
        return null;
    }

    public AlarmBean getAlarmByName(String name) {
        return searchAndFindByName(name);
    }

    AlarmBean searchAndFindByName(String name) {
        for (AlarmBean ab : alarms) {
            if (ab.getAlarmName().equals(name)) {
                return ab;
            }
        }
        return null;
    }
}
