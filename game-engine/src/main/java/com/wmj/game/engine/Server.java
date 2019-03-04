package com.wmj.game.engine;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.wmj.game.common.service.ServiceName;

public class Server {
    private ServiceName serviceName;
    private String consulHost;
    private int consulPort;

    private Server(ServiceName serviceName) {
        this.serviceName = serviceName;
    }

    public ServiceName getServiceName() {

        return serviceName;
    }

    private void Register2Consul(String serviceName, int servicePort) {
        Consul consul = Consul.builder().withPing(true).withHostAndPort(HostAndPort.fromParts(consulHost, consulPort)).build();
    }

    public static Server.Builder builder() {
        return new Server.Builder();
    }

    public static class Builder {
        private ServiceName serviceName;
        private String consulHost;
        private int port;

    }
}
