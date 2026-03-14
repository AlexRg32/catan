import { useState } from 'react'
import type { ResourceType } from './game3d/adapters/commandBuilder'

type MaritimeTradeModalProps = {
  open: boolean
  onClose: () => void
  onSubmit: (ratio: 4 | 3 | 2, giveType: string, getType: string) => void
}

const TYPES: ResourceType[] = ['WOOD', 'WOOL', 'GRAIN', 'CLAY', 'ORE']
const LABELS: Record<ResourceType, string> = {
  WOOD: 'Madera',
  WOOL: 'Lana',
  GRAIN: 'Trigo',
  CLAY: 'Arcilla',
  ORE: 'Mineral',
}

export function MaritimeTradeModal({ open, onClose, onSubmit }: MaritimeTradeModalProps) {
  const [ratio, setRatio] = useState<4 | 3 | 2>(4)
  const [giveType, setGiveType] = useState<ResourceType>('WOOD')
  const [getType, setGetType] = useState<ResourceType>('ORE')

  if (!open) {
    return null
  }

  return (
    <div className="modal-backdrop">
      <section className="modal-card">
        <h3>Comercio marítimo</h3>
        <p>Selecciona ratio y recursos para intercambiar con puertos o banca.</p>

        <div className="ratio-chip-row">
          {[4, 3, 2].map((value) => (
            <button
              key={value}
              type="button"
              className={ratio === value ? 'trade-chip trade-chip--active' : 'trade-chip ghost'}
              onClick={() => setRatio(value as 4 | 3 | 2)}
            >
              {value}:1
            </button>
          ))}
        </div>

        <div>
          <p className="muted-inline">Entrego ({ratio})</p>
          <div className="resource-chip-grid">
            {TYPES.map((resource) => (
              <button
                key={`maritime-give-${resource}`}
                type="button"
                className={giveType === resource ? 'trade-chip trade-chip--active' : 'trade-chip ghost'}
                onClick={() => setGiveType(resource)}
              >
                {LABELS[resource]}
              </button>
            ))}
          </div>
        </div>

        <div>
          <p className="muted-inline">Recibo (1)</p>
          <div className="resource-chip-grid">
            {TYPES.map((resource) => (
              <button
                key={`maritime-get-${resource}`}
                type="button"
                className={getType === resource ? 'trade-chip trade-chip--active' : 'trade-chip ghost'}
                onClick={() => setGetType(resource)}
              >
                {LABELS[resource]}
              </button>
            ))}
          </div>
        </div>

        <div className="room-actions">
          <button onClick={() => onSubmit(ratio, giveType, getType)} disabled={giveType === getType}>
            Confirmar marítimo
          </button>
          <button className="ghost" onClick={onClose}>
            Cerrar
          </button>
        </div>
      </section>
    </div>
  )
}
