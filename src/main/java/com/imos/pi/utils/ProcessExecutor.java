/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ejb.Stateless;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Alok Ranjan
 */
@Stateless
public class ProcessExecutor {
    
    @Getter
    private final List<String> command;
    private Process process;

    public ProcessExecutor() {
        command = new ArrayList<>();
    }

    public ProcessExecutor(List<String> command) {
        Objects.requireNonNull(command, "command cannot be null");
        this.command = command;
    }

    public void startExecutionNonBlocking() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        process = pb.start();
    }

    public Process getProcess() {
        if (process == null) {
            throw new IllegalStateException("Process not started yet");
        }
        return process;
    }

    public OutputStream getOutputStream() {
        return process.getOutputStream();
    }

    public Flags startExecution() throws IOException {

        ProcessBuilder pb = new ProcessBuilder(command);
        process = pb.start();
        Flags flags = new Flags();

        try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            // read the output from the command       
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = stdInput.readLine()) != null) {
                builder.append(line);
                builder.append(" ");
                flags.setReadInput(true);
            }
            flags.setInputMsg(builder.toString());

            // read any errors from the attempted command
            while ((line = stdError.readLine()) != null) {
                System.err.println("xxxxx : " + line);
                flags.setErrMsg(flags.getErrMsg() + line + "\n");
                flags.setReadError(true);
            }
        }
        return flags;
    }

    @Setter @Getter
    public static class Flags {

        private boolean readInput;
        private String inputMsg = "";
        private boolean readError;
        private String errMsg = "";
    }

}

