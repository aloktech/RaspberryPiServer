/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.alarm;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Alok
 */
@Getter
@Setter
@ManagedBean(name = "alarmAction")
public class AlarmAction {

    private AlarmBean alarmBean;
//    private AlarmBean selectedAlarmBean;
    private UploadedFile file;

    public void uploadFile() {
        if (file != null) {
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            alarmBean.getSong().setSongData(file.getContents());
            alarmBean.getSong().setSongName(file.getFileName());
            alarmBean.getSong().setSongPath(File.separator + "Music" + File.separator + file.getFileName());
        }
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        file = event.getFile();
        if (file != null) {
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            alarmBean.getSong().setSongData(file.getContents());
            alarmBean.getSong().setSongName(file.getFileName());
            alarmBean.getSong().setSongPath(File.separator + "Music" + File.separator + file.getFileName());
        }
    }

    public void selectFile() {

    }

    public void downloadFile() {

    }

    public void addAlarm(ActionEvent actionEvent) {
        uploadFile();
        AlarmRepository.getInstance().addAlarm(alarmBean);
    }

    public void updateAlarm(ActionEvent actionEvent) {
        AlarmRepository.getInstance().updateAlarm(alarmBean);
    }

    public void deleteAlarm(ActionEvent actionEvent) {
        AlarmRepository.getInstance().deleteAlarmByName(alarmBean.getAlarmName());
    }

    public void resetAlarm(ActionEvent actionEvent) {
        alarmBean = null;
    }

    public Set<AlarmBean> getAllAlarms() {
        return AlarmRepository.getInstance().getAllAlarms();
    }
}
