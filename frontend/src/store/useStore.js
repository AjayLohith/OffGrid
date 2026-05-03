import { create } from 'zustand';

const readStorage = (key, fallback) => {
  if (typeof window === 'undefined') {
    return fallback;
  }
  return window.localStorage.getItem(key) || fallback;
};

const writeStorage = (key, value) => {
  if (typeof window === 'undefined') {
    return;
  }
  window.localStorage.setItem(key, value);
};

const useStore = create((set, get) => ({
  nick: readStorage('offgrid.nick', 'anonymous'),
  room: readStorage('offgrid.room', 'general'),
  messages: [],
  peers: [],
  connected: false,
  sosActive: false,
  setNick: (nick) => {
    writeStorage('offgrid.nick', nick);
    set({ nick });
  },
  setRoom: (room) => {
    writeStorage('offgrid.room', room);
    set({ room });
  },
  addMessage: (message) => {
    set((state) => {
      if (state.messages.some((item) => item.id === message.id)) {
        return state;
      }
      return { messages: [message, ...state.messages].slice(0, 500) };
    });
  },
  setMessages: (messages) => set({ messages: [...messages].slice(0, 500) }),
  setPeers: (peers) => set({ peers: [...peers] }),
  setConnected: (connected) => set({ connected }),
  setSosActive: (sosActive) => set({ sosActive })
}));

export default useStore;
