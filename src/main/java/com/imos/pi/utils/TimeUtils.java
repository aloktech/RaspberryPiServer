/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.utils;

import static com.imos.pi.utils.RaspberryPiConstant.SLASH;
import static com.imos.pi.utils.RaspberryPiConstant.UNDER_SCORE;
import static com.imos.pi.utils.RaspberryPiConstant.COLON;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Alok
 */
public class TimeUtils {

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
}
