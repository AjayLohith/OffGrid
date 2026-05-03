package com.offgrid.OffGrid.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PeerRepository extends MongoRepository<PeerEntity, String> {

    Optional<PeerEntity> findByHostAndTcpPort(String host, int tcpPort);

    List<PeerEntity> findAllByOrderByLastSeenDesc();
}
