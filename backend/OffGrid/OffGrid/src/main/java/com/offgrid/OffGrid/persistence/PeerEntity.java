package com.offgrid.OffGrid.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "peers")
public class PeerEntity {

    @Id
    private String id;

    private String host;
    private int tcpPort;
    private LocalDateTime lastSeen;
    private String nick;
    private String room;

    public PeerEntity() {
    }

    public PeerEntity(String id, String host, int tcpPort, LocalDateTime lastSeen, String nick, String room) {
        this.id = id;
        this.host = host;
        this.tcpPort = tcpPort;
        this.lastSeen = lastSeen;
        this.nick = nick;
        this.room = room;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
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

    public static class Builder {
        private String id;
        private String host;
        private int tcpPort;
        private LocalDateTime lastSeen;
        private String nick;
        private String room;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder tcpPort(int tcpPort) {
            this.tcpPort = tcpPort;
            return this;
        }

        public Builder lastSeen(LocalDateTime lastSeen) {
            this.lastSeen = lastSeen;
            return this;
        }

        public Builder nick(String nick) {
            this.nick = nick;
            return this;
        }

        public Builder room(String room) {
            this.room = room;
            return this;
        }

        public PeerEntity build() {
            return new PeerEntity(id, host, tcpPort, lastSeen, nick, room);
        }
    }
}
