package com.sfl.video.service.impl;

import com.sfl.video.service.ShellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ShellServiceImpl implements ShellService {

    public boolean execute(String command) {
        log.info("Execute command: [{}]", command);
        String result = null;
        try {
            Process process = Runtime.getRuntime().exec(command);
            if (process.waitFor() != 0) {
                log.error("The process no is no exist, the command: {}", command);
                return false;
            }
            result = IOUtils.toString(process.getInputStream());
        } catch (Exception e) {
            log.error("Execute the shell failed, ", e);
            return false;
        } finally {
            if (!StringUtils.isEmpty(result)) {
                log.info("Execute command result: {}", result);
            }
        }
        return true;
    }

}
