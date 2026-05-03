package com.offgrid.OffGrid.apis;

import com.offgrid.OffGrid.config.MeshPulseProperties;
import com.offgrid.OffGrid.p2p.MessageRouter;
import com.offgrid.OffGrid.p2p.PeerMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sos")
public class SosController {

    private final MessageRouter messageRouter;
    private final MeshPulseProperties properties;

    public SosController(MessageRouter messageRouter, MeshPulseProperties properties) {
        this.messageRouter = messageRouter;
        this.properties = properties;
    }

    @PostMapping
    public ResponseEntity<PeerMessage> send(@RequestBody SosRequest request) {
        String roomName = request.roomName() == null || request.roomName().isBlank()
                ? properties.getNode().getRoom()
                : request.roomName();
        PeerMessage message = PeerMessage.sos(properties.getNode().getNick(), roomName, request.latitude(), request.longitude());
        messageRouter.receive(message);
        return ResponseEntity.ok(message);
    }

    public record SosRequest(double latitude, double longitude, String roomName) {
    }
}
