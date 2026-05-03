import useStore from '../store/useStore';

function formatTime(timestamp) {
  return new Date(timestamp).toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit'
  });
}

function MessageBubble({ message }) {
  const nick = useStore((state) => state.nick);
  const isOwn = message.senderNick === nick;
  const isSos = message.type === 'SOS';
  const time = formatTime(message.timestamp);

  if (isSos) {
    return (
      <div className="w-full rounded-3xl border border-red-500/60 bg-red-950/90 p-4 text-red-50 shadow-lg shadow-red-950/30">
        <div className="flex items-start justify-between gap-4">
          <div>
            <div className="text-xs uppercase tracking-[0.24em] text-red-200">SOS</div>
            <div className="mt-2 text-sm font-semibold">{message.senderNick}</div>
            <div className="mt-2 text-sm leading-6 text-red-100">{message.content}</div>
            {message.latitude !== 0 || message.longitude !== 0 ? (
              <div className="mt-3 text-xs text-red-200">
                Coordinates: {message.latitude}, {message.longitude}
              </div>
            ) : null}
          </div>
          <div className="text-xs text-red-200">{time}</div>
        </div>
      </div>
    );
  }

  return (
    <div className={`flex ${isOwn ? 'justify-end' : 'justify-start'}`}>
      <div
        className={`max-w-[85%] rounded-3xl px-4 py-3 shadow-lg ${
          isOwn ? 'bg-emerald-500 text-slate-950' : 'bg-slate-800 text-slate-100'
        }`}
      >
        <div className="flex items-center justify-between gap-4 text-xs opacity-80">
          <span className="font-semibold">{message.senderNick}</span>
          <span>{time}</span>
        </div>
        <div className="mt-2 text-sm leading-6">{message.content}</div>
      </div>
    </div>
  );
}

export default MessageBubble;
