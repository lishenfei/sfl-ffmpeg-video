package com.sfl.video.service;

import it.sauronsoftware.jave.EncoderException;

import java.io.File;

/**
 * Created by lishenfei on 2017-01-13.
 */
public interface VideoService {

    /**
     * 对 basePath 目录下的所有视频文件截图（多张截图合并到一个文件）
     *
     * @param basePath
     */
    void videosScreenshot(String basePath);

    /**
     * 获取视频文件播放时长
     *
     * @param videoFile
     * @return 视频播放时长，单位为秒
     * @throws EncoderException
     */
    long getDuration(File videoFile);
}
