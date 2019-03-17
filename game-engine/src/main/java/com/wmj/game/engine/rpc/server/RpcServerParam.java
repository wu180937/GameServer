package com.wmj.game.engine.rpc.server;

import com.wmj.game.common.service.ServiceName;

/**
 * @Author: wumj
 * @Date: 2019/3/16 21:36
 * @Description:
 */
public class RpcServerParam {
    private ServiceName serviceName;
    private String host;
    private int port;

    public RpcServerParam(ServiceName serviceName, String host, int port) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
