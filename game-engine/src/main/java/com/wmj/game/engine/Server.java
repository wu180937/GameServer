package com.wmj.game.engine;

import com.wmj.game.common.service.ServiceName;

public class Server {
    private ServiceName serviceName;

    public Server(ServiceName serviceName) {
        this.serviceName = serviceName;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }
}
