import type { GameProjection } from '../../../lib/types'

type BuildingsLayerProps = {
  projection: GameProjection | null
}

export function BuildingsLayer({ projection }: BuildingsLayerProps) {
  return (
    <div className="buildings-layer" aria-label="Asentamientos y ciudades">
      {(projection?.players ?? []).map((player, index) => (
        <span key={player.playerId} className="building-chip">
          P{index + 1}: VP visibles {player.visibleVictoryPoints}
        </span>
      ))}
    </div>
  )
}
