import useStore from '../store/useStore';

function StatusBar({ peerCount }) {
  const room = useStore((state) => state.room);
  const nick = useStore((state) => state.nick);
  const connected = useStore((state) => state.connected);

  return (
    <div className="sticky top-0 z-20 border-b border-slate-800 bg-slate-950/95 px-4 py-3 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 text-xs text-slate-300">
        <div className="flex flex-wrap items-center gap-4">
          <div>
            <span className="text-slate-500">Room</span> <span className="font-semibold text-slate-100">{room}</span>
          </div>
          <div>
            <span className="text-slate-500">Nick</span> <span className="font-semibold text-slate-100">{nick}</span>
          </div>
        </div>
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2">
            <span className={`h-2.5 w-2.5 rounded-full ${connected ? 'bg-emerald-400' : 'bg-red-500'}`} />
            <span>{connected ? 'Connected' : 'Disconnected'}</span>
          </div>
          <div>
            <span className="text-slate-500">Peers</span> <span className="font-semibold text-slate-100">{peerCount}</span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default StatusBar;
