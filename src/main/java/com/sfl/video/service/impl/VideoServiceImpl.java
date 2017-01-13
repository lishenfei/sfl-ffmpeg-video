package com.sfl.video.service.impl;

import com.sfl.video.config.VideoProperties;
import com.sfl.video.service.ShellService;
import com.sfl.video.service.VideoService;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Collection;

/**
 * Created by lishenfei on 2017-01-13.
 */
@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    public static final String FFMPEG_CMD = "ffmpeg -i %input -frames 1 -vf \"select=not(mod(n\\,%fps)),scale=%scale,tile=%columnx%row\" %out";
    public static final String SCREENSHOT_IMAGE = "%s.png";
    public static final int VIDEO_FPS = 25;

    @Autowired
    private VideoProperties videoProperties;

    @Autowired
    private ShellService shellService;

    //    @PostConstruct
    public void run() {
        this.videosScreenshot(videoProperties.getBasePath());
    }

    @Override
    public void videosScreenshot(String basePath) {
        if (StringUtils.isEmpty(basePath)) {
            basePath = videoProperties.getBasePath();
        }
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
            return ls / 1000;
        } catch (EncoderException e) {
            log.error("File:{} get duration error. ", videoFile, e);
            return -1L;
        }
    }

    private void videoScreenshot(File srcFile) {
        File videoFile = srcFile;
        if (videoFile.getName().contains(StringUtils.SPACE)) {
            videoFile = new File(srcFile.getParent() + "/" + srcFile.getName().replaceAll(StringUtils.SPACE, "_"));
            srcFile.renameTo(videoFile);
        }
        String screenImageName = String.format(SCREENSHOT_IMAGE, FilenameUtils.getBaseName(videoFile.getName()))
                .replaceAll(StringUtils.SPACE, StringUtils.EMPTY);
        File screenImage = new File(videoFile.getParent() + "/" + screenImageName);

        if (screenImage.exists()) {
            log.info("The screen image:[{}] exists. ", screenImage.getPath());
            return;
        }
        String cmd = this.buildFFmpegCmd(videoFile, screenImage);
        shellService.execute(cmd);
        log.info("Screenshot complete, the video:{} ", videoFile.getPath());
    }

    private String buildFFmpegCmd(File videoFile, File screenImage) {
        long videoTime = this.getDuration(videoFile);
        int row = videoProperties.getRow();
        int column = videoProperties.getColumn();
        int minFPS = 60 * VIDEO_FPS; // 1分钟
        long fps = (videoTime > 0) ? ((videoTime * VIDEO_FPS) / (row * column)) : minFPS;
        if (fps < (minFPS / row * column)) {
            fps = minFPS;
            row = 1;
            column = 1;
        }
        return FFMPEG_CMD.replace("%input", videoFile.getPath())
                .replace("%fps", String.valueOf(fps))
                .replace("%scale", videoProperties.getScale())
                .replace("%column", String.valueOf(column))
                .replace("%row", String.valueOf(row))
                .replace("%out", screenImage.getPath());
    }

}
