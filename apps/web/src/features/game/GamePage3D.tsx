import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import type { GameEventResponse, GameProjection } from '../../lib/types'
import { ActionLogPanel } from './ActionLogPanel'
import { AwardsPanel } from './AwardsPanel'
import { GameScene3D } from './game3d/scene/GameScene3D'
import { ActionDock } from './game3d/hud/ActionDock'
import { GameTopHud } from './game3d/hud/GameTopHud'
import { HandAndCardsHud } from './game3d/hud/HandAndCardsHud'
import { TradeDock } from './game3d/hud/TradeDock'
import {
  buildCityCommand,
  buildRoadCommand,
  buildSettlementCommand,
  moveRobberCommand,
  setupRoadCommand,
  setupSettlementCommand,
} from './game3d/adapters/commandBuilder'
import { useDragPlacement } from './game3d/interaction/useDragPlacement'
import { useBoardPicking, type PickedTarget } from './game3d/interaction/useBoardPicking'
import { useLegalHighlights } from './game3d/interaction/useLegalHighlights'

type GamePage3DProps = {
  gameId: string
  connected: boolean
  lastSequence: number
  snapshot: GameProjection | null
  events: GameEventResponse[]
  error: string | null
  onSendCommand: (type: string, payload: Record<string, unknown>) => void
  onOpenMaritime: () => void
  onOpenDiscard: () => void
  onOpenRobber: () => void
}

export function GamePage3D({
  gameId,
  connected,
  lastSequence,
  snapshot,
  events,
  error,
  onSendCommand,
  onOpenMaritime,
  onOpenDiscard,
  onOpenRobber,
}: GamePage3DProps) {
  const highlights = useLegalHighlights(snapshot)
  const setupMode = snapshot?.phase === 'SETUP' && !snapshot?.setupCompleted
  const setupStep = useMemo(() => {
    if (!setupMode) return null
    const hasSettlementTargets = highlights.setupSettlement.size > 0
    const hasRoadTargets = highlights.setupRoad.size > 0
    if (hasSettlementTargets && !hasRoadTargets) return 'SETTLEMENT'
    if (hasRoadTargets && !hasSettlementTargets) return 'ROAD'
    return hasRoadTargets ? 'ROAD' : 'SETTLEMENT'
  }, [highlights.setupRoad, highlights.setupSettlement, setupMode])
  const picking = useBoardPicking()
  const [interactionHint, setInteractionHint] = useState<string | null>(null)

  const dragPlacement = useDragPlacement({
    highlights,
    setupMode,
    setupStep,
    onBuildRoad: (edgeIndex, isSetup) => {
      const command = isSetup ? setupRoadCommand(edgeIndex) : buildRoadCommand(edgeIndex)
      onSendCommand(command.type, command.payload)
      setInteractionHint(null)
    },
    onBuildSettlement: (nodeIndex, isSetup) => {
      const command = isSetup ? setupSettlementCommand(nodeIndex) : buildSettlementCommand(nodeIndex)
      onSendCommand(command.type, command.payload)
      setInteractionHint(null)
    },
    onBuildCity: (nodeIndex) => {
      const command = buildCityCommand(nodeIndex)
      onSendCommand(command.type, command.payload)
      setInteractionHint(null)
    },
    onInvalidAction: (message) => {
      setInteractionHint(message)
    },
  })

  const handleHoverTarget = (target: PickedTarget | null) => {
    if (target) {
      picking.onHover(target.kind, target.index)
    } else {
      picking.clearHover()
    }
    dragPlacement.setHoverTarget(target)
  }

  const handleSelectTarget = (target: PickedTarget) => {
    if (dragPlacement.dropOn(target)) {
      return
    }

    if (target.kind === 'edge') {
      if (setupMode && highlights.setupRoad.has(target.index)) {
        const command = setupRoadCommand(target.index)
        onSendCommand(command.type, command.payload)
        setInteractionHint(null)
      } else if (!setupMode && highlights.road.has(target.index)) {
        const command = buildRoadCommand(target.index)
        onSendCommand(command.type, command.payload)
        setInteractionHint(null)
      } else if (dragPlacement.activePiece) {
        setInteractionHint('Esa arista no es válida para la pieza actual.')
      }
      return
    }

    if (target.kind === 'node') {
      if (setupMode && highlights.setupSettlement.has(target.index)) {
        const command = setupSettlementCommand(target.index)
        onSendCommand(command.type, command.payload)
        setInteractionHint(null)
      } else if (!setupMode && highlights.city.has(target.index)) {
        const command = buildCityCommand(target.index)
        onSendCommand(command.type, command.payload)
        setInteractionHint(null)
      } else if (!setupMode && highlights.settlement.has(target.index)) {
        const command = buildSettlementCommand(target.index)
        onSendCommand(command.type, command.payload)
        setInteractionHint(null)
      } else if (dragPlacement.activePiece) {
        setInteractionHint('Ese nodo no es válido para la pieza actual.')
      }
    }
  }

  return (
    <main className="page-shell game-shell">
      <GameTopHud
        gameId={gameId}
        connected={connected}
        lastSequence={lastSequence}
        projection={snapshot}
        setupMode={setupMode}
        setupStep={setupStep}
      />

      <div className="topbar">
        <div className="room-actions">
          <span className="hud-chip">Hover: {picking.hovered ? `${picking.hovered.kind} ${picking.hovered.index}` : 'none'}</span>
          {dragPlacement.activePiece ? <span className="hud-chip">Drag: {dragPlacement.activePiece}</span> : null}
          {interactionHint ? <span className="hud-chip">{interactionHint}</span> : null}
        </div>
        <Link className="ghost link-button" to="/lobby">
          Salir a lobby
        </Link>
      </div>

      <section className="game-grid">
        <GameScene3D
          projection={snapshot}
          highlights={highlights}
          setupMode={setupMode}
          activeDragPiece={dragPlacement.activePiece}
          previewTarget={dragPlacement.previewTarget}
          onHoverTarget={handleHoverTarget}
          onSelectTarget={handleSelectTarget}
          onMoveRobber={(targetHexIndex) => {
            const command = moveRobberCommand(targetHexIndex)
            onSendCommand(command.type, command.payload)
          }}
        />

        <ActionDock
          phase={snapshot?.phase ?? 'UNKNOWN'}
          specialFlow={snapshot?.specialFlow ?? 'NONE'}
          canRoll={highlights.canRoll}
          canEndTurn={highlights.canEndTurn}
          canBuyDev={highlights.canBuyDev}
          setupMode={setupMode}
          setupStep={setupStep}
          activeDragPiece={dragPlacement.activePiece}
          onSendCommand={onSendCommand}
          onStartDragPiece={dragPlacement.startDrag}
          onCancelDrag={dragPlacement.cancelDrag}
          onOpenDiscard={onOpenDiscard}
          onOpenRobber={onOpenRobber}
        />

        <HandAndCardsHud projection={snapshot} onSendCommand={onSendCommand} />
        <TradeDock projection={snapshot} onSendCommand={onSendCommand} onOpenMaritime={onOpenMaritime} />
        <AwardsPanel projection={snapshot} />
        <ActionLogPanel events={events} />
      </section>

      {error ? <p className="error-banner">{error}</p> : null}
    </main>
  )
}
