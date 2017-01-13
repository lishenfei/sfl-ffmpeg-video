package com.sfl.video;

import com.sfl.video.service.VideoService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by lishenfei on 2017-01-13.
 */
@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        VideoService videoService = contextRefreshedEvent.getApplicationContext().getBean(VideoService.class);
        videoService.videosScreenshot(null);
    }
}
