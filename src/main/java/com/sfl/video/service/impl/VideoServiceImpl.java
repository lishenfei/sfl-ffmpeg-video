package com.sfl.video.service.impl;

import com.sfl.video.config.VideoProperties;
import com.sfl.video.service.ShellService;
import com.sfl.video.service.VideoService;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collection;

/**
 * Created by lishenfei on 2017-01-13.
 */
@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    public static final String FFMPEG_CMD = "ffmpeg -i %input -frames 1 -vf \"select=not(mod(n\\,%fps)),scale=375:250,tile=%columnx%row\" %out";
    public static final String SCREENSHOT_IMAGE = "%s.png";

    @Autowired
    private VideoProperties videoProperties;

    @Autowired
    private ShellService shellService;

    @PostConstruct
    public void run() {
        this.videosScreenshot(videoProperties.getBasePath());
    }

    @Override
    public void videosScreenshot(String basePath) {
        log.info("Videos screenshot basePath: {}", basePath);

        File baseFile = new File(basePath);
        if (!baseFile.exists()) {
            return;
        }
        if (baseFile.isDirectory()) {
            Collection<File> fileCollection = FileUtils.listFiles(baseFile, videoProperties.getExtensions().split(","), true);
            if (CollectionUtils.isEmpty(fileCollection)) {
                return;
            }
            fileCollection.forEach(this::videoScreenshot);
        } else if (baseFile.isFile()
                && FilenameUtils.isExtension(baseFile.getName(), videoProperties.getExtensions().split(","))) {
            this.videoScreenshot(baseFile);
        }
    }

    @Override
    public long getDuration(File videoFile) {
        try {
            long ls = new Encoder().getInfo(videoFile).getDuration();
            return ls / 60000;
        } catch (EncoderException e) {
            log.error("file:{} get duration error. ", videoFile, e);
            return -1L;
        }
    }

    private void videoScreenshot(File videoFile) {
        String screenImage = String.format(SCREENSHOT_IMAGE, FilenameUtils.getBaseName(videoFile.getName()));
        if (new File(screenImage).exists()) {
            return;
        }
        String cmd = this.buildFFmpegCmd(videoFile, screenImage);
        shellService.execute(cmd);
        log.info("Screenshot the video:{} ", videoFile.getPath());
    }

    private String buildFFmpegCmd(File videoFile, String screenImage) {
        long videoTime = this.getDuration(videoFile);
        long fps = 1500; // 默认1分钟
        if (videoTime > 0) {
            fps = (videoTime * 60 * 25) / (videoProperties.getRow() * videoProperties.getColumn());
        }
        return FFMPEG_CMD.replace("%input", videoFile.getPath())
                .replace("%fps", String.valueOf(fps))
                .replace("%column", String.valueOf(videoProperties.getColumn()))
                .replace("%row", String.valueOf(videoProperties.getRow()))
                .replace("%out", videoFile.getParent() + "/" + screenImage);
    }

}
