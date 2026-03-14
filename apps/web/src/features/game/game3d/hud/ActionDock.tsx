import { buyDevCardCommand, endTurnCommand, rollDiceCommand } from '../adapters/commandBuilder'
import type { DragPiece, SetupStep } from '../interaction/useDragPlacement'

type ActionDockProps = {
  phase: string
  specialFlow: string
  canRoll: boolean
  canEndTurn: boolean
  canBuyDev: boolean
  setupMode: boolean
  setupStep: SetupStep
  activeDragPiece: DragPiece | null
  onSendCommand: (type: string, payload: Record<string, unknown>) => void
  onStartDragPiece: (piece: DragPiece) => void
  onCancelDrag: () => void
  onOpenDiscard: () => void
  onOpenRobber: () => void
}

export function ActionDock({
  phase,
  specialFlow,
  canRoll,
  canEndTurn,
  canBuyDev,
  setupMode,
  setupStep,
  activeDragPiece,
  onSendCommand,
  onStartDragPiece,
  onCancelDrag,
  onOpenDiscard,
  onOpenRobber,
}: ActionDockProps) {
  return (
    <section className="panel">
      <h3>Acciones</h3>
      <p className="muted-inline">
        Fase {phase} · Flujo {specialFlow}
      </p>
      {setupMode ? (
        <p className="muted-inline">
          Secuencia setup: {setupStep === 'ROAD' ? 'coloca carretera' : 'coloca poblado'}
        </p>
      ) : null}
      <div className="room-actions">
        <button
          onClick={() => {
            const command = rollDiceCommand()
            onSendCommand(command.type, command.payload)
          }}
          disabled={!canRoll}
        >
          Tirar dados
        </button>
        <button
          onClick={() => {
            const command = buyDevCardCommand()
            onSendCommand(command.type, command.payload)
          }}
          disabled={!canBuyDev}
        >
          Comprar desarrollo
        </button>
        <button
          onClick={() => {
            const command = endTurnCommand()
            onSendCommand(command.type, command.payload)
          }}
          disabled={!canEndTurn}
        >
          Finalizar turno
        </button>
      </div>

      <div className="drag-piece-grid">
        <button
          className={activeDragPiece === 'settlement' ? 'drag-active' : 'ghost'}
          onClick={() => onStartDragPiece('settlement')}
          disabled={setupMode && setupStep === 'ROAD'}
        >
          {setupMode ? 'Arrastrar poblado setup' : 'Arrastrar poblado'}
        </button>
        <button
          className={activeDragPiece === 'city' ? 'drag-active' : 'ghost'}
          onClick={() => onStartDragPiece('city')}
          disabled={setupMode}
        >
          Arrastrar ciudad
        </button>
        <button
          className={activeDragPiece === 'road' ? 'drag-active' : 'ghost'}
          onClick={() => onStartDragPiece('road')}
          disabled={setupMode && setupStep === 'SETTLEMENT'}
        >
          {setupMode ? 'Arrastrar carretera setup' : 'Arrastrar carretera'}
        </button>
        <button className="ghost" onClick={onCancelDrag}>
          Cancelar arrastre
        </button>
      </div>

      <div className="room-actions">
        <button className="ghost" onClick={onOpenDiscard}>
          Descarte
        </button>
        <button className="ghost" onClick={onOpenRobber}>
          Ladrón
        </button>
      </div>
    </section>
  )
}
