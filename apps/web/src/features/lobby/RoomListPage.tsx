import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../app/auth'
import { clearSession } from '../../lib/auth/session'
import { apiClient } from '../../lib/api/client'

export function RoomListPage() {
  const navigate = useNavigate()
  const { session, setSession } = useAuth()
  const [roomName, setRoomName] = useState('Mesa Catan')
  const [roomCode, setRoomCode] = useState('')
  const [busyAction, setBusyAction] = useState<'create' | 'join' | null>(null)
  const [error, setError] = useState<string | null>(null)

  async function handleCreate() {
    if (!session) return
    setBusyAction('create')
    setError(null)
    try {
      const room = await apiClient.createRoom(session.accessToken, roomName)
      navigate(`/room/${room.roomCode}`)
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'No se pudo crear la sala')
    } finally {
      setBusyAction(null)
    }
  }

  async function handleJoin() {
    if (!session || !roomCode.trim()) return
    setBusyAction('join')
    setError(null)
    try {
      const room = await apiClient.joinRoom(session.accessToken, roomCode.trim().toUpperCase())
      navigate(`/room/${room.roomCode}`)
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'No se pudo unir a la sala')
    } finally {
      setBusyAction(null)
    }
  }

  function handleLogout() {
    clearSession()
    setSession(null)
    navigate('/login')
  }

  return (
    <main className="page-shell">
      <header className="topbar">
        <div>
          <h1>Lobby de Catan</h1>
          <p>Hola, {session?.user.nickname}</p>
        </div>
        <button className="ghost" onClick={handleLogout}>
          Salir
        </button>
      </header>

      <section className="lobby-grid">
        <article className="panel">
          <h2>Crear sala</h2>
          <p>Abre una partida nueva y comparte el código con 3 jugadores más.</p>
          <label>
            Nombre de sala
            <input
              value={roomName}
              onChange={(event) => setRoomName(event.target.value)}
              placeholder="Mesa Catan"
            />
          </label>
          <button onClick={handleCreate} disabled={busyAction !== null}>
            {busyAction === 'create' ? 'Creando...' : 'Crear sala'}
          </button>
        </article>

        <article className="panel">
          <h2>Unirse por código</h2>
          <p>Introduce un código válido de 6 caracteres.</p>
          <label>
            Código de sala
            <input
              value={roomCode}
              onChange={(event) => setRoomCode(event.target.value)}
              placeholder="ABC123"
              maxLength={6}
            />
          </label>
          <button onClick={handleJoin} disabled={busyAction !== null || roomCode.trim().length < 6}>
            {busyAction === 'join' ? 'Uniendo...' : 'Unirme'}
          </button>
        </article>
      </section>

      {error ? <p className="error-banner">{error}</p> : null}
    </main>
  )
}
