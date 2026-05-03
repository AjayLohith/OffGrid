package com.offgrid.OffGrid.service;

import com.offgrid.OffGrid.p2p.PeerMessage;
import com.offgrid.OffGrid.persistence.MessageEntity;
import com.offgrid.OffGrid.persistence.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private final MessageRepository messageRepository;

    public ChatService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public MessageEntity save(PeerMessage message) {
        MessageEntity entity = MessageEntity.builder()
                .id(message.getId())
                .roomName(message.getRoomName())
                .senderNick(message.getSenderNick())
                .content(message.getContent())
                .type(message.getType().name())
                .latitude(message.getLatitude())
                .longitude(message.getLongitude())
                .timestamp(message.getTimestamp())
                .build();
        return messageRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<MessageEntity> findByRoom(String roomName, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 500));
        return messageRepository.findByRoomNameOrderByTimestampDesc(roomName, PageRequest.of(0, safeLimit));
    }
}
