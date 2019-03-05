package com.wmj.game.engine;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.wmj.game.common.service.ServiceName;
import org.apache.commons.lang3.StringUtils;

public class Server {
    private ServiceName serviceName;
    private String consulHost;
    private int consulPort;

    public Server(ServiceName serviceName, String consulHost, int consulPort) {
        this.serviceName = serviceName;
        this.consulHost = consulHost;
        this.consulPort = consulPort;
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
        private int consulPort;

        public Builder setServiceName(ServiceName serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder setConsulHost(String consulHost) {
            this.consulHost = consulHost;
            return this;
        }

        public Builder setConsulPort(int consulPort) {
            this.consulPort = consulPort;
            return this;
        }

        public Server build() {
            if (serviceName == null) {
                throw new IllegalArgumentException("serviceName cannot null.");
            }
            if (StringUtils.isEmpty(consulHost)) {
                throw new IllegalArgumentException("consulHost cannot null or empty.");
            }
            if (consulPort < 1) {
                throw new IllegalArgumentException("consulPort is require.");
            }
            return new Server(serviceName, consulHost, consulPort);
        }
    }
}
