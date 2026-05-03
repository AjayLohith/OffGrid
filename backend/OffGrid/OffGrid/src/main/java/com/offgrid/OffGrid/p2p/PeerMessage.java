package com.offgrid.OffGrid.p2p;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PeerMessage {

    private String id;
    private MessageType type;
    private String roomName;
    private String senderNick;
    private String content;
    private double latitude;
    private double longitude;
    private long timestamp;

    public PeerMessage() {
    }

    public PeerMessage(String id, MessageType type, String roomName, String senderNick, String content,
                       double latitude, double longitude, long timestamp) {
        this.id = id;
        this.type = type;
        this.roomName = roomName;
        this.senderNick = senderNick;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PeerMessage chat(String nick, String room, String content) {
        return PeerMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(MessageType.CHAT)
                .roomName(room)
                .senderNick(nick)
                .content(content)
                .latitude(0.0d)
                .longitude(0.0d)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static PeerMessage sos(String nick, String room, double lat, double lng) {
        return PeerMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(MessageType.SOS)
                .roomName(room)
                .senderNick(nick)
                .content("SOS ALERT from " + nick + " at " + lat + "," + lng)
                .latitude(lat)
                .longitude(lng)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static PeerMessage ack(String nick, String room) {
        return PeerMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(MessageType.ACK)
                .roomName(room)
                .senderNick(nick)
                .content("ACK")
                .latitude(0.0d)
                .longitude(0.0d)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static PeerMessage ping(String nick, String room) {
        return PeerMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(MessageType.PING)
                .roomName(room)
                .senderNick(nick)
                .content("PING")
                .latitude(0.0d)
                .longitude(0.0d)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSenderNick() {
        return senderNick;
    }

    public void setSenderNick(String senderNick) {
        this.senderNick = senderNick;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class Builder {
        private String id;
        private MessageType type;
        private String roomName;
        private String senderNick;
        private String content;
        private double latitude;
        private double longitude;
        private long timestamp;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(MessageType type) {
            this.type = type;
            return this;
        }

        public Builder roomName(String roomName) {
            this.roomName = roomName;
            return this;
        }

        public Builder senderNick(String senderNick) {
            this.senderNick = senderNick;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public PeerMessage build() {
            return new PeerMessage(id, type, roomName, senderNick, content, latitude, longitude, timestamp);
        }
    }
}
