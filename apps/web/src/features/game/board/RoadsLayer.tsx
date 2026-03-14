import type { GameProjection } from '../../../lib/types'

type RoadsLayerProps = {
  projection: GameProjection | null
}

export function RoadsLayer({ projection }: RoadsLayerProps) {
  return (
    <div className="roads-layer" aria-label="Carreteras">
      {(projection?.players ?? []).map((player, index) => (
        <span key={player.playerId} className="road-chip">
          P{index + 1}: {player.hasLongestRoad ? 'Carretera más larga' : 'Carreteras en progreso'}
        </span>
      ))}
    </div>
  )
}
