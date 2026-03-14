import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../../app/auth'
import { apiClient } from '../../lib/api/client'
import type { RoomResponse } from '../../lib/types'

export function RoomPage() {
  const navigate = useNavigate()
  const { roomCode = '' } = useParams()
  const { session } = useAuth()

  const [room, setRoom] = useState<RoomResponse | null>(null)
  const [busy, setBusy] = useState<'ready' | 'start' | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!session || !roomCode) {
      return
    }

    let cancelled = false

    const fetchRoom = async () => {
      try {
        const response = await apiClient.getRoom(session.accessToken, roomCode)
        if (!cancelled) {
          setRoom(response)
        }
      } catch (requestError) {
        if (!cancelled) {
          setError(requestError instanceof Error ? requestError.message : 'No se pudo cargar la sala')
        }
      }
    }

    void fetchRoom()
    const intervalId = window.setInterval(fetchRoom, 2500)
    return () => {
      cancelled = true
      window.clearInterval(intervalId)
    }
  }, [roomCode, session])

  const mySeat = useMemo(
    () => room?.seats.find((seat) => seat.userId === session?.user.id) ?? null,
    [room, session?.user.id],
  )

  const isHost = room?.hostUserId === session?.user.id
  const allReady = (room?.seats.length ?? 0) === 4 && room?.seats.every((seat) => seat.ready)

  async function toggleReady() {
    if (!session || !room || !mySeat) return
    setBusy('ready')
    setError(null)
    try {
      const response = await apiClient.setReady(session.accessToken, room.roomCode, !mySeat.ready)
      setRoom(response)
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'No se pudo actualizar listo')
    } finally {
      setBusy(null)
    }
  }

  async function startGame() {
    if (!session || !room) return
    setBusy('start')
    setError(null)
    try {
      const response = await apiClient.startGame(session.accessToken, room.roomCode)
      navigate(`/game/${response.gameId}`)
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'No se pudo iniciar partida')
    } finally {
      setBusy(null)
    }
  }

  return (
    <main className="page-shell">
      <header className="topbar">
        <div>
          <h1>Sala {roomCode.toUpperCase()}</h1>
          <p>Esperando 4 jugadores listos</p>
        </div>
        <Link className="ghost link-button" to="/lobby">
          Volver al lobby
        </Link>
      </header>

      <section className="panel">
        <h2>Asientos</h2>
        <ul className="seat-list">
          {Array.from({ length: 4 }, (_, seatIndex) => {
            const seat = room?.seats.find((candidate) => candidate.seatIndex === seatIndex)
            if (!seat) {
              return (
                <li key={seatIndex} className="seat-item seat-empty">
                  <strong>Asiento {seatIndex + 1}</strong>
                  <span>Libre</span>
                </li>
              )
            }

            return (
              <li key={seat.userId} className={`seat-item ${seat.ready ? 'seat-ready' : ''}`}>
                <strong>{seat.nickname}</strong>
                <span>{seat.ready ? 'Listo' : 'No listo'}</span>
              </li>
            )
          })}
        </ul>

        <div className="room-actions">
          {mySeat ? (
            <button onClick={toggleReady} disabled={busy !== null}>
              {busy === 'ready' ? 'Guardando...' : mySeat.ready ? 'Marcar no listo' : 'Marcar listo'}
            </button>
          ) : null}

          {isHost ? (
            <button onClick={startGame} disabled={busy !== null || !allReady}>
              {busy === 'start' ? 'Iniciando...' : 'Iniciar partida'}
            </button>
          ) : null}
        </div>
      </section>

      {error ? <p className="error-banner">{error}</p> : null}
    </main>
  )
}
