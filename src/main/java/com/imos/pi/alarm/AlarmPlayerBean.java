/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.alarm;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.Timer;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Alok
 */
@Startup
@Singleton
public class AlarmPlayerBean {

    @Schedule(second = "0", minute = "0", hour = "0", persistent = false)
    public void checkStatusDaily() {

        Set<AlarmBean> alarms = AlarmRepository.getInstance().getAllAlarms();
        alarms.stream()
                .filter(a -> a.isEnable())
                .forEach(a -> {
                    Timer timer = new Timer();
                    ScheduledTask task = new ScheduledTask();
                    task.setAlarm(a);
                    Date date = AlarmType.DAILY.equals(a.getAlarmType()) ? a.getHourAndMinute() : a.getDateAndTimeToPlay();
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.setTime(date);
                    timer.schedule(task, cal.getTime());
                });
    }
}
