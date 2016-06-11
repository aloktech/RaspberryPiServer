/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.th;

import com.imos.pi.utils.ProcessExecutor;
import com.imos.pi.utils.HazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.imos.pi.utils.HttpMethod;
import static com.imos.pi.utils.RaspberryPiConstant.*;
import com.imos.pi.utils.RestClient;
import com.imos.pi.utils.TimeUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.ejb.Stateless;
import lombok.Getter;
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
    private final ConcurrentMap<String, String> restMap;
    private ConcurrentMap<String, String> map, tempMap;
    private final HazelcastInstance hazelcastInstance;
    @Getter
    private final List<String> command;
    private final List<String> paths;
    private final RestClient restClient;

    private final TimeUtils timeUtils;

    public TempAndHumidSensorController() {
        command = new ArrayList<>();
        command.add("sudo");
        command.add("python");
        command.add("/home/pi/Adafruit_Python_DHT/examples/AdafruitDHT.py");
        command.add("22");
        command.add("4");

        hazelcastInstance = HazelcastFactory.getInstance().getHazelcastInstance();

        timeUtils = new TimeUtils();

        restMap = new ConcurrentHashMap<>();

        paths = new ArrayList<>();
        paths.add(BASIC);
        paths.add(ELASTIC);
        paths.add(UPLOAD);
        paths.add(TEMP_HUMID_DB);
        paths.add(TEMP_HUMID);

        restClient = new RestClient();
    }

    public void executeTheSensor() {
        try {
            data = executeCommand(command);
            String timeWithDate = timeUtils.getCurrentTimeWithDate();
            temp = Double.parseDouble(data.substring(tempLength, data.indexOf(CELCIUS)));
            humid = Double.parseDouble(data.substring(data.indexOf(HUMIDITY) + humidLength, data.indexOf(PERCENTAGE)));

            map = hazelcastInstance.getMap(TEMP_HUMID_MAP);
            JSONObject jsonData = new JSONObject();
            jsonData.put(TEMP, temp);
            jsonData.put(HUMID, humid);
            map.put(timeWithDate, jsonData.toString());
            log.info(timeWithDate);
        } catch (NumberFormatException | JSONException e) {
            log.info(e.getMessage());
        }

    }

    public void saveDataInElasticSearch() {
        map = hazelcastInstance.getMap(TEMP_HUMID_MAP);
        tempMap = new ConcurrentHashMap<>(map);

        tempMap.keySet().removeAll(restMap.keySet());
        restMap.putAll(tempMap);

        JSONArray allData = new JSONArray();
        List<JSONObject> list = new ArrayList<>();
        tempMap.forEach((k, v) -> {
            JSONObject jdata = new JSONObject();
            jdata.put(TIME, k);
            jdata.put(DATA, new JSONObject(v));
            allData.put(jdata);
            list.add(jdata);
        });

        try {
            restClient.setPaths(paths);
            restClient.setHttpMethod(HttpMethod.POST);
            restClient.setData(list.toString());
            restClient.configure().setUrlPath().execute();
        } catch (Exception e) {
            log.severe(e.getMessage());
        }

    }

    public void saveDataAsJSON() {
        String fileName = timeUtils.getCurrentTimeWithDate();
        map = hazelcastInstance.getMap(TEMP_HUMID_MAP);

        JSONArray allData = new JSONArray();
        List<JSONObject> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            JSONObject jdata = new JSONObject();
            jdata.put(TIME, entry.getKey());
            jdata.put(DATA, new JSONObject(entry.getValue()));
            allData.put(jdata);
            list.add(jdata);
        }
        list.sort((JSONObject o1, JSONObject o2) -> {
            String val1, val2;
            val1 = o1.getString(TIME);
            val2 = o2.getString(TIME);
            String value1[], value2[];
            value1 = val1.split(UNDER_SCORE);
            value2 = val2.split(UNDER_SCORE);
            if (Integer.parseInt(value1[0]) == Integer.parseInt(value2[0])) {
                if (Integer.parseInt(value1[1]) == Integer.parseInt(value2[1])) {
                    if (Integer.parseInt(value1[2]) == Integer.parseInt(value2[2])) {
                        if (Integer.parseInt(value1[3]) == Integer.parseInt(value2[3])) {
                            if (Integer.parseInt(value1[4]) == Integer.parseInt(value2[4])) {
                                if (Integer.parseInt(value1[5]) == Integer.parseInt(value2[5])) {
                                    return Integer.parseInt(value1[6]) - Integer.parseInt(value2[6]);
                                } else {
                                    return Integer.parseInt(value1[5]) - Integer.parseInt(value2[5]);
                                }
                            } else {
                                return Integer.parseInt(value1[4]) - Integer.parseInt(value2[4]);
                            }
                        } else {
                            return Integer.parseInt(value1[3]) - Integer.parseInt(value2[3]);
                        }
                    } else {
                        return Integer.parseInt(value1[2]) - Integer.parseInt(value2[2]);
                    }
                } else {
                    return Integer.parseInt(value1[1]) - Integer.parseInt(value2[1]);
                }
            } else {
                return Integer.parseInt(value1[0]) - Integer.parseInt(value2[0]);
            }
        });

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".json"));) {
            writer.append(list.toString());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }

        saveDataInElasticSearch();
        
        map.clear();
        restMap.clear();
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
