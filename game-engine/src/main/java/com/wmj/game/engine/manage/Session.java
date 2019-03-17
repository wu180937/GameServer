package com.wmj.game.engine.manage;

/**
 * @Author: wumj
 * @Date: 2019/3/16 17:17
 * @Description:
 */
public interface Session {
    void send(byte[] data);

    long getSessionId();

    void close();
}
