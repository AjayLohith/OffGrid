package com.offgrid.OffGrid.apis;

import com.offgrid.OffGrid.config.MeshPulseProperties;
import com.offgrid.OffGrid.p2p.MessageRouter;
import com.offgrid.OffGrid.p2p.PeerMessage;
import com.offgrid.OffGrid.persistence.MessageEntity;
import com.offgrid.OffGrid.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final MessageRouter messageRouter;
    private final ChatService chatService;
    private final MeshPulseProperties properties;

    public ChatController(MessageRouter messageRouter, ChatService chatService, MeshPulseProperties properties) {
        this.messageRouter = messageRouter;
        this.chatService = chatService;
        this.properties = properties;
    }

    @PostMapping("/send")
    public ResponseEntity<PeerMessage> send(@RequestBody SendChatRequest request) {
        String roomName = request.roomName() == null || request.roomName().isBlank()
                ? properties.getNode().getRoom()
                : request.roomName();
        PeerMessage message = PeerMessage.chat(properties.getNode().getNick(), roomName, request.content());
        messageRouter.receive(message);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/history")
    public ResponseEntity<List<MessageEntity>> history(@RequestParam String room,
                                                       @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatService.findByRoom(room, limit));
    }

    public record SendChatRequest(String content, String roomName) {
    }
}
