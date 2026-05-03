import { useEffect, useState } from 'react';
import { getPeers } from '../api/meshApi';
import useStore from '../store/useStore';

function isFresh(lastSeen) {
  if (!lastSeen) {
    return false;
  }
  const seenAt = new Date(lastSeen).getTime();
  return Number.isFinite(seenAt) && Date.now() - seenAt < 30000;
}

function PeerList() {
  const peers = useStore((state) => state.peers);
  const setPeers = useStore((state) => state.setPeers);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let mounted = true;

    const load = async () => {
      try {
        setLoading(true);
        const data = await getPeers();
        if (mounted) {
          setPeers(data);
        }
      } catch (error) {
        console.error('Failed to load peers', error);
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    };

    load();
    const timer = setInterval(load, 5000);

    return () => {
      mounted = false;
      clearInterval(timer);
    };
  }, [setPeers]);

  return (
    <aside className="flex h-full flex-col rounded-3xl border border-slate-800 bg-slate-900/80 p-4">
      <div className="mb-4 flex items-center justify-between">
        <div>
          <h2 className="text-sm font-semibold text-slate-100">Peers</h2>
          <p className="text-xs text-slate-500">Live on the local mesh</p>
        </div>
        <div className="text-xs text-slate-400">{peers.length} total</div>
      </div>

      <div className="space-y-2 overflow-y-auto pr-1">
        {peers.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-slate-700 px-4 py-6 text-sm text-slate-500">
            {loading ? 'Refreshing peers...' : 'No peers discovered yet'}
          </div>
        ) : (
          peers.map((peer) => (
            <div key={peer.id} className="flex items-center justify-between rounded-2xl bg-slate-950/60 px-3 py-3">
              <div>
                <div className="text-sm font-medium text-slate-100">{peer.nick || peer.host}</div>
                <div className="text-xs text-slate-500">{peer.room || 'unknown room'}</div>
              </div>
              <div className="flex items-center gap-2 text-xs text-slate-400">
                <span className={`h-2.5 w-2.5 rounded-full ${isFresh(peer.lastSeen) ? 'bg-emerald-400' : 'bg-slate-600'}`} />
                <span>{isFresh(peer.lastSeen) ? 'recent' : 'stale'}</span>
              </div>
            </div>
          ))
        )}
      </div>
    </aside>
  );
}

export default PeerList;
