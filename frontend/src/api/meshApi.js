const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/$/, '');

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export function sendMessage(content, roomName) {
  return request('/api/chat/send', {
    method: 'POST',
    body: JSON.stringify({ content, roomName })
  });
}

export function sendSos(latitude, longitude, roomName) {
  return request('/api/sos', {
    method: 'POST',
    body: JSON.stringify({ latitude, longitude, roomName })
  });
}

export function getHistory(roomName, limit = 50) {
  const params = new URLSearchParams({ room: roomName, limit: String(limit) });
  return request(`/api/chat/history?${params.toString()}`);
}

export function getPeers() {
  return request('/api/peers');
}

export function getStatus() {
  return request('/api/status');
}
