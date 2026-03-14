import type { GameProjection } from '../../lib/types'

type AwardsPanelProps = {
  projection: GameProjection | null
}

export function AwardsPanel({ projection }: AwardsPanelProps) {
  const longest = projection?.players.find((player) => player.hasLongestRoad)
  const largest = projection?.players.find((player) => player.hasLargestArmy)

  return (
    <section className="panel">
      <h2>Premios</h2>
      <p>Carretera más larga: {longest ? longest.playerId.slice(0, 8) : 'Sin asignar'}</p>
      <p>Ejército más grande: {largest ? largest.playerId.slice(0, 8) : 'Sin asignar'}</p>
    </section>
  )
}
