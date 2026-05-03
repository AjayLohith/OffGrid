import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { sendMessage as sendMessageRequest } from '../api/meshApi';
import useStore from '../store/useStore';

export default function useWebSocket(roomName, onMessage) {
  const [connected, setConnected] = useState(false);
  const clientRef = useRef(null);
  const onMessageRef = useRef(onMessage);
  const setStoreConnected = useStore((state) => state.setConnected);

  useEffect(() => {
    onMessageRef.current = onMessage;
  }, [onMessage]);

  useEffect(() => {
    const apiBaseUrl = (
      import.meta.env.VITE_WS_BASE_URL ||
      import.meta.env.VITE_API_BASE_URL ||
      'http://localhost:8080'
    ).replace(/\/$/, '');
    const client = new Client({
      reconnectDelay: 5000,
      webSocketFactory: () => new SockJS(`${apiBaseUrl}/ws`),
      debug: () => {}
    });

    client.onConnect = () => {
      setConnected(true);
      setStoreConnected(true);
      client.subscribe(`/topic/room/${roomName}`, (frame) => {
        try {
          const payload = JSON.parse(frame.body);
          onMessageRef.current?.(payload);
        } catch (error) {
          console.error('Failed to parse message frame', error);
        }
      });
    };

    client.onWebSocketClose = () => {
      setConnected(false);
      setStoreConnected(false);
    };

    client.onStompError = () => {
      setConnected(false);
      setStoreConnected(false);
    };

    client.activate();
    clientRef.current = client;

    return () => {
      setConnected(false);
      setStoreConnected(false);
      client.deactivate();
      clientRef.current = null;
    };
  }, [roomName, setStoreConnected]);

  const sendMessage = async (content, targetRoom = roomName) => sendMessageRequest(content, targetRoom);

  const disconnect = async () => {
    if (clientRef.current) {
      await clientRef.current.deactivate();
    }
    setConnected(false);
    setStoreConnected(false);
  };

  return { connected, sendMessage, disconnect };
}
