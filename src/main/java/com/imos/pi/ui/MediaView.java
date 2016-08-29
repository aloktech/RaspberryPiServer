/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.ui;

import com.google.common.io.Files;
import com.imos.pi.common.RaspberryPiConstant;
import com.imos.pi.ui.utils.DashboardUtils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Pintu
 */
@Getter
@Setter
@ManagedBean(name = "media")
public class MediaView implements Serializable {

    private UploadedFile file;

    public void upload(FileUploadEvent event) {
        try {
            Files.write(event.getFile().getContents(), new File(RaspberryPiConstant.MUSIC_DIR + event.getFile().getFileName()));
            DashboardUtils.setMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        } catch (IOException ex) {
            DashboardUtils.setMessage("Failure", event.getFile().getFileName() + " is uploaded.");
        }
    }

    public void download() {

    }
}
