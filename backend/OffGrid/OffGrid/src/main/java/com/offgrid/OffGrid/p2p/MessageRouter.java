package com.offgrid.OffGrid.p2p;

import com.offgrid.OffGrid.service.ChatService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class MessageRouter {

    private static final Logger log = LoggerFactory.getLogger(MessageRouter.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final PeerConnectionManager connectionManager;
    private final ChatService chatService;
    private final LinkedHashSet<String> seenIds = new LinkedHashSet<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final int maxDedupCache;

    public MessageRouter(SimpMessagingTemplate messagingTemplate,
                         PeerConnectionManager connectionManager,
                         ChatService chatService,
                         com.offgrid.OffGrid.config.MeshPulseProperties properties) {
        this.messagingTemplate = messagingTemplate;
        this.connectionManager = connectionManager;
        this.chatService = chatService;
        this.maxDedupCache = properties.getP2p().getMaxDedupCache();
    }

    public void receive(PeerMessage message) {
        if (message == null || message.getId() == null) {
            return;
        }

        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (seenIds.contains(message.getId())) {
                return;
            }
            seenIds.add(message.getId());
            while (seenIds.size() > maxDedupCache) {
                Iterator<String> iterator = seenIds.iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                } else {
                    break;
                }
            }
        } finally {
            writeLock.unlock();
        }

        if (message.getType() == MessageType.PING) {
            connectionManager.broadcast(PeerMessage.ack(message.getSenderNick(), message.getRoomName()));
            return;
        }

        // Ignore system messages that shouldn't be persisted or shown to users
        if (message.getType() == MessageType.ACK || message.getType() == MessageType.PEER_LIST) {
            return;
        }

        chatService.save(message);
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomName(), message);
        connectionManager.broadcast(message);
        log.info("Routed message {} from {} in room {}", message.getId(), message.getSenderNick(), message.getRoomName());
    }
}
