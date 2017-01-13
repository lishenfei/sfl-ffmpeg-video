package com.sfl.video.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lishenfei on 2017-01-03.
 */
@ConfigurationProperties(prefix = "video")
@Configuration
@Data
public class VideoProperties {

    private String basePath;

    private String extensions;

    private int row;

    private int column;
}
