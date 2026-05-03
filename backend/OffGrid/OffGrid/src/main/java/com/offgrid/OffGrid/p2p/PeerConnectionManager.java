package com.offgrid.OffGrid.p2p;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offgrid.OffGrid.config.MeshPulseProperties;
import io.libp2p.core.Host;
import io.libp2p.core.P2PChannel;
import io.libp2p.core.PeerId;
import io.libp2p.core.Stream;
import io.libp2p.core.dsl.HostBuilder;
import io.libp2p.core.multiformats.Multiaddr;
import io.libp2p.core.multistream.ProtocolBinding;
import io.libp2p.core.pubsub.MessageApi;
import io.libp2p.core.pubsub.PubsubApi;
import io.libp2p.core.pubsub.PubsubApiKt;
import io.libp2p.core.pubsub.PubsubPublisherApi;
import io.libp2p.core.pubsub.PubsubSubscription;
import io.libp2p.core.pubsub.Topic;
import io.libp2p.pubsub.gossip.builders.GossipRouterBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PeerConnectionManager {

    private static final Logger log = LoggerFactory.getLogger(PeerConnectionManager.class);

    private final ObjectMapper objectMapper;
    private final MeshPulseProperties properties;
    private final MessageRouter messageRouter;

    private Host host;
    private PubsubApi pubsubApi;
    private PubsubPublisherApi publisher;
    private PubsubSubscription roomSubscription;
    private ProtocolBinding<Void> pubsubBinding;

    public PeerConnectionManager(ObjectMapper objectMapper,
                                 MeshPulseProperties properties,
                                 @Lazy MessageRouter messageRouter) {
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.messageRouter = messageRouter;
    }

    @PostConstruct
    public void start() {
        if (!properties.getP2p().isEnabled()) {
            log.info("libp2p mesh is disabled");
            return;
        }

        var router = new GossipRouterBuilder().build();
        pubsubApi = PubsubApiKt.createPubsubApi(router);

        String protocol = router.getProtocol().getAnnounceStr();
        pubsubBinding = ProtocolBinding.Companion.createSimple(protocol, this::initPubsubStream);

        host = new HostBuilder()
                .protocol(pubsubBinding)
                .listen("/ip4/0.0.0.0/tcp/" + properties.getNode().getTcpPort())
                .build();
        host.start().join();

        publisher = pubsubApi.createPublisher(host.getPrivKey(), System.currentTimeMillis());
        roomSubscription = pubsubApi.subscribe(
                (java.util.function.Consumer<MessageApi>) this::handlePubsubMessage,
                topic(properties.getNode().getRoom())
        );

        connectSeeds();
        log.info("libp2p host {} listening on {}", getPeerId(), host.listenAddresses());
    }

    private CompletableFuture<Void> initPubsubStream(P2PChannel channel) {
        if (!(channel instanceof Stream stream)) {
            return CompletableFuture.failedFuture(new IllegalStateException("pubsub channel is not a stream"));
        }
        ((io.libp2p.pubsub.PubsubRouter) ((io.libp2p.pubsub.PubsubApiImpl) pubsubApi).getRouter()).addPeer(stream);
        return CompletableFuture.completedFuture(null);
    }

    private void handlePubsubMessage(MessageApi messageApi) {
        ByteBuf data = messageApi.getData();
        byte[] bytes = ByteBufUtil.getBytes(data, data.readerIndex(), data.readableBytes(), false);
        try {
            PeerMessage message = objectMapper.readValue(new String(bytes, StandardCharsets.UTF_8), PeerMessage.class);
            messageRouter.receive(message);
        } catch (Exception ex) {
            log.debug("Dropping malformed libp2p pubsub payload: {}", ex.getMessage());
        }
    }

    private void connectSeeds() {
        String seedPeers = properties.getP2p().getSeedPeers();
        if (seedPeers == null || seedPeers.isBlank()) {
            return;
        }
        for (String seed : seedPeers.split(",")) {
            String trimmed = seed.trim();
            if (!trimmed.isBlank()) {
                connectAsync(trimmed);
            }
        }
    }

    public void connectAsync(String hostAddress, int port, String peerId) {
        if (peerId == null || peerId.isBlank() || peerId.equals(getPeerId())) {
            return;
        }
        connectAsync("/ip4/" + hostAddress + "/tcp/" + port + "/p2p/" + peerId);
    }

    public void connectAsync(String multiaddrValue) {
        if (host == null || multiaddrValue == null || multiaddrValue.isBlank()) {
            return;
        }
        try {
            Multiaddr multiaddr = new Multiaddr(multiaddrValue);
            PeerId peerId = multiaddr.getPeerId();
            if (peerId == null || peerId.toBase58().equals(getPeerId())) {
                return;
            }
            pubsubBinding.dial(host, multiaddr).getStream()
                    .thenAccept(stream -> log.info("Connected libp2p peer {}", peerId))
                    .exceptionally(ex -> {
                        log.debug("libp2p dial failed for {}: {}", multiaddrValue, ex.getMessage());
                        return null;
                    });
        } catch (Exception ex) {
            log.warn("Skipping invalid libp2p seed peer '{}'. Use /ip4/<host>/tcp/<port>/p2p/<peerId>", multiaddrValue);
        }
    }

    @Async("meshTaskExecutor")
    public void broadcast(PeerMessage message) {
        if (!properties.getP2p().isEnabled() || publisher == null || message == null) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(message);
            ByteBuf data = Unpooled.copiedBuffer(json, StandardCharsets.UTF_8);
            publisher.publish(data, topic(message.getRoomName()))
                    .exceptionally(ex -> {
                        log.debug("libp2p publish failed for message {}: {}", message.getId(), ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.warn("Failed to publish message {} over libp2p: {}", message.getId(), e.getMessage());
        }
    }

    public String getPeerId() {
        return host == null ? "" : host.getPeerId().toBase58();
    }

    public List<String> listenAddresses() {
        return host == null
                ? List.of()
                : host.listenAddresses().stream().map(Object::toString).toList();
    }

    private Topic topic(String roomName) {
        String room = roomName == null || roomName.isBlank() ? properties.getNode().getRoom() : roomName;
        return new Topic("offgrid-room-" + room);
    }

    @PreDestroy
    public void shutdown() {
        if (roomSubscription != null) {
            roomSubscription.unsubscribe();
        }
        if (host != null) {
            host.stop().join();
        }
    }
}
