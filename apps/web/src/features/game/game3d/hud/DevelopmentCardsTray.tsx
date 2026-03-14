import type { GameProjection } from '../../../../lib/types'
import { playKnightCommand, playProgressCommand } from '../adapters/commandBuilder'
import { DevCard } from './DevCard'

type DevelopmentCardsTrayProps = {
  projection: GameProjection | null
  onSendCommand: (type: string, payload: Record<string, unknown>) => void
}

export function DevelopmentCardsTray({ projection, onSendCommand }: DevelopmentCardsTrayProps) {
  const self = projection?.players.find((player) => player.self)
  const cards = self?.devCards ?? {}

  return (
    <section className="panel">
      <h3>Cartas de desarrollo</h3>
      <div className="dev-card-grid">
        <DevCard
          label="Caballero"
          count={Number(cards.KNIGHT ?? 0)}
          accent="#2563eb"
          actionLabel="Jugar"
          onAction={() => {
            const command = playKnightCommand()
            onSendCommand(command.type, command.payload)
          }}
        />
        <DevCard
          label="Carreteras"
          count={Number(cards.ROAD_BUILDING ?? 0)}
          accent="#b45309"
          actionLabel="Usar"
          onAction={() => {
            const command = playProgressCommand('ROAD_BUILDING', { edges: [] })
            onSendCommand(command.type, command.payload)
          }}
        />
        <DevCard
          label="Monopolio"
          count={Number(cards.MONOPOLY ?? 0)}
          accent="#7c3aed"
          actionLabel="Usar"
          onAction={() => {
            const command = playProgressCommand('MONOPOLY', { resourceType: 'WOOD' })
            onSendCommand(command.type, command.payload)
          }}
        />
        <DevCard
          label="Año abundancia"
          count={Number(cards.YEAR_OF_PLENTY ?? 0)}
          accent="#0f766e"
          actionLabel="Usar"
          onAction={() => {
            const command = playProgressCommand('YEAR_OF_PLENTY', {
              resourceA: 'WOOD',
              resourceB: 'GRAIN',
            })
            onSendCommand(command.type, command.payload)
          }}
        />
        <DevCard label="Puntos ocultos" count={Number(cards.VICTORY_POINT ?? 0)} accent="#9a3412" />
      </div>
      <p className="muted-inline">Total en mano: {self?.devCardCount ?? 0}</p>
    </section>
  )
}
