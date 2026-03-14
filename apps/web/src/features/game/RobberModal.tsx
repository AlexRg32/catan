import { useState } from 'react'

type RobberHexOption = {
  hexIndex: number
  label: string
}

type RobberModalProps = {
  open: boolean
  defaultHexIndex?: number
  hexOptions?: RobberHexOption[]
  victimsByHex?: Record<number, { playerId: string; label: string }[]>
  onClose: () => void
  onMove: (targetHexIndex?: number, targetPlayerId?: string | null) => void
}

export function RobberModal({
  open,
  defaultHexIndex,
  hexOptions,
  victimsByHex,
  onClose,
  onMove,
}: RobberModalProps) {
  const [hexIndex, setHexIndex] = useState(defaultHexIndex ?? 0)
  const [victim, setVictim] = useState<string>('')
  const victimsForHex = victimsByHex?.[hexIndex] ?? []

  if (!open) {
    return null
  }

  return (
    <div className="modal-backdrop">
      <section className="modal-card">
        <h3>Mover ladrón</h3>
        <p>Selecciona casilla destino y rival adyacente.</p>

        <div>
          <p className="muted-inline">Hex objetivo</p>
          <div className="hex-option-grid">
            {(hexOptions ?? []).map((option) => (
              <button
                key={option.hexIndex}
                className={hexIndex === option.hexIndex ? 'trade-chip trade-chip--active' : 'trade-chip ghost'}
                type="button"
                onClick={() => {
                  setHexIndex(option.hexIndex)
                  setVictim('')
                }}
              >
                {option.label}
              </button>
            ))}
          </div>
        </div>

        <label>
          Jugador a robar
          <select value={victim} onChange={(event) => setVictim(event.target.value)}>
            <option value="">Sin objetivo</option>
            {victimsForHex.map((option) => (
              <option key={option.playerId} value={option.playerId}>
                {option.label}
              </option>
            ))}
          </select>
        </label>
        <p className="muted-inline">
          {victimsForHex.length === 0
            ? 'No hay víctimas adyacentes en ese hex.'
            : `Víctimas adyacentes: ${victimsForHex.length}`}
        </p>

        <div className="room-actions">
          <button
            onClick={() => onMove(hexIndex, victim || null)}
            disabled={Boolean(victim) && !victimsForHex.some((option) => option.playerId === victim)}
          >
            Confirmar movimiento
          </button>
          <button className="ghost" onClick={onClose}>
            Cerrar
          </button>
        </div>
      </section>
    </div>
  )
}
