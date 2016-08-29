/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.imos.pi.common.RaspberryPiConstant;
import static com.imos.pi.common.RaspberryPiConstant.TIME;
import com.imos.pi.database.TimeTempHumidData;
import com.imos.pi.utils.HazelcastFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Pintu
 */
@Singleton
@Log
public final class HazelcastService {

    private final ObjectMapper MAPPER = new ObjectMapper();
    private final ObjectWriter output;
    private Optional<String> data;
    private final Calendar cal = GregorianCalendar.getInstance();
    private IMap<Long, TimeTempHumidData> map;

    public HazelcastService() {
        output = MAPPER.writer().withDefaultPrettyPrinter();
    }

    @PostConstruct
    public void init() {

        HazelcastInstance hazelcastInstance = HazelcastFactory.getInstance().getHazelcastInstance();
        map = hazelcastInstance.getMap(RaspberryPiConstant.TEMP_HUMID_MAP);

        new Thread(() -> populateHazelcastDB()).start();

        System.out.println(map.size());
    }

    public void saveData(String data) {
        this.data = Optional.of(data);
        if (this.data.isPresent()) {
            try {
                JSONArray array = new JSONArray(this.data.get());
                for (int index = 0, size = array.length(); index < size; index++) {
                    try {
                        JSONObject obj = array.getJSONObject(index);
                        long time = getValue(obj);
                        obj.put(TIME, time);
                        map.put(time, MAPPER.readValue(obj.toString(), TimeTempHumidData.class));
                    } catch (IOException ex) {
                        log.severe(ex.getMessage());
                    }
                }
            } catch (Exception e) {
                log.severe(e.getMessage());
            }
        }
    }

    private long getValue(JSONObject obj) throws Exception {
        if (obj.has(TIME)) {
            try {
                String value = obj.getString(TIME);
                String[] val = value.split("_");
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(val[0]));
                cal.set(Calendar.MONTH, Integer.parseInt(val[1]) - 1);
                cal.set(Calendar.YEAR, Integer.parseInt(val[2]));
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(val[3]));
                cal.set(Calendar.MINUTE, Integer.parseInt(val[4]));
                cal.set(Calendar.SECOND, Integer.parseInt(val[5]));
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTimeInMillis();
            } catch (JSONException | NumberFormatException e) {
                return obj.getLong(TIME);
            }
        } else {
            throw new Exception("Invalid value");
        }
    }

    public Collection<String> getAllData() {
        if (map.isEmpty()) {
            return new ArrayList<>();
        } else {
            System.out.println("loop start");
            
            return map.entrySet().stream()
                    .sorted((t1, t2) -> t1.getKey() < t2.getKey() ? -1 : 1)
                    .map(o -> {
                        try {
                            return new JSONObject(output.writeValueAsString(o.getValue())).toString();
                        } catch (JsonProcessingException | JSONException e) {
                            return new JSONObject().toString();
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    public Collection<String> extractDataForTimeRange(long start, long end) {
        System.out.println("entry");
        if (map.isEmpty()) {
            return new ArrayList<>();
        } else {
            Predicate startPredicate = Predicates.greaterEqual(TIME, start);
            Predicate endPredicate = Predicates.lessEqual(TIME, end);
            Predicate predicate = Predicates.and(startPredicate, endPredicate);
            System.out.println("loop start");
            return map.values(predicate)
                    .stream()
                    .sorted((t1, t2) -> t1.getTime() < t2.getTime() ? -1 : 1)
                    .map(o -> {
                        try {
                            System.out.println(o);
                            return new JSONObject(output.writeValueAsString(o)).toString();
                        } catch (JsonProcessingException | JSONException e) {
                            return new JSONObject().toString();
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    public void localBackupHazelcastDB() {
        Collection<String> db = new ArrayList<>();
        if (!map.isEmpty()) {
            db.addAll(map.entrySet()
                    .stream()
                    .sorted((t1, t2) -> t1.getKey() < t2.getKey() ? -1 : 1)
                    .map(o -> {
                        try {
                            return output.writeValueAsString(o.getValue());
                        } catch (Exception e) {
                            return new JSONObject().toString();
                        }
                    })
                    .collect(Collectors.toList()));
        }
        try {
            Files.write(Paths.get("./Backup/data.json"), new JSONArray(db.toString()).toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
    }

    public void populateHazelcastDB() {
        try {
//            searchDirectoryAndPopulateHazelcastDB(new File("." + File.separator + "Backup"));
//            searchDirectoryAndPopulateHazelcastDB(new File("."));
            System.out.println("upload entry");
            searchDirectoryAndPopulateHazelcastDB(new File("/home/pi/NetBeansProjects/RaspberryPiServer/Backup/data.json"));
            System.out.println("upload end");
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
    }

    private void searchDirectoryAndPopulateHazelcastDB(File file) throws IOException {
        if (file.isDirectory()) {
            for (File tempFile : file.listFiles()) {
                if (tempFile.isDirectory()) {
                    searchDirectoryAndPopulateHazelcastDB(tempFile);
                } else if (tempFile.isFile() && tempFile.getName().endsWith(".json")
                        && (tempFile.getName().matches("\\d{1,2}_\\d{1,2}_\\d{4}_\\d{1,2}_\\d{1,2}_\\d{1,2}\\.json")
                        || tempFile.getName().equals("data.json"))) {
                    uploadFile(com.google.common.io.Files.toString(tempFile, StandardCharsets.UTF_8));
                }
            }
        } else if (file.isFile() && file.getName().endsWith(".json")
                && (file.getName().matches("\\d{1,2}_\\d{1,2}_\\d{4}_\\d{1,2}_\\d{1,2}_\\d{1,2}\\.json")
                || file.getName().equals("data.json"))) {
            uploadFile(com.google.common.io.Files.toString(file, StandardCharsets.UTF_8));
        }
    }

    private void uploadFile(String data) {
        JSONArray array = new JSONArray(data);
        for (int index = 0, size = array.length(); index < size; index++) {
            try {
                JSONObject obj = array.getJSONObject(index);
                long time = getValue(obj);
                obj.put(TIME, time);
                map.put(time, MAPPER.readValue(obj.toString(), TimeTempHumidData.class));
            } catch (Exception e) {
                System.out.println("### : " + array.get(index).toString());
                log.severe(e.getMessage());
            }
        }
    }
}
