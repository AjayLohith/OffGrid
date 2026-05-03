import { useEffect, useMemo, useRef, useState } from 'react';
import { Send } from 'lucide-react';
import { getHistory } from '../api/meshApi';
import useWebSocket from '../hooks/useWebSocket';
import useStore from '../store/useStore';
import MessageBubble from './MessageBubble';
import PeerList from './PeerList';
import SosButton from './SosButton';
import StatusBar from './StatusBar';

function ChatRoom() {
  const room = useStore((state) => state.room);
  const messages = useStore((state) => state.messages);
  const peers = useStore((state) => state.peers);
  const setMessages = useStore((state) => state.setMessages);
  const addMessage = useStore((state) => state.addMessage);
  const [draft, setDraft] = useState('');
  const bottomRef = useRef(null);

  const { connected, sendMessage } = useWebSocket(room, (message) => {
    addMessage(message);
  });

  useEffect(() => {
    let active = true;
    getHistory(room, 50)
      .then((history) => {
        if (active) {
          setMessages(history);
        }
      })
      .catch((error) => console.error('Failed to load history', error));

    return () => {
      active = false;
    };
  }, [room, setMessages]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }, [messages]);

  const orderedMessages = useMemo(() => [...messages].reverse(), [messages]);

  const submit = async (event) => {
    event.preventDefault();
    const content = draft.trim();
    if (!content) {
      return;
    }

    try {
      const message = await sendMessage(content, room);
      addMessage(message);
      setDraft('');
    } catch (error) {
      console.error('Failed to send message', error);
    }
  };

  return (
    <div className="flex min-h-screen flex-col bg-slate-950 text-slate-100">
      <StatusBar peerCount={peers.length} connected={connected} />

      <main className="mx-auto grid w-full max-w-7xl flex-1 gap-4 px-4 py-4 md:grid-cols-[1fr_280px] md:gap-6">
        <section className="flex min-h-0 flex-col rounded-3xl border border-slate-800 bg-slate-900/70 shadow-2xl shadow-black/20">
          <div className="flex-1 overflow-y-auto px-4 py-4 md:px-6">
            <div className="space-y-3">
              {orderedMessages.map((message) => (
                <MessageBubble key={message.id} message={message} />
              ))}
              <div ref={bottomRef} />
            </div>
          </div>

          <form onSubmit={submit} className="border-t border-slate-800 px-4 py-4 md:px-6">
            <div className="flex flex-col gap-3 lg:flex-row lg:items-center">
              <div className="lg:w-auto">
                <SosButton />
              </div>
              <div className="flex flex-1 gap-3">
                <input
                  value={draft}
                  onChange={(event) => setDraft(event.target.value)}
                  placeholder="Type a message for the room"
                  className="flex-1 rounded-3xl border border-slate-700 bg-slate-950/80 px-4 py-3 text-slate-100 outline-none transition placeholder:text-slate-500 focus:border-red-500"
                />
                <button
                  type="submit"
                  className="inline-flex items-center gap-2 rounded-3xl bg-emerald-500 px-5 py-3 font-semibold text-slate-950 transition hover:bg-emerald-400"
                >
                  <Send size={18} />
                  Send
                </button>
              </div>
            </div>
          </form>
        </section>

        <div className="hidden min-h-0 md:block">
          <PeerList />
        </div>
      </main>
    </div>
  );
}

export default ChatRoom;
