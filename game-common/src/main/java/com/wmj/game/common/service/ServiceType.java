package com.wmj.game.common.service;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/5
 * @Description:
 */
public enum ServiceType {
    Tcp("tcp"),
    Udp("udp"),
    WebSocket("ws"),
    Rpc("rpc");
    String type;

    ServiceType(String type) {
        this.type = type;
    }

    public String generateServiceName(ServiceName serviceName) {
        return "(-" + type + "-)" + serviceName.getName();
    }
}
