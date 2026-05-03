import { useEffect, useState } from 'react';
import { sendSos } from '../api/meshApi';
import useStore from '../store/useStore';

function SosButton() {
  const room = useStore((state) => state.room);
  const sosActive = useStore((state) => state.sosActive);
  const setSosActive = useStore((state) => state.setSosActive);
  const [countdown, setCountdown] = useState(0);
  const [notice, setNotice] = useState('');

  useEffect(() => {
    if (countdown <= 0) {
      setSosActive(false);
      return undefined;
    }

    const timer = window.setInterval(() => {
      setCountdown((value) => {
        if (value <= 1) {
          window.clearInterval(timer);
          setSosActive(false);
          return 0;
        }
        return value - 1;
      });
    }, 1000);

    return () => window.clearInterval(timer);
  }, [countdown, setSosActive]);

  const fire = async (latitude, longitude, message) => {
    try {
      await sendSos(latitude, longitude, room);
      setNotice(message || 'SOS sent');
      setSosActive(true);
      setCountdown(30);
    } catch (error) {
      setNotice('Failed to send SOS');
      console.error(error);
    }
  };

  const handleClick = () => {
    if (countdown > 0 || sosActive) {
      return;
    }

    if (!navigator.geolocation) {
      fire(0, 0, 'Geolocation unavailable, sending SOS without coordinates').catch(() => {});
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        fire(position.coords.latitude, position.coords.longitude, 'SOS sent with location').catch(() => {});
      },
      () => {
        fire(0, 0, 'Location denied, sending SOS without coordinates').catch(() => {});
      },
      { enableHighAccuracy: true, timeout: 8000, maximumAge: 0 }
    );
  };

  const disabled = countdown > 0 || sosActive;

  return (
    <div className="space-y-2">
      <button
        type="button"
        onClick={handleClick}
        disabled={disabled}
        className="inline-flex w-full items-center justify-center rounded-3xl bg-mesh-600 px-5 py-4 text-base font-semibold text-white shadow-lg shadow-mesh-900/30 transition hover:bg-mesh-500 disabled:cursor-not-allowed disabled:bg-slate-700"
      >
        {countdown > 0 ? `Send SOS (${countdown}s)` : 'Send SOS'}
      </button>
      <p className="min-h-5 text-xs text-slate-400">{notice}</p>
    </div>
  );
}

export default SosButton;
