package com.offgrid.OffGrid.apis;

import com.offgrid.OffGrid.config.MeshPulseProperties;
import com.offgrid.OffGrid.p2p.PeerConnectionManager;
import com.offgrid.OffGrid.service.PeerRegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatusController {

    private final PeerRegistryService peerRegistryService;
    private final MeshPulseProperties properties;
    private final PeerConnectionManager peerConnectionManager;

    public StatusController(PeerRegistryService peerRegistryService,
                            MeshPulseProperties properties,
                            PeerConnectionManager peerConnectionManager) {
        this.peerRegistryService = peerRegistryService;
        this.properties = properties;
        this.peerConnectionManager = peerConnectionManager;
    }

    @GetMapping("/peers")
    public ResponseEntity<?> peers() {
        return ResponseEntity.ok(peerRegistryService.getAllPeers());
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("nick", properties.getNode().getNick());
        response.put("room", properties.getNode().getRoom());
        response.put("tcpPort", properties.getNode().getTcpPort());
        response.put("peerId", peerConnectionManager.getPeerId());
        response.put("listenAddresses", peerConnectionManager.listenAddresses());
        response.put("peerCount", peerRegistryService.getAllPeers().size());
        response.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
        return ResponseEntity.ok(response);
    }
}
