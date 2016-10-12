/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.alarm;

import java.util.Calendar;
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
        Calendar cal = GregorianCalendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        alarms.stream()
                .filter(a -> a.isEnable() && !a.isActive())
                .forEach(alarm -> {
                    Timer timer = new Timer();
                    ScheduledTask task = new ScheduledTask(timer, alarm);
                    cal.setTime(alarm.getDateAndTime());
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    if (AlarmType.RECURRING == alarm.getAlarmType() && (alarm.getIncrementByDays() == 0 || alarm.getIncrementByDays() > 1)) {
                        long delay = 0;
                        if (alarm.getIncrementByDays() > 1) {
                            delay = alarm.getIncrementByDays() * 24 * 3600 * 1000L;
                        } else if (alarm.getIncrementByHours() > 0) {
                            delay = alarm.getIncrementByHours() * 3600 * 1000L;
                        } else if (alarm.getIncrementByMinutes() > 0) {
                            delay = alarm.getIncrementByMinutes() * 60 * 1000L;
                        }
                        timer.scheduleAtFixedRate(task, cal.getTime(), delay);
                    } else {
                        timer.schedule(task, cal.getTime());
                    }
                });
    }
}
