import type { GameProjection } from '../../lib/types'

type PlayerHandPanelProps = {
  projection: GameProjection | null
}

export function PlayerHandPanel({ projection }: PlayerHandPanelProps) {
  const self = projection?.players.find((player) => player.self)

  return (
    <section className="panel">
      <h2>Tu mano</h2>
      <p>Recursos totales: {self?.resourceCount ?? 0}</p>
      <p>Cartas desarrollo: {self?.devCardCount ?? 0}</p>
      <pre className="json-preview">{JSON.stringify(self?.resources ?? {}, null, 2)}</pre>
    </section>
  )
}
