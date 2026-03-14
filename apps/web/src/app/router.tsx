import { useMemo, useState } from 'react'
import { BrowserRouter, Navigate, Outlet, Route, Routes } from 'react-router-dom'
import { AuthContext } from './auth'
import { LoginPage } from '../features/auth/LoginPage'
import { RegisterPage } from '../features/auth/RegisterPage'
import { RoomListPage } from '../features/lobby/RoomListPage'
import { RoomPage } from '../features/lobby/RoomPage'
import { GamePage } from '../features/game/GamePage'
import { clearSession, loadSession, type Session } from '../lib/auth/session'
import { ToastViewport } from '../lib/ui/toast'

function ProtectedLayout() {
  return <Outlet />
}

function PublicLayout() {
  return <Outlet />
}

function RequireAuth({ session }: { session: Session | null }) {
  if (!session) {
    return <Navigate to="/login" replace />
  }
  return <ProtectedLayout />
}

function RedirectIfAuthenticated({ session }: { session: Session | null }) {
  if (session) {
    return <Navigate to="/lobby" replace />
  }
  return <PublicLayout />
}

export function AppRouter() {
  const [session, setSessionState] = useState<Session | null>(() => loadSession())

  const authContext = useMemo(
    () => ({
      session,
      setSession: (nextSession: Session | null) => {
        if (!nextSession) {
          clearSession()
        }
        setSessionState(nextSession)
      },
    }),
    [session],
  )

  return (
    <AuthContext.Provider value={authContext}>
      <BrowserRouter>
        <Routes>
          <Route element={<RedirectIfAuthenticated session={session} />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
          </Route>

          <Route element={<RequireAuth session={session} />}>
            <Route path="/lobby" element={<RoomListPage />} />
            <Route path="/room/:roomCode" element={<RoomPage />} />
            <Route path="/game/:gameId" element={<GamePage />} />
          </Route>

          <Route path="*" element={<Navigate to={session ? '/lobby' : '/login'} replace />} />
        </Routes>
        <ToastViewport />
      </BrowserRouter>
    </AuthContext.Provider>
  )
}
