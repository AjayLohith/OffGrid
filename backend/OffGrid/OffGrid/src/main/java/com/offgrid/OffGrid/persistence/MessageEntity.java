package com.offgrid.OffGrid.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
@CompoundIndexes({
        @CompoundIndex(name = "idx_room_timestamp", def = "{'roomName': 1, 'timestamp': -1}")
})
public class MessageEntity {

    @Id
    private String id;

    private String roomName;
    private String senderNick;
    private String content;
    private String type;
    private double latitude;
    private double longitude;
    private long timestamp;
    private LocalDateTime receivedAt;

    public MessageEntity() {
    }

    public MessageEntity(String id, String roomName, String senderNick, String content, String type,
                         double latitude, double longitude, long timestamp, LocalDateTime receivedAt) {
        this.id = id;
        this.roomName = roomName;
        this.senderNick = senderNick;
        this.content = content;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.receivedAt = receivedAt;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public static class Builder {
        private String id;
        private String roomName;
        private String senderNick;
        private String content;
        private String type;
        private double latitude;
        private double longitude;
        private long timestamp;
        private LocalDateTime receivedAt;

        public Builder id(String id) {
            this.id = id;
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

        public Builder type(String type) {
            this.type = type;
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

        public Builder receivedAt(LocalDateTime receivedAt) {
            this.receivedAt = receivedAt;
            return this;
        }

        public MessageEntity build() {
            return new MessageEntity(id, roomName, senderNick, content, type, latitude, longitude, timestamp, receivedAt);
        }
    }
}
