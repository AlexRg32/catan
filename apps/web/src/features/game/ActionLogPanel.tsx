import type { GameEventResponse } from '../../lib/types'

type ActionLogPanelProps = {
  events: GameEventResponse[]
}

export function ActionLogPanel({ events }: ActionLogPanelProps) {
  const iconFor = (type: string) => {
    if (type.includes('roll')) return 'DICE'
    if (type.includes('build') || type.includes('setup_place')) return 'BUILD'
    if (type.includes('trade')) return 'TRADE'
    if (type.includes('robber') || type.includes('knight')) return 'ROBBER'
    if (type.includes('dev')) return 'CARD'
    return 'EVENT'
  }

  const titleFor = (type: string) => {
    if (type === 'roll_dice') return 'Tirada de dados'
    if (type === 'end_turn') return 'Fin de turno'
    if (type === 'build_road') return 'Construcción: carretera'
    if (type === 'build_settlement') return 'Construcción: poblado'
    if (type === 'build_city') return 'Construcción: ciudad'
    if (type === 'buy_dev_card') return 'Compra de desarrollo'
    if (type === 'setup_place_settlement') return 'Setup: poblado'
    if (type === 'setup_place_road') return 'Setup: carretera'
    if (type === 'move_robber') return 'Movimiento del ladrón'
    if (type === 'discard_resources') return 'Descarte de recursos'
    if (type === 'maritime_trade') return 'Comercio marítimo'
    if (type === 'propose_trade') return 'Propuesta de comercio'
    if (type === 'play_dev_knight') return 'Carta: caballero'
    if (type === 'play_dev_progress') return 'Carta: progreso'
    return type
  }

  return (
    <section className="panel timeline-panel">
      <h2>Timeline</h2>
      <ol className="events-list">
        {events.slice(-12).map((event) => (
          <li key={event.seq} className="timeline-item">
            <span className="timeline-tag">{iconFor(event.type)}</span>
            <span className="timeline-seq">#{event.seq}</span>
            <strong>{titleFor(event.type)}</strong>
            <span className="timeline-time">
              {new Date(event.createdAt).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })}
            </span>
          </li>
        ))}
      </ol>
    </section>
  )
}
