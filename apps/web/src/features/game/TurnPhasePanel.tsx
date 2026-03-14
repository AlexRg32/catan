import type { GameProjection } from '../../lib/types'

type TurnPhasePanelProps = {
  projection: GameProjection | null
  onEndTurn: () => void
}

export function TurnPhasePanel({ projection, onEndTurn }: TurnPhasePanelProps) {
  return (
    <section className="panel">
      <h2>Fase de turno</h2>
      <p>Fase actual: {projection?.phase ?? '...'}</p>
      <p>Flujo especial: {projection?.specialFlow ?? 'NONE'}</p>
      <button onClick={onEndTurn}>Finalizar turno</button>
    </section>
  )
}
