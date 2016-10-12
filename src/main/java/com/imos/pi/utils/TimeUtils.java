/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.utils;

import com.imos.common.utils.Scheduler;
import com.imos.pi.common.DayLight;
import static com.imos.pi.common.RaspberryPiConstant.SLASH;
import static com.imos.pi.common.RaspberryPiConstant.UNDER_SCORE;
import static com.imos.pi.common.RaspberryPiConstant.COLON;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 *
 * @author Alok
 */
@Singleton
public class TimeUtils {
    
    public TimeUtils() {
    }
    
    private final Calendar INSTANCE = GregorianCalendar.getInstance();

    public String getTimeWithDate() {

        StringBuilder builder = new StringBuilder();
        INSTANCE.setTimeInMillis(System.currentTimeMillis());
        builder.append(INSTANCE.get(Calendar.HOUR_OF_DAY));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.MINUTE));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.SECOND));
        builder.append(COLON);
        builder.append(INSTANCE.get(Calendar.DAY_OF_MONTH));
        builder.append(SLASH);
        builder.append(INSTANCE.get(Calendar.MONTH));
        builder.append(SLASH);
        builder.append(INSTANCE.get(Calendar.YEAR));

        return builder.toString();
    }

    public String getCurrentDate() {

        StringBuilder builder = new StringBuilder();
        INSTANCE.setTimeInMillis(System.currentTimeMillis());
        builder.append(INSTANCE.get(Calendar.DAY_OF_MONTH));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.MONTH) + 1);
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.YEAR));

        return builder.toString();
    }

    public String getCurrentTime() {
        StringBuilder builder = new StringBuilder();
        INSTANCE.setTimeInMillis(System.currentTimeMillis());
        builder.append(INSTANCE.get(Calendar.HOUR_OF_DAY));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.MINUTE));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.SECOND));

        return builder.toString();
    }

    public String getYesterdayDate() {
        StringBuilder builder = new StringBuilder();
        INSTANCE.setTimeInMillis(System.currentTimeMillis());
        int tempDay = INSTANCE.get(Calendar.DAY_OF_MONTH), day;
        Calendar temp = GregorianCalendar.getInstance();
        temp.set(Calendar.MONTH, INSTANCE.get(Calendar.MONTH) - 1);
        temp.set(Calendar.DAY_OF_MONTH, 1);
        day = tempDay == 1 ? temp.getActualMaximum(Calendar.DAY_OF_MONTH) : tempDay - 1;
        builder.append(day);
        builder.append(UNDER_SCORE);
        builder.append(tempDay == 1 ? INSTANCE.get(Calendar.MONTH) : INSTANCE.get(Calendar.MONTH) + 1);
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.YEAR));

        return builder.toString();
    }

    public String getYesterdayTimeWithDate() {
        StringBuilder builder = new StringBuilder();
        INSTANCE.setTimeInMillis(System.currentTimeMillis());
        int tempDay = INSTANCE.get(Calendar.DAY_OF_MONTH), day;
        Calendar temp = GregorianCalendar.getInstance();
        temp.set(Calendar.MONTH, INSTANCE.get(Calendar.MONTH) - 1);
        temp.set(Calendar.DAY_OF_MONTH, 1);
        day = tempDay == 1 ? temp.getActualMaximum(Calendar.DAY_OF_MONTH) : tempDay - 1;
        builder.append(day);
        builder.append(UNDER_SCORE);
        builder.append(tempDay == 1 ? INSTANCE.get(Calendar.MONTH) : INSTANCE.get(Calendar.MONTH) + 1);
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.YEAR));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.HOUR_OF_DAY));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.MINUTE));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.SECOND));

        return builder.toString();
    }

    public long getYesterdayTime() {
        INSTANCE.setTimeInMillis(System.currentTimeMillis());
        int tempDay = INSTANCE.get(Calendar.DAY_OF_MONTH);
        int tempMonth = INSTANCE.get(Calendar.MONTH);
        Calendar temp = GregorianCalendar.getInstance();
        temp.set(Calendar.MONTH, tempDay == 1 ? tempMonth - 1 : tempMonth);
        temp.set(Calendar.DAY_OF_MONTH, tempDay == 1 ? temp.getActualMaximum(Calendar.DAY_OF_MONTH) : tempDay - 1);

        return temp.getTimeInMillis();
    }

    public String getCurrentTimeWithDate() {
        StringBuilder builder = new StringBuilder();
        INSTANCE.setTimeInMillis(System.currentTimeMillis());
        builder.append(INSTANCE.get(Calendar.DAY_OF_MONTH));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.MONTH) + 1);
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.YEAR));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.HOUR_OF_DAY));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.MINUTE));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.SECOND));

        return builder.toString();
    }

    public String getCurrentTimeWithDateInMillis() {
        StringBuilder builder = new StringBuilder();
        INSTANCE.setTimeInMillis(System.currentTimeMillis());
        builder.append(INSTANCE.get(Calendar.DAY_OF_MONTH));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.MONTH) + 1);
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.YEAR));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.HOUR_OF_DAY));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.MINUTE));
        builder.append(UNDER_SCORE);
        builder.append(INSTANCE.get(Calendar.SECOND));

        return builder.toString();
    }

    public long extractTime(Calendar cal, DayLight dayLight) {
        if (dayLight == DayLight.START) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        }

        return cal.getTimeInMillis();
    }
}
