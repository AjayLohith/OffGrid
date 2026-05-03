import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useStore from '../store/useStore';

function JoinForm() {
  const navigate = useNavigate();
  const storedNick = useStore((state) => state.nick);
  const storedRoom = useStore((state) => state.room);
  const setNick = useStore((state) => state.setNick);
  const setRoom = useStore((state) => state.setRoom);
  const [nick, setNickInput] = useState(storedNick === 'anonymous' ? '' : storedNick);
  const [room, setRoomInput] = useState(storedRoom || 'general');

  const submit = (event) => {
    event.preventDefault();
    const trimmedNick = nick.trim();
    const trimmedRoom = room.trim() || 'general';
    if (!trimmedNick) {
      return;
    }
    setNick(trimmedNick);
    setRoom(trimmedRoom);
    navigate('/chat');
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-4 py-10">
      <form
        onSubmit={submit}
        className="w-full max-w-md rounded-3xl border border-slate-800 bg-slate-900/90 p-8 shadow-2xl shadow-black/30 backdrop-blur"
      >
        <div className="mb-8">
          <p className="text-sm uppercase tracking-[0.3em] text-red-300">OffGrid</p>
          <h1 className="mt-3 text-3xl font-semibold text-slate-50">Join the local mesh</h1>
          <p className="mt-2 text-sm leading-6 text-slate-400">
            Pick a display name and room. No internet is required once the nodes are on the same LAN.
          </p>
        </div>

        <label className="mb-5 block">
          <span className="mb-2 block text-sm font-medium text-slate-300">Your name</span>
          <input
            value={nick}
            onChange={(event) => setNickInput(event.target.value)}
            placeholder="e.g. Maya"
            className="w-full rounded-2xl border border-slate-700 bg-slate-950/80 px-4 py-3 text-slate-100 outline-none transition placeholder:text-slate-500 focus:border-red-500"
          />
        </label>

        <label className="mb-6 block">
          <span className="mb-2 block text-sm font-medium text-slate-300">Room name</span>
          <input
            value={room}
            onChange={(event) => setRoomInput(event.target.value)}
            placeholder="general"
            className="w-full rounded-2xl border border-slate-700 bg-slate-950/80 px-4 py-3 text-slate-100 outline-none transition placeholder:text-slate-500 focus:border-red-500"
          />
        </label>

        <button
          type="submit"
          className="w-full rounded-2xl bg-red-600 px-4 py-3 font-semibold text-white transition hover:bg-red-500"
        >
          Enter room
        </button>
      </form>
    </div>
  );
}

export default JoinForm;
