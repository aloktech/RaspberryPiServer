/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.th;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.io.Files;
import com.imos.pi.utils.ProcessExecutor;
import com.imos.pi.utils.HazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.imos.pi.common.DayLight;
import static com.imos.pi.common.RaspberryPiConstant.*;
import com.imos.pi.service.HazelcastService;
import com.imos.pi.utils.TimeUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Alok Ranjan
 */
@Stateless
@Log
public class TempAndHumidSensorController {

    private String data;
    private double temp, humid;
    private final int tempLength = TEMPERATURE.length(), humidLength = HUMIDITY.length();
    private ProcessExecutor executor;
    private IMap<String, String> current;
    private IMap<Long, TimeTempHumidData> hazelcastDB, tempDB;
    private final HazelcastInstance hazelcastInstance;
    @Getter
    private final List<String> command;

    private final TimeUtils timeUtils;

    private final ObjectMapper MAPPER = new ObjectMapper();
    private final ObjectWriter ow;
    @Setter @Getter
    private String baseFolder;

    @Inject
    private HazelcastService hazelcastService;

    public TempAndHumidSensorController() {
        command = new ArrayList<>();
        command.add("sudo");
        command.add("python");
        command.add("/home/pi/Adafruit_Python_DHT/examples/AdafruitDHT.py");
        command.add("22");
        command.add("4");

        hazelcastInstance = HazelcastFactory.getInstance().getHazelcastInstance();

        timeUtils = new TimeUtils();

        ow = MAPPER.writer().withDefaultPrettyPrinter();
        
        baseFolder = "/home/pi/NetBeansProjects/RaspberryPiServer/";
    }

    public void executeTheSensor() {
        try {
            data = executeCommand(command);

            temp = Double.parseDouble(data.substring(tempLength, data.indexOf(CELCIUS)));
            humid = Double.parseDouble(data.substring(data.indexOf(HUMIDITY) + humidLength, data.indexOf(PERCENTAGE)));

            TempHumidData tempHumidData = new TempHumidData();
            tempHumidData.setHumidity(humid);
            tempHumidData.setTemperature(temp);

            TimeTempHumidData jsonData = new TimeTempHumidData();
            jsonData.setData(tempHumidData);
            long time = System.currentTimeMillis();
            jsonData.setTime(time);

            hazelcastDB = hazelcastInstance.getMap(TEMP_HUMID_MAP);
            current = hazelcastInstance.getMap(TEMP_HUMID_CURRENT);
            hazelcastDB.put(time, jsonData);

            current.put(CURRENT, ow.writeValueAsString(jsonData.getData()));
            log.info(timeUtils.getCurrentTimeWithDate());
        } catch (NumberFormatException | JSONException | JsonProcessingException e) {
            log.info(e.getMessage());
        }
    }

    public void saveDataAsJSON() {
        String fileName = timeUtils.getYesterdayTimeWithDate();
        long yesterdayTime = timeUtils.getYesterdayTime();
        
        System.out.println(fileName);

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(yesterdayTime);
        System.out.println(cal.getTime());
        JSONArray arrayData = new JSONArray();
        hazelcastService.extractDataForTimeRange(timeUtils.extractTime(cal, DayLight.START),
                timeUtils.extractTime(cal, DayLight.END))
                .stream()
                .forEach((entry) -> {
                    arrayData.put(new JSONObject(entry));
                });

        try {
            Files.write(arrayData.toString().getBytes(), new File(baseFolder + fileName + ".json"));
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
    }
    
    public void updateLocalDB() {
        if (tempDB == null) {
            tempDB = hazelcastDB;
        } else {
            Map<Long, TimeTempHumidData> temp = new HashMap<>(hazelcastDB);
        }
        
    }

    public String executeCommand(List<String> command) {
        String value = "";
        try {
            executor = new ProcessExecutor(command);
            value = executor.startExecution().getInputMsg();

        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
        return value;
    }
}
