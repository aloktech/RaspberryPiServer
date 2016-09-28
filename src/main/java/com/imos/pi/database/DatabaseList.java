/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import lombok.extern.java.Log;

/**
 *
 * @author Pintu
 */
@Log
@Startup
@Singleton
public class DatabaseList {

    private final static List<TimeTempHumidData> ALL_DATA = new ArrayList<>();

    private static final Map<String, TimeTempHumidData> CURRENT_MAP = new HashMap<>();

    public static final Map<Long, ListDayIndex> DAY_LIST_INDEX_MAP = new HashMap<>();

    public final Calendar CALENDAR = GregorianCalendar.getInstance();

    private static DatabaseList INSTANCE;

    private final ObjectMapper MAPPER = new ObjectMapper();

    private final String baseFolder;

    public DatabaseList() {
        baseFolder = "/home/pi/NetBeansProjects/RaspberryPiServer/";
    }

    @PostConstruct
    public void uploadData() {
        TimeTempHumidData[] array;
        try {
            array = MAPPER.readValue(new File(baseFolder + "allData.json"), TimeTempHumidData[].class);
            for (TimeTempHumidData value : array) {
                addData(value);
            }
        } catch (IOException ex) {
            Logger.getLogger(DatabaseList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int size() {
        return ALL_DATA.size();
    }

    public static DatabaseList getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseList();
        }

        return INSTANCE;
    }

    public void setCurrentValue(TimeTempHumidData data) {
        CURRENT_MAP.put("CURRENT", data);
    }

    public TimeTempHumidData getCurrentValue() {
        return CURRENT_MAP.get("CURRENT");
    }

    public void addData(TimeTempHumidData data) {
        if (!ALL_DATA.contains(data)) {
            ALL_DATA.add(data);

            int size = ALL_DATA.size();

            setStartTime(data.getTime(), false);

            ListDayIndex listDayIndex = DAY_LIST_INDEX_MAP.get(CALENDAR.getTimeInMillis());

            if (listDayIndex == null) {
                listDayIndex = new ListDayIndex();
                listDayIndex.setStartIndex(size - 1);
            }
            listDayIndex.setEndIndex(size - 1);
            DAY_LIST_INDEX_MAP.put(CALENDAR.getTimeInMillis(), listDayIndex);
        }
    }

    public List<TimeTempHumidData> getOneDayData(Long time) {
        setStartTime(time, false);

        ListDayIndex listDayIndex = DAY_LIST_INDEX_MAP.get(CALENDAR.getTimeInMillis());

        if (listDayIndex != null) {
            List<TimeTempHumidData> TEMP_ALL_DATA = new ArrayList<>(ALL_DATA);
            List<TimeTempHumidData> dayList = new ArrayList<>();
            dayList.addAll(TEMP_ALL_DATA.subList(listDayIndex.getStartIndex(), listDayIndex.getEndIndex()));
            try {
                LocalDate date = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
                dayList.addAll(Arrays.asList(MAPPER.readValue(new File(baseFolder + date.getDayOfMonth() + "_" + date.getMonth().getValue() + "_" + date.getYear() + ".json"),
                        TimeTempHumidData[].class)));
            } catch (IOException ex) {
                Logger.getLogger(DatabaseList.class.getName()).log(Level.SEVERE, null, ex);
            }
            return dayList
                    .stream()
                    .distinct()
                    .sorted((d1, d2) -> Long.compare(d1.getTime(), d2.getTime()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public List<TimeTempHumidData> getAllData() {
        ALL_DATA.sort((d1, d2) -> Long.compare(d1.getTime(), d2.getTime()));
        return ALL_DATA;
    }

    public ListDayIndex getDayIndex(Long dayTime) {
        return DAY_LIST_INDEX_MAP.get(dayTime);
    }

    public void setStartTime(Long time, boolean excludeDayofMonth) {
        CALENDAR.setTimeInMillis(time);
        if (excludeDayofMonth) {
            CALENDAR.set(Calendar.DAY_OF_MONTH, 1);
        }
        CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
        CALENDAR.set(Calendar.MINUTE, 0);
        CALENDAR.set(Calendar.SECOND, 0);
        CALENDAR.set(Calendar.MILLISECOND, 0);
    }
}
