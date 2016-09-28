/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imos.pi.ui.utils.DashboardUtils;
import com.imos.pi.database.DatabaseList;
import com.imos.pi.database.TimeTempHumidData;
import static com.imos.pi.ui.utils.DashboardConstant.DATA_NOT_AVAILABLE;
import static com.imos.pi.ui.utils.DashboardConstant.DOUBLE_FORMAT;
import static com.imos.pi.ui.utils.DashboardConstant.HUMIDITY;
import static com.imos.pi.ui.utils.DashboardConstant.TEMPERATURE;
import com.imos.pi.utils.TimeUtils;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
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

    private TimeUtils timeUtils;

    private final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    private DatabaseList databaseList;

    @PostConstruct
    public void init() {
        date = new Date();
        timeUtils = new TimeUtils();
        timeInterval = 12;

        timeIntervalStr = String.format("%d", timeInterval);

        chartModel = new LineChartModel();
        chartModel.setLegendPosition("ne");
        chartModel.setShowPointLabels(false);
        chartModel.getAxes().put(AxisType.X, new CategoryAxis("Time"));

        chartModel = uploadChartData(chartModel);

        Axis yAxis = chartModel.getAxis(AxisType.Y);
        yAxis.setLabel("%");
        yAxis.setMin(20);
        yAxis.setMax(80);
    }

    private LineChartModel uploadChartData(LineChartModel chartModel) throws JSONException {
        maxTemp = maxHumid = minTemp = minHumid = avgTemp = avgHumid = 0;
        LocalTime timeValue = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalTime();
        LocalDate dateValue = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        chartModel.setTitle(String.format("Temperature and Humidity Daily Chart on %s at time %s",
                DateTimeFormatter.ofPattern("dd-MMM-yy").format(dateValue),
                DateTimeFormatter.ofPattern("hh:mm a").format(timeValue)));

        ChartSeries temperatureSeries = new ChartSeries();
        temperatureSeries.setLabel(TEMPERATURE);
        ChartSeries humiditySeries = new ChartSeries();
        humiditySeries.setLabel(HUMIDITY);
        final AtomicInteger valueCounter = new AtomicInteger(0);
        final AtomicInteger count = new AtomicInteger(0);
        Collection<TimeTempHumidData> allData = databaseList.getOneDayData(date.getTime());

        if (allData.isEmpty()) {
            temperatureSeries.set(DATA_NOT_AVAILABLE, 50);
            humiditySeries.set(DATA_NOT_AVAILABLE, 60);
            Axis xAxis = chartModel.getAxis(AxisType.X);
            xAxis.setTickAngle(0);
        } else {
            allData.stream()
                    .forEach(d -> {
                        LocalTime v = Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
                        int hval = v.getHour();
                        int mval = v.getMinute();
                        String time = (hval < 10 ? "0" + hval : hval) + "-" + (mval < 10 ? "0" + mval : mval);
                        double tempd = d.getData().getTemperature();
                        double humidd = d.getData().getHumidity();
                        if (timeInterval > 0 && count.get() % timeInterval == 0) {
                            setTempAndHumidValue(tempd, humidd, temperatureSeries, time, humiditySeries, valueCounter);
                        } else if (timeInterval == 0) {
                            setTempAndHumidValue(tempd, humidd, temperatureSeries, time, humiditySeries, valueCounter);
                        }
                        count.incrementAndGet();
                    });
            valueCounter.set(valueCounter.get() == 0 ? count.get() : valueCounter.get());
            avgTemp = Double.parseDouble(new DecimalFormat(DOUBLE_FORMAT).format(avgTemp / valueCounter.get()));
            avgHumid = Double.parseDouble(new DecimalFormat(DOUBLE_FORMAT).format(avgHumid / valueCounter.get()));

            TimeTempHumidData current = databaseList.getCurrentValue();
            if (current != null) {
                currHumid = current.getData().getHumidity();
                currTemp = current.getData().getTemperature();
            }

            Axis xAxis = chartModel.getAxis(AxisType.X);
            xAxis.setTickAngle(90);
        }

        chartModel.clear();
        chartModel.addSeries(temperatureSeries);
        chartModel.addSeries(humiditySeries);

        return chartModel;
    }

    private void setTempAndHumidValue(double tempd, double humidd, ChartSeries temperature,
            String time, ChartSeries humidity, AtomicInteger valueCounter) {

        calculateMinMax(tempd, humidd);

        temperature.set(time, tempd);
        humidity.set(time, humidd);
        valueCounter.incrementAndGet();
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
//        try {
//            File file = new File("." + File.separator + event.getFile().getFileName());
//            Files.write(event.getFile().getContents(), file);
//            hazelcastService.saveData(Files.toString(file, StandardCharsets.UTF_8));
//            file.delete();
//        } catch (Exception ex) {
//            log.severe(ex.getMessage());
//        }
//        DashboardUtils.setMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
    }

    public void uploadChart(ActionEvent event) {
        uploadChartData(chartModel);

        DashboardUtils.setMessage("Successfully", "Chart is populated");
    }

    public void onSlideEnd(SlideEndEvent event) {
        FacesMessage message = new FacesMessage("Slide Ended", "Value: " + event.getValue());
        FacesContext.getCurrentInstance().addMessage(null, message);
        timeIntervalStr = String.format("Time interval : %d", timeInterval);

//        uploadChartData(chartModel);
    }
}
