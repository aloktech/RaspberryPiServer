/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.md;

import com.imos.pi.utils.ProcessExecutor;
import static com.imos.pi.utils.RaspberryPiConstant.UNDER_SCORE;
import com.imos.pi.utils.SMTPMailService;
import com.imos.pi.utils.TimeUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 *
 * @author Alok
 */
public class ScheduledTask extends TimerTask {

    private ProcessExecutor executor;
    private final int detectionCount = 0;
    public boolean recordingStarted, recordingEnded, canSendMail, createMp4;
    private final String mp4boxCommand = "MP4Box",
            mp4SplitCommand = "MP4Box -splits 22000 ", endWithVideo = ".h264";
    private final AtomicBoolean sendMail = new AtomicBoolean(false);
    private final AtomicLong timeDelay = new AtomicLong();

    @Inject
    private TimeUtils timeUtils;

    private final SMTPMailService mailService;

    public ScheduledTask() {
//        timeUtils = new TimeUtils();

        mailService = new SMTPMailService();
    }

    @Override
    public void run() {

        try {
            creatMp4File();
            splitMp4File("./");
            mailService.sendMailWithAttachment(String.valueOf(detectionCount), "./", "timeStamp.txt");
            sendMail.set(true);
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
                if (m1 == m2) {
                    return s1 < s2 ? - 1 : 1;
                } else {
                    return m1 < m2 ? - 1 : 1;
                }
            } else {
                return h1 < h2 ? - 1 : 1;
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

    public String detectorTheSensor(List<String> command) {
        return executeCommand(command);
    }

    private String createFileName() {
        StringBuilder fileName = new StringBuilder();
        fileName.append(timeUtils.getCurrentTime());
        fileName.append(endWithVideo);

        return fileName.toString();
    }

    private String executeCommand(List<String> command) {
        String value = "";
        try {
            executor = new ProcessExecutor(command);
            value = executor.startExecution().getInputMsg();

        } catch (IOException ex) {
            Logger.getLogger(ScheduledTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

}
