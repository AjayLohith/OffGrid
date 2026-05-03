package com.offgrid.OffGrid.service;

import com.offgrid.OffGrid.persistence.PeerEntity;
import com.offgrid.OffGrid.persistence.PeerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PeerRegistryService {

    private final PeerRepository peerRepository;

    public PeerRegistryService(PeerRepository peerRepository) {
        this.peerRepository = peerRepository;
    }

    @Transactional
    public PeerEntity registerPeer(String host, int port, String nick, String room) {
        String id = key(host, port);
        PeerEntity entity = peerRepository.findById(id).orElseGet(() -> PeerEntity.builder()
                .id(id)
                .host(host)
                .tcpPort(port)
                .build());

        entity.setHost(host);
        entity.setTcpPort(port);
        entity.setLastSeen(LocalDateTime.now());
        if (nick != null && !nick.isBlank()) {
            entity.setNick(nick);
        } else if (entity.getNick() == null || entity.getNick().isBlank()) {
            entity.setNick(host);
        }
        if (room != null && !room.isBlank()) {
            entity.setRoom(room);
        }
        return peerRepository.save(entity);
    }

    @Transactional
    public PeerEntity unregisterPeer(String host, int port) {
        String id = key(host, port);
        PeerEntity entity = peerRepository.findById(id).orElseGet(() -> PeerEntity.builder()
                .id(id)
                .host(host)
                .tcpPort(port)
                .build());
        entity.setLastSeen(LocalDateTime.now());
        return peerRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<PeerEntity> getAllPeers() {
        return peerRepository.findAllByOrderByLastSeenDesc();
    }

    @Transactional(readOnly = true)
    public Optional<PeerEntity> findPeer(String host, int port) {
        return peerRepository.findByHostAndTcpPort(host, port);
    }

    private String key(String host, int port) {
        return host + ":" + port;
    }
}
