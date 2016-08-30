/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.th;

import com.imos.pi.database.TimeTempHumidData;
import com.imos.pi.database.TempHumidData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imos.pi.utils.ProcessExecutor;
import static com.imos.pi.common.RaspberryPiConstant.*;
import com.imos.pi.database.DatabaseList;
import com.imos.pi.utils.TimeUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    @Getter
    private final List<String> command;

    private final TimeUtils timeUtils;

    private final ObjectMapper MAPPER = new ObjectMapper();
    @Setter
    @Getter
    private String baseFolder;

    @Inject
    private DatabaseList databaseList;

    public TempAndHumidSensorController() {
        command = new ArrayList<>();
        command.add("sudo");
        command.add("python");
        command.add("/home/pi/Adafruit_Python_DHT/examples/AdafruitDHT.py");
        command.add("22");
        command.add("4");

        timeUtils = new TimeUtils();

        baseFolder = "/home/pi/NetBeansProjects/RaspberryPiServer/";
    }

    public void executeTheSensor() {
        if (!System.getProperty("os.name").equals("Linux")) {
            return;
        }
        try {
            data = executeCommand(command);

            temp = Double.parseDouble(data.substring(tempLength, data.indexOf(CELCIUS)));
            humid = Double.parseDouble(data.substring(data.indexOf(HUMIDITY) + humidLength, data.indexOf(PERCENTAGE)));

            TempHumidData tempHumidData = new TempHumidData();
            tempHumidData.setHumidity(humid);
            tempHumidData.setTemperature(temp);

            TimeTempHumidData jsonData = new TimeTempHumidData();
            jsonData.setData(tempHumidData);
            jsonData.setTime(System.currentTimeMillis());

            databaseList.addData(jsonData);
            databaseList.setCurrentValue(jsonData);

            log.info(timeUtils.getCurrentTimeWithDate());
        } catch (NumberFormatException | JSONException e) {
            log.info(e.getMessage());
        }
    }

    public void saveDataAsJSON() {
        String fileName = timeUtils.getYesterdayTimeWithDate();
        long yesterdayTime = timeUtils.getYesterdayTime();

        final JSONArray arrayData = new JSONArray();
        databaseList.getDayData(yesterdayTime)
                .stream()
                .forEach(d -> {
                    try {
                        arrayData.put(new JSONObject(MAPPER.writeValueAsString(d)));
                    } catch (JsonProcessingException | JSONException e) {
                    }
                });

        try {
            Files.write(Paths.get(baseFolder + fileName + ".json"), arrayData.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }

        fileName = timeUtils.getYesterdayDate();
        new File(fileName).delete();

        final JSONArray allData = new JSONArray();
        DatabaseList.getInstance()
                .getAllData()
                .stream()
                .forEach(d -> {
                    try {
                        allData.put(new JSONObject(MAPPER.writeValueAsString(d)));
                    } catch (JsonProcessingException | JSONException e) {
                    }
                });
        try {
            Files.write(Paths.get(baseFolder + "allData.json"), allData.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
    }
    List<TimeTempHumidData> tempData;

    public void updateLocalDB() throws IOException {
        String fileName = timeUtils.getCurrentDate();
        File file = new File(baseFolder + fileName + ".json");

        if (file.exists()) {
            tempData = new ArrayList<>(Arrays.asList(MAPPER.readValue(file, TimeTempHumidData[].class)));
        } else {
            tempData = new ArrayList<>();
        }

        tempData.addAll(databaseList.getDayData(System.currentTimeMillis()));

        JSONArray arrayData = new JSONArray();
        tempData.stream()
                .sorted((d1, d2) -> Long.compare(d1.getTime(), d2.getTime()))
                .distinct()
                .forEach(d -> {
                    try {
                        arrayData.put(new JSONObject(MAPPER.writeValueAsString(d)));
                    } catch (JsonProcessingException | JSONException e) {
                    }
                });
        
        try {
            Files.write(Paths.get(baseFolder + fileName + ".json"), arrayData.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
            log.severe(ex.getMessage());
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
