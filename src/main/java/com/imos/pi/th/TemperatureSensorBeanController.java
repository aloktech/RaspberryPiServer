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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Alok Ranjan
 */
@Stateless
public class TemperatureSensorBeanController {

    private String data;
    private final String temperatue = "Temp=", celcius = "*", humidity = "Humidity=", percentage = "%",
            tempStr = "temp", humidStr = "humid", tempHumidMap = "temp:humid-map", separator = "_",
            timeStr = "time", dataStr = "data";
    private double temp, humid;
    private final int tempLength = temperatue.length(), humidLength = humidity.length();
    private ProcessExecutor executor;
    private final ConcurrentMap<String, String> restMap;
    private ConcurrentMap<String, String> map, tempMap;
    private final HazelcastInstance hazelcastInstance;
    private final List<String> command;
    private final List<String> paths;
    private final RestClient restClient;

    private final TimeUtils timeUtils;

    public TemperatureSensorBeanController() {
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
        paths.add("basic");
        paths.add("elastic");
        paths.add("upload");
        paths.add("temp-humid-db");
        paths.add("temp-humid");

        restClient = new RestClient();
    }

    public void executeTheSensor() {
        try {
            data = executeCommand(command);
            String timeWithDate = timeUtils.getCurrentTimeWithDate();
            temp = Double.parseDouble(data.substring(tempLength, data.indexOf(celcius)));
            humid = Double.parseDouble(data.substring(data.indexOf(humidity) + humidLength, data.indexOf(percentage)));

            map = hazelcastInstance.getMap(tempHumidMap);
            JSONObject jsonData = new JSONObject();
            jsonData.put(tempStr, temp);
            jsonData.put(humidStr, humid);
            map.put(timeWithDate, jsonData.toString());
            Logger.getLogger(TemperatureSensorBeanController.class.getName()).log(Level.INFO, timeWithDate);
        } catch (NumberFormatException | JSONException e) {
            Logger.getLogger(TemperatureSensorBeanController.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void saveDataThroughRESTService() {
        map = hazelcastInstance.getMap(tempHumidMap);
        tempMap = new ConcurrentHashMap<>(map);

        tempMap.keySet().removeAll(restMap.keySet());
        restMap.putAll(tempMap);

        JSONArray allData = new JSONArray();
        List<JSONObject> list = new ArrayList<>();
        tempMap.forEach((k, v) -> {
            JSONObject jdata = new JSONObject();
            jdata.put(timeStr, k);
            jdata.put(dataStr, new JSONObject(v));
            allData.put(jdata);
            list.add(jdata);
        });

        try {
//            StringBuilder builder = new StringBuilder();
//            builder.append("[");
//            list.stream().map((obj) -> {
//                builder.append(obj.toString());
//                return obj;
//            }).forEach((_item) -> {
//                builder.append(",");
//            });
//            builder.append("]");

            restClient.setPaths(paths);
            restClient.setHttpMethod(HttpMethod.POST);
            restClient.setData(list.toString());
            restClient.configure().setUrlPath().execute();
        } catch (Exception e) {
            Logger.getLogger(TemperatureSensorBeanController.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void saveDataAsJSON() {
        String fileName = timeUtils.getCurrentTimeWithDate();
        map = hazelcastInstance.getMap(tempHumidMap);

        JSONArray allData = new JSONArray();
        List<JSONObject> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            JSONObject jdata = new JSONObject();
            jdata.put(timeStr, entry.getKey());
            jdata.put(dataStr, new JSONObject(entry.getValue()));
            allData.put(jdata);
            list.add(jdata);
        }
        list.sort((JSONObject o1, JSONObject o2) -> {
            String val1, val2;
            val1 = o1.getString(timeStr);
            val2 = o2.getString(timeStr);
            String value1[], value2[];
            value1 = val1.split("_");
            value2 = val2.split("_");
            if (Integer.parseInt(value1[0]) == Integer.parseInt(value2[0])) {
                if (Integer.parseInt(value1[1]) == Integer.parseInt(value2[1])) {
                    if (Integer.parseInt(value1[2]) == Integer.parseInt(value2[2])) {
                        if (Integer.parseInt(value1[3]) == Integer.parseInt(value2[3])) {
                            if (Integer.parseInt(value1[4]) == Integer.parseInt(value2[4])) {
                                if (Integer.parseInt(value1[5]) == Integer.parseInt(value2[5])) {
                                    return Integer.parseInt(value1[6]) < Integer.parseInt(value2[6]) ? -1 : 1;
                                } else {
                                    return Integer.parseInt(value1[5]) < Integer.parseInt(value2[5]) ? -1 : 1;
                                }
                            } else {
                                return Integer.parseInt(value1[4]) < Integer.parseInt(value2[4]) ? -1 : 1;
                            }
                        } else {
                            return Integer.parseInt(value1[3]) < Integer.parseInt(value2[3]) ? -1 : 1;
                        }
                    } else {
                        return Integer.parseInt(value1[2]) < Integer.parseInt(value2[2]) ? -1 : 1;
                    }
                } else {
                    return Integer.parseInt(value1[1]) < Integer.parseInt(value2[1]) ? -1 : 1;
                }
            } else {
                return Integer.parseInt(value1[0]) < Integer.parseInt(value2[0]) ? -1 : 1;
            }
        });

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".json"));) {

//            writer.append("[");
//            for (JSONObject obj : list) {
//                writer.append(obj.toString());
//                writer.append(",");
//            }
//            writer.append(new JSONObject().toString());
            writer.append(list.toString());
        } catch (IOException ex) {
            Logger.getLogger(TemperatureSensorBeanController.class.getName()).log(Level.SEVERE, null, ex);
        }

        saveDataThroughRESTService();
        map.clear();
        restMap.clear();
    }

    private String executeCommand(List<String> command) {
        String value = "";
        try {
            executor = new ProcessExecutor(command);
            value = executor.startExecution().getInputMsg();

        } catch (IOException ex) {
            Logger.getLogger(TemperatureSensorBeanController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }
}
