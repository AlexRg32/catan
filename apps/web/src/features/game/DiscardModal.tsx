import { useMemo, useState } from 'react'

type DiscardModalProps = {
  open: boolean
  requiredCount?: number
  availableResources?: Record<string, number> | null
  onClose: () => void
  onDiscard: (cards?: Record<string, number>) => void
}

const ORDER = ['WOOD', 'WOOL', 'GRAIN', 'CLAY', 'ORE']

export function DiscardModal({
  open,
  requiredCount,
  availableResources,
  onClose,
  onDiscard,
}: DiscardModalProps) {
  const [selection, setSelection] = useState<Record<string, number>>({})

  const effectiveResources = availableResources ?? null
  const effectiveRequired = requiredCount ?? 1
  const selectedTotal = useMemo(
    () => Object.values(selection).reduce((acc, value) => acc + value, 0),
    [selection],
  )

  if (!open) {
    return null
  }

  const handleClose = () => {
    setSelection({})
    onClose()
  }

  const handleDiscard = () => {
    onDiscard(selection)
    setSelection({})
  }

  return (
    <div className="modal-backdrop">
      <section className="modal-card">
        <h3>Descartar recursos</h3>
        <p>Debes descartar exactamente {effectiveRequired} cartas.</p>

        {effectiveResources ? (
          <div className="discard-grid discard-grid--cards">
            {ORDER.map((resource) => {
              const maxAvailable = Number(effectiveResources[resource] ?? 0)
              if (maxAvailable <= 0) return null
              const value = Number(selection[resource] ?? 0)
              return (
                <article key={resource} className="resource-stepper-card">
                  <strong>{resource}</strong>
                  <span className="muted-inline">Disponibles: {maxAvailable}</span>
                  <div className="stepper-row">
                    <button
                      className="ghost"
                      type="button"
                      onClick={() => {
                        setSelection((prev) => ({ ...prev, [resource]: Math.max(0, value - 1) }))
                      }}
                    >
                      -
                    </button>
                    <strong className="stepper-count">{value}</strong>
                    <button
                      className="ghost"
                      type="button"
                      onClick={() => {
                        setSelection((prev) => ({ ...prev, [resource]: Math.min(maxAvailable, value + 1) }))
                      }}
                    >
                      +
                    </button>
                  </div>
                </article>
              )
            })}
          </div>
        ) : null}

        <p className="muted-inline">
          Seleccionadas: {selectedTotal}/{effectiveRequired}
        </p>

        <div className="room-actions">
          <button
            onClick={handleDiscard}
            disabled={Boolean(effectiveResources) && selectedTotal !== effectiveRequired}
          >
            Confirmar descarte
          </button>
          <button className="ghost" onClick={handleClose}>
            Cerrar
          </button>
        </div>
      </section>
    </div>
  )
}
