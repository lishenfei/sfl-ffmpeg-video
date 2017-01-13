package com.sfl.video.service.impl;

import com.sfl.video.service.ShellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
@Slf4j
public class ShellServiceImpl implements ShellService {

    public boolean execute(String command) {
        log.info("Execute command: [{}]", command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            readStream(process.getErrorStream());
            readStream(process.getInputStream());
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            log.error("Execute the command:[{}] failed, ", command, e);
            return false;
        }
    }

    @Async
    public void readStream(InputStream inputStream) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                log.info("{}", line);
            }
        } catch (IOException ioe) {
            log.error("Read stream error. ", ioe);
        } finally {
            IOUtils.closeQuietly(br);
        }
    }
}
