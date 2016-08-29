/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.service;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Alok
 */
@Singleton
public class ElasticSearchService {

    private final String connectionUrl = "http://10.0.0.10:9200";
    private JestClient client;

    @PostConstruct
    public void init() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(connectionUrl)
                .multiThreaded(true)
                .build());
        client = factory.getObject();
    }

    public String createIndex(String data) {

        try {
            JSONObject jData = new JSONObject(data);

            String index = jData.getString("index");

            client.execute(new CreateIndex.Builder(index).build());
            JSONObject status = new JSONObject();
            status.put("status", "OK");
            status.put("index", index);
        } catch (IOException ex) {
            Logger.getLogger(ElasticSearchService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String uploadData(String data, String index, String type) {

        try {

            JSONObject status = new JSONObject();
            boolean indexExists = client.execute(new IndicesExists.Builder(index).build()).isSucceeded();
            if (!indexExists) {
                client.execute(new CreateIndex.Builder(index).build());
            }
            Builder bulkIndexBuilder = new Bulk.Builder();
            Calendar cal = GregorianCalendar.getInstance();
            JSONArray array = new JSONArray(data);
            for (int ind = 0; ind < array.length(); ind++) {
                JSONObject tempJson = array.getJSONObject(ind);
                if (tempJson.has("time")) {
                    String[] time = tempJson.getString("time").split("_");
                    cal.set(Calendar.YEAR, Integer.parseInt(time[2]));
                    cal.set(Calendar.MONTH, Integer.parseInt(time[1]) - 1);
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(time[0]));
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[3]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(time[4]));
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    tempJson.put("timeInMillis", cal.getTimeInMillis());
                    tempJson.put("date", Integer.parseInt(time[2]) + "-" + Integer.parseInt(time[1]) + "-" + Integer.parseInt(time[0]));
                    if (time.length > 3) {
                        tempJson.put("time", Integer.parseInt(time[3]) + "-" + Integer.parseInt(time[4]));
                    }

                    bulkIndexBuilder.addAction(new Index.Builder(tempJson.toString()).index(index).type(type).id(String.valueOf(cal.getTimeInMillis())).build());
                }
            }
            client.execute(bulkIndexBuilder.build());
            status.put("status", "OK");
            status.put("index", index);
            status.put("type", type);
            status.put("count", array.length());
            status.put("time", new Date());
        } catch (IOException ex) {
            Logger.getLogger(ElasticSearchService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String extractData(String inputData) {
        try {
            String index = "temp-humid-db", type = "temp-humid";
            long startTime, endTime;
            JSONObject data = new JSONObject(inputData);
            startTime = data.getLong("startTime");
            endTime = data.getLong("endTime");
            String query;
            query = "{\n"
                    + " \"size\": 10000, \n"
                    + " \"query\" : {\n"
                    + " \"filtered\" : {\n"
                    + " \"filter\" : {\n"
                    + " \"range\" : {\n"
                    + " \"timeInMillis\" : {\n"
                    + " \"gte\" : "
                    + startTime
                    + ",\n"
                    + " \"lt\" : "
                    + endTime
                    + "\n"
                    + " }\n"
                    + " }\n"
                    + " }\n"
                    + " }\n"
                    + " },\n"
                    + " \"sort\": { \"timeInMillis\": { \"order\": \"asc\" }}\n"
                    + "}\n"
                    + "";
            Search search = new Search.Builder(query)
                    .addIndex(index)
                    .addType(type)
                    .build();
            JestResult result  = client.execute(search);
            JSONObject json = new JSONObject(result.getJsonString());
            if (json.has("hits")) {
                return json.getJSONObject("hits").getJSONArray("hits").toString();
            }
        } catch (IOException ex) {
            Logger.getLogger(ElasticSearchService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new JSONObject().toString();
    }
}
