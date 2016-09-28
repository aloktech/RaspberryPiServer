/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.md;

import static com.imos.pi.common.RaspberryPiConstant.UNDER_SCORE;
import com.imos.pi.service.SMTPMailService;
import com.imos.pi.utils.TimeUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alok
 */
public class ScheduledTask extends TimerTask {

    public boolean recordingStarted, recordingEnded, canSendMail, createMp4;
    private final String mp4boxCommand = "MP4Box", mp4SplitCommand = "MP4Box -splits 22000 ",
            endWithVideo = ".h264";

    private final TimeUtils timeUtils;

    private final SMTPMailService mailService;

    public ScheduledTask() {
        timeUtils = new TimeUtils();
        mailService = new SMTPMailService();
    }

    @Override
    public void run() {
        try {
            creatMp4File();
            splitMp4File("./");
            mailService.sendMailWithAttachment("./");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(MotionSensorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void creatMp4File() throws IOException, InterruptedException {
        File files = new File("./");
        String mainCmd = mp4boxCommand, cmd;
        cmd = mainCmd;
        int counter = 0;
        List<String> names = new ArrayList<>();
        for (File file : files.listFiles()) {
            if (file.getName().endsWith("h264")) {
                names.add(file.getName());
            }
        }
        Collections.sort(names, (String o1, String o2) -> {
            int h1, h2, m1, m2, s1, s2;
            String[] data1 = o1.substring(0, o1.lastIndexOf(endWithVideo)).split(UNDER_SCORE),
                    data2 = o2.substring(0, o2.lastIndexOf(endWithVideo)).split(UNDER_SCORE);
            h1 = Integer.parseInt(data1[0]);
            h2 = Integer.parseInt(data2[0]);
            m1 = Integer.parseInt(data1[1]);
            m2 = Integer.parseInt(data2[1]);
            s1 = Integer.parseInt(data1[2]);
            s2 = Integer.parseInt(data2[2]);
            if (h1 == h2) {
                return (m1 == m2) ? (s1 - s2) : (m1 - m2);
            } else {
                return h1 - h2;
            }
        });
        int allItem = names.size();
        for (String name : names) {
            if (counter < 6) {
                cmd += " -cat " + name;
                counter++;
            } else {
                String tempFileName = createFileName();
                tempFileName = tempFileName.substring(0, tempFileName.lastIndexOf(endWithVideo));
                tempFileName = "new_" + tempFileName + ".mp4";
                cmd += " -new " + tempFileName;
                Process p = Runtime.getRuntime().exec(cmd);
                p.waitFor();
                counter = 0;
                cmd = mainCmd;
            }
        }
        if (counter <= allItem) {
            String tempFileName = createFileName();
            tempFileName = tempFileName.substring(0, tempFileName.lastIndexOf(endWithVideo));
            tempFileName = "new_" + tempFileName + ".mp4";
            cmd += " -new " + tempFileName;
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
        }
    }

    private void splitMp4File(String fileName) throws IOException, InterruptedException {
        File files = new File("./" + fileName);
        String mainCmd = mp4SplitCommand, cmd;
        for (File file : files.listFiles()) {
            if (file.getName().endsWith("mp4")) {
                double bytes = file.length();
                double kilobytes = (bytes / 1024);
                double megabytes = (kilobytes / 1024);
                if (megabytes > 22) {
                    cmd = mainCmd + file.getName();
                    Process p = Runtime.getRuntime().exec(cmd);
                    p.waitFor();
                }
            }
        }
    }

    private String createFileName() {
        StringBuilder fileName = new StringBuilder();
        fileName.append(timeUtils.getCurrentTime());
        fileName.append(endWithVideo);

        return fileName.toString();
    }

}
