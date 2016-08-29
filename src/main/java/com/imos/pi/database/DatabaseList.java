/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
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

    public static final Map<Long, ListMonthIndex> MONTH_LIST_INDEX_MAP = new HashMap<>();

    public static final Map<Long, ListDayIndex> DAY_LIST_INDEX_MAP = new HashMap<>();

    private static DatabaseList INSTANCE;

    private final ObjectMapper MAPPER = new ObjectMapper();

    private final String baseFolder;

    public DatabaseList() {
        baseFolder = "/home/pi/NetBeansProjects/RaspberryPiServer/";
//        String baseFolder = "F:\\Tools\\Netbeans8.1Workspace\\RaspberryPiServer\\src\\main\\resources\\";
    }

    @PostConstruct
    public void uploadData() {
        JsonNode array;
        try {
            array = MAPPER.readValue(new File(baseFolder + "allData.json"), JsonNode.class);
            Iterator<JsonNode> itr = array.iterator();
            while (itr.hasNext()) {
                DatabaseList.getInstance().addData(MAPPER.readValue(MAPPER.writeValueAsString(itr.next()), TimeTempHumidData.class));
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

    public final static Calendar CALENDAR = GregorianCalendar.getInstance();

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

    public List<TimeTempHumidData> getDayData(Long time) {
        setStartTime(time, false);

        ListDayIndex listDayIndex = DAY_LIST_INDEX_MAP.get(CALENDAR.getTimeInMillis());

        if (listDayIndex != null) {
            List<TimeTempHumidData> TEMP_ALL_DATA = new ArrayList<>(ALL_DATA);
            List<TimeTempHumidData> dayList = TEMP_ALL_DATA.subList(listDayIndex.getStartIndex(), listDayIndex.getEndIndex())
                    .stream()
                    .sorted((d1, d2) -> Long.compare(d1.getTime(), d2.getTime()))
                    .distinct()
                    .collect(Collectors.toList());
            return dayList;
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
