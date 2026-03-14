import type { GameProjection } from '../../../../lib/types'
import type { SetupStep } from '../interaction/useDragPlacement'

type GameTopHudProps = {
  gameId: string
  connected: boolean
  lastSequence: number
  projection: GameProjection | null
  setupMode: boolean
  setupStep: SetupStep
}

export function GameTopHud({
  gameId,
  connected,
  lastSequence,
  projection,
  setupMode,
  setupStep,
}: GameTopHudProps) {
  const active = projection?.players.find((player) => player.playerId === projection.activePlayerId)
  const lastRoll = projection?.lastRoll ?? 0

  return (
    <section className="panel game-top-hud">
      <div>
        <h1>Partida {gameId.slice(0, 8)} · 3D</h1>
        <p>
          Estado: {connected ? 'Conectado' : 'Reconectando'} · Seq: {lastSequence}
        </p>
      </div>
      <div className="hud-chips">
        <span className="hud-chip">Turno #{projection?.turnNumber ?? '-'}</span>
        <span className="hud-chip">Dados {lastRoll > 0 ? lastRoll : '-'}</span>
        <span className="hud-chip">Fase {projection?.phase ?? '-'}</span>
        <span className="hud-chip">Flujo {projection?.specialFlow ?? 'NONE'}</span>
        <span className="hud-chip">Activo {active ? active.playerId.slice(0, 8) : '---'}</span>
        {setupMode ? (
          <span className="hud-chip">
            Setup: {setupStep === 'ROAD' ? 'coloca carretera' : 'coloca poblado'}
          </span>
        ) : null}
      </div>
      <ol className="hud-score-list">
        {(projection?.players ?? []).map((player, index) => (
          <li
            key={player.playerId}
            className={[
              'hud-score-item',
              player.playerId === projection?.activePlayerId ? 'hud-score-item--active' : '',
              player.self ? 'hud-score-item--self' : '',
            ]
              .filter(Boolean)
              .join(' ')}
          >
            <strong>J{index + 1}</strong>
            <span>{player.playerId.slice(0, 8)}</span>
            <span>{player.totalVictoryPoints} PV</span>
            <span>{player.resourceCount} rec</span>
            <span>{player.hasLongestRoad ? 'Ruta' : '-'}</span>
            <span>{player.hasLargestArmy ? 'Ejército' : '-'}</span>
          </li>
        ))}
      </ol>
    </section>
  )
}
