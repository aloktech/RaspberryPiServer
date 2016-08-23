/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.ui;

import com.google.common.io.Files;
import com.imos.pi.ui.utils.DashboardUtils;
import com.imos.pi.common.DayLight;
import static com.imos.pi.common.RaspberryPiConstant.CURRENT;
import static com.imos.pi.common.RaspberryPiConstant.TEMP_HUMID_CURRENT;
import com.imos.pi.service.HazelcastService;
import static com.imos.pi.ui.utils.DashboardConstant.*;
import com.imos.pi.utils.HazelcastFactory;
import com.imos.pi.utils.TimeUtils;
import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SlideEndEvent;
import org.primefaces.json.JSONException;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

/**
 *
 * @author Pintu
 */
@Getter
@Setter
@Log
@ManagedBean(name = "graph")
public class TemperatureAndHumidityView implements Serializable {

    private LineChartModel chartModel;

    private Date startDate, endDate, todayDate, date;
    private int timeInterval;
    private String timeIntervalStr;
    private double currTemp, maxTemp, minTemp, avgTemp;
    private double currHumid, maxHumid, minHumid, avgHumid;
    private ConcurrentMap<String, String> current;
    private ConcurrentMap<String, String> map, tempMap;

    @Inject
    private HazelcastService hazelcastService;

    private TimeUtils timeUtils;

    @PostConstruct
    public void init() {
        date = new Date();
        timeUtils = new TimeUtils();
        timeInterval = 12;

        timeIntervalStr = String.format("Time interval : %d", timeInterval);

        chartModel = new LineChartModel();
        chartModel.setTitle(String.format("Temperature and Humidity Daily Chart on %s",
                new SimpleDateFormat("dd-MMM-yy").format(new Date())));
        chartModel.setLegendPosition("ne");
        chartModel.setShowPointLabels(false);
        chartModel.getAxes().put(AxisType.X, new CategoryAxis("Time"));

        chartModel = uploadChartData(chartModel);

        Axis yAxis = chartModel.getAxis(AxisType.Y);
        yAxis.setLabel("%");
        yAxis.setMin(20);
        yAxis.setMax(75);
    }

    private LineChartModel uploadChartData(LineChartModel chartModel) throws JSONException {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);

        String allData = hazelcastService.extractDataForTimeRange(timeUtils.extractTime(cal, DayLight.START),
                timeUtils.extractTime(cal, DayLight.END)).toString();

        JSONArray tempArray = new JSONArray(allData);
        ChartSeries temperatureSeries = new ChartSeries();
        temperatureSeries.setLabel(TEMPERATURE);
        ChartSeries humiditySeries = new ChartSeries();
        humiditySeries.setLabel(HUMIDITY);
        double tempd, humidd;
        int count = 0, valueCounter = 0;
        if (tempArray.length() > 0) {
            for (int index = 0; index < tempArray.length(); index++) {
                JSONObject tempJson = tempArray.getJSONObject(index);
                cal.setTimeInMillis(tempJson.getLong(TIME));
                int hval = cal.get(Calendar.HOUR_OF_DAY);
                int mval = cal.get(Calendar.MINUTE);
                String time = (hval < 10 ? "0" + hval : hval) + "-" + (mval < 10 ? "0" + mval : mval);

                JSONObject data = tempJson.getJSONObject(DATA);
                tempd = data.getDouble(TEMP);
                humidd = data.getDouble(HUMID);

                if (timeInterval > 0 && count % timeInterval == 0) {
                    valueCounter = setTempAndHumidValue(tempd, humidd, temperatureSeries, time, humiditySeries, valueCounter);
                } else if (timeInterval == 0) {
                    valueCounter = setTempAndHumidValue(tempd, humidd, temperatureSeries, time, humiditySeries, valueCounter);
                }
                count++;
            }
            valueCounter = valueCounter == 0 ? count : valueCounter;
            avgTemp = Double.parseDouble(new DecimalFormat(DOUBLE_FORMAT).format(avgTemp / valueCounter));
            avgHumid = Double.parseDouble(new DecimalFormat(DOUBLE_FORMAT).format(avgHumid / valueCounter));

            current = HazelcastFactory.getInstance().getHazelcastInstance().getMap(TEMP_HUMID_CURRENT);
            if (current.get(CURRENT) != null) {
                JSONObject json = new JSONObject(current.get(CURRENT));
                currHumid = json.getDouble(HUMID);
                currTemp = json.getDouble(TEMP);
            }

            Axis xAxis = chartModel.getAxis(AxisType.X);
            xAxis.setTickAngle(90);
        } else {
            temperatureSeries.set(DATA_NOT_AVAILABLE, 50);
            humiditySeries.set(DATA_NOT_AVAILABLE, 60);
            Axis xAxis = chartModel.getAxis(AxisType.X);
            xAxis.setTickAngle(0);

            maxTemp = maxHumid = minTemp = minHumid = avgTemp = avgHumid = 0;
        }

        chartModel.clear();
        chartModel.addSeries(temperatureSeries);
        chartModel.addSeries(humiditySeries);

        return chartModel;
    }

    private int setTempAndHumidValue(double tempd, double humidd, ChartSeries temperature,
            String time, ChartSeries humidity, int valueCounter) {

        calculateMinMax(tempd, humidd);

        temperature.set(time, tempd);
        humidity.set(time, humidd);
        valueCounter++;
        return valueCounter;
    }

    private void calculateMinMax(double tempd, double humidd) {
        if (maxTemp < tempd) {
            maxTemp = tempd;
        }

        if (maxHumid < humidd) {
            maxHumid = humidd;
        }

        if (minTemp == 0) {
            minTemp = tempd;
        }
        if (minTemp > tempd) {
            minTemp = tempd;
        }

        if (minHumid == 0) {
            minHumid = humidd;
        }
        if (minHumid > humidd) {
            minHumid = humidd;
        }

        avgTemp += tempd;
        avgHumid += humidd;
    }

    public void uploadFile(FileUploadEvent event) {
        try {
            System.out.println(event.getFile().getFileName());
            File file = new File("." + File.separator + event.getFile().getFileName());
            Files.write(event.getFile().getContents(), file);
            hazelcastService.saveData(Files.toString(file, StandardCharsets.UTF_8));
            file.delete();
        } catch (Exception ex) {
            log.severe(ex.getMessage());
        }
        DashboardUtils.setMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
    }

    public void uploadChart(ActionEvent event) {
        uploadChartData(chartModel);

        DashboardUtils.setMessage("Successfully", "Chart is populated");
    }

    public void onSlideEnd(SlideEndEvent event) {
        FacesMessage message = new FacesMessage("Slide Ended", "Value: " + event.getValue());
        FacesContext.getCurrentInstance().addMessage(null, message);
        timeIntervalStr = String.format("Time interval : %d", timeInterval);

        uploadChartData(chartModel);
    }
}
