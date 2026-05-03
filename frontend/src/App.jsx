import { Navigate, Route, Routes } from 'react-router-dom';
import ChatRoom from './components/ChatRoom';
import JoinForm from './components/JoinForm';
import useStore from './store/useStore';

function App() {
  const nick = useStore((state) => state.nick);

  return (
    <Routes>
      <Route
        path="/"
        element={nick && nick !== 'anonymous' ? <Navigate to="/chat" replace /> : <JoinForm />}
      />
      <Route
        path="/chat"
        element={nick && nick !== 'anonymous' ? <ChatRoom /> : <Navigate to="/" replace />}
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
