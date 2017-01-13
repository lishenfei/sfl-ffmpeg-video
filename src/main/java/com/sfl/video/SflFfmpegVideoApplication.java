package com.sfl.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SflFfmpegVideoApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(SflFfmpegVideoApplication.class);
        springApplication.addListeners(new ApplicationStartupListener());
        springApplication.run(args);
    }
}
