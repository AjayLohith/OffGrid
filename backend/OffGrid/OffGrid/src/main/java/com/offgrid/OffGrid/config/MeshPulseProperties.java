package com.offgrid.OffGrid.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "meshpulse")
public class MeshPulseProperties {

    private final Node node = new Node();
    private final Mdns mdns = new Mdns();
    private final P2p p2p = new P2p();
    private final Cors cors = new Cors();

    public Node getNode() {
        return node;
    }

    public Mdns getMdns() {
        return mdns;
    }

    public P2p getP2p() {
        return p2p;
    }

    public Cors getCors() {
        return cors;
    }

    public static class Node {
        private int port = 8080;
        private int tcpPort = 9090;
        private String nick = "anonymous";
        private String room = "general";

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getTcpPort() {
            return tcpPort;
        }

        public void setTcpPort(int tcpPort) {
            this.tcpPort = tcpPort;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }
    }

    public static class Mdns {
        private boolean enabled = true;
        private String serviceType = "_meshpulse._tcp.local.";
        private String discoveryTag = "meshpulse-default";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public String getDiscoveryTag() {
            return discoveryTag;
        }

        public void setDiscoveryTag(String discoveryTag) {
            this.discoveryTag = discoveryTag;
        }
    }

    public static class P2p {
        private boolean enabled = true;
        private String seedPeers = "";
        private long reconnectDelayMs = 5000L;
        private int maxDedupCache = 2000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSeedPeers() {
            return seedPeers;
        }

        public void setSeedPeers(String seedPeers) {
            this.seedPeers = seedPeers;
        }

        public long getReconnectDelayMs() {
            return reconnectDelayMs;
        }

        public void setReconnectDelayMs(long reconnectDelayMs) {
            this.reconnectDelayMs = reconnectDelayMs;
        }

        public int getMaxDedupCache() {
            return maxDedupCache;
        }

        public void setMaxDedupCache(int maxDedupCache) {
            this.maxDedupCache = maxDedupCache;
        }
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:5173,http://localhost:3000";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }
}
