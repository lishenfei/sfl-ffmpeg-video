package com.sfl.video.service;

public interface ShellService {

    /**
     * @param command 命令行
     * @return boolean 执行结果
     */
    boolean execute(String command);
}
