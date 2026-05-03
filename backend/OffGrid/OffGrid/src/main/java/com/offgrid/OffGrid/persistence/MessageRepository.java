package com.offgrid.OffGrid.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<MessageEntity, String> {

    List<MessageEntity> findByRoomNameOrderByTimestampDesc(String roomName, Pageable pageable);
}
