package com.offgrid.OffGrid.p2p;

import com.offgrid.OffGrid.config.MeshPulseProperties;
import com.offgrid.OffGrid.service.PeerRegistryService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

@Service
public class MdnsDiscoveryService implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(MdnsDiscoveryService.class);

    private final MeshPulseProperties properties;
    private final PeerConnectionManager peerConnectionManager;
    private final PeerRegistryService peerRegistryService;
    private JmDNS jmdns;
    private InetAddress localAddress;

    public MdnsDiscoveryService(MeshPulseProperties properties,
                                PeerConnectionManager peerConnectionManager,
                                PeerRegistryService peerRegistryService) {
        this.properties = properties;
        this.peerConnectionManager = peerConnectionManager;
        this.peerRegistryService = peerRegistryService;
    }

    @PostConstruct
    public void init() throws IOException {
        if (!properties.getP2p().isEnabled() || !properties.getMdns().isEnabled()) {
            log.info("mDNS discovery is disabled");
            return;
        }

        localAddress = findLocalAddress();
        jmdns = JmDNS.create(localAddress);
        String serviceName = properties.getMdns().getDiscoveryTag() + "-" + properties.getNode().getNick();
        Map<String, String> text = new Hashtable<>();
        text.put("room", properties.getNode().getRoom());
        text.put("peerId", peerConnectionManager.getPeerId());
        ServiceInfo serviceInfo = ServiceInfo.create(
                properties.getMdns().getServiceType(),
                serviceName,
                properties.getNode().getTcpPort(),
                0,
                0,
                text
        );
        jmdns.registerService(serviceInfo);
        jmdns.addServiceListener(properties.getMdns().getServiceType(), new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent event) {
                handle(event);
            }

            @Override
            public void serviceRemoved(ServiceEvent event) {
                // no-op
            }

            @Override
            public void serviceResolved(ServiceEvent event) {
                handle(event);
            }
        });
        log.info("mDNS discovery started on {}", localAddress.getHostAddress());
    }

    private void handle(ServiceEvent event) {
        ServiceInfo serviceInfo = event.getInfo();
        if (serviceInfo == null) {
            serviceInfo = jmdns.getServiceInfo(properties.getMdns().getServiceType(), event.getName());
        }
        if (serviceInfo == null) {
            return;
        }

        InetAddress[] addresses = serviceInfo.getInetAddresses();
        if (addresses == null || addresses.length == 0) {
            return;
        }

        InetAddress address = addresses[0];
        if (address.equals(localAddress)) {
            return;
        }

        String tag = properties.getMdns().getDiscoveryTag() + "-";
        String nick = serviceInfo.getName();
        if (nick == null || !nick.startsWith(tag)) {
            return;
        }
        if (nick != null && nick.startsWith(tag)) {
            nick = nick.substring(tag.length());
        }
        String room = serviceInfo.getPropertyString("room");
        String peerId = serviceInfo.getPropertyString("peerId");
        peerRegistryService.registerPeer(address.getHostAddress(), serviceInfo.getPort(), nick, room);
        peerConnectionManager.connectAsync(address.getHostAddress(), serviceInfo.getPort(), peerId);
    }

    private InetAddress findLocalAddress() throws IOException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        if (interfaces == null) {
            try {
                return InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new IOException("Unable to resolve local host address", e);
            }
        }
        for (NetworkInterface networkInterface : Collections.list(interfaces)) {
            if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                continue;
            }
            for (InetAddress address : Collections.list(networkInterface.getInetAddresses())) {
                if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                    return address;
                }
            }
        }
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new IOException("Unable to resolve local host address", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (jmdns != null) {
            try {
                jmdns.unregisterAllServices();
                jmdns.close();
            } catch (IOException e) {
                log.warn("Failed to close JmDNS: {}", e.getMessage());
            }
        }
    }
}
