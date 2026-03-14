import { useEffect, useMemo, useRef, useState, useSyncExternalStore } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useAuth } from '../../app/auth'
import type { GameProjection } from '../../lib/types'
import { createGameStore } from '../../lib/state/gameStore'
import { showToast } from '../../lib/ui/toastStore'
import { GameWsClient } from '../../lib/ws/client'
import type { CommandEnvelope } from '../../lib/ws/client'
import { resyncGameState } from '../../lib/ws/resync'
import { ActionLogPanel } from './ActionLogPanel'
import { AwardsPanel } from './AwardsPanel'
import { BuildPanel } from './BuildPanel'
import { DevCardsPanel } from './DevCardsPanel'
import { DiscardModal } from './DiscardModal'
import { EndGameModal } from './EndGameModal'
import { GamePage3D } from './GamePage3D'
import './GameLayout.css'
import { MaritimeTradeModal } from './MaritimeTradeModal'
import { PlayerHandPanel } from './PlayerHandPanel'
import { RobberModal } from './RobberModal'
import { TradePanel } from './TradePanel'
import { TurnPhasePanel } from './TurnPhasePanel'
import { BoardRenderer } from './board/BoardRenderer'
import {
  discardResourcesCommand,
  maritimeTradeCommand,
  moveRobberCommand,
  type ResourceType,
} from './game3d/adapters/commandBuilder'

const WS_URL =
  import.meta.env.VITE_WS_URL ??
  `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}/ws`
const ENABLE_3D_BOARD = import.meta.env.VITE_ENABLE_3D_BOARD === 'true'

function parseResourceInput(raw: string): Record<string, number> {
  const [resource, amountRaw] = raw.split(':')
  const amount = Number(amountRaw)
  if (!resource || Number.isNaN(amount) || amount <= 0) {
    return {}
  }
  return { [resource.toUpperCase()]: amount }
}

function asResourceType(value: string): ResourceType {
  if (value === 'WOOD' || value === 'WOOL' || value === 'GRAIN' || value === 'CLAY' || value === 'ORE') {
    return value
  }
  return 'WOOD'
}

function buildRobberHexOptions(snapshot: GameProjection | null): { hexIndex: number; label: string }[] {
  const hexes = snapshot?.board?.hexes ?? []
  return hexes
    .map((hex) => ({
      hexIndex: hex.hexIndex,
      label: `${hex.terrain}${hex.numberToken ? ` ${hex.numberToken}` : ''} (#${hex.hexIndex})`,
    }))
    .sort((a, b) => a.hexIndex - b.hexIndex)
}

function buildRobberVictimsByHex(
  snapshot: GameProjection | null,
): Record<number, { playerId: string; label: string }[]> {
  const board = snapshot?.board
  if (!board) return {}

  const labelsByPlayerId: Record<string, string> = {}
  ;(snapshot?.players ?? []).forEach((player, index) => {
    labelsByPlayerId[player.playerId] = `Jugador ${index + 1} (${player.playerId.slice(0, 8)})`
  })

  const selfIds = new Set((snapshot?.players ?? []).filter((player) => player.self).map((player) => player.playerId))
  const victimsByHex: Record<number, { playerId: string; label: string }[]> = {}

  board.intersections.forEach((intersection) => {
    const ownerId = intersection.ownerPlayerId
    if (!ownerId || selfIds.has(ownerId)) return

    intersection.adjacentHexIndexes.forEach((hexIndex) => {
      const current = victimsByHex[hexIndex] ?? []
      if (!current.some((candidate) => candidate.playerId === ownerId)) {
        victimsByHex[hexIndex] = [
          ...current,
          {
            playerId: ownerId,
            label: labelsByPlayerId[ownerId] ?? ownerId.slice(0, 8),
          },
        ]
      }
    })
  })

  return victimsByHex
}

export function GamePage() {
  const { gameId = '' } = useParams()
  const { session } = useAuth()
  const gameStore = useMemo(() => createGameStore(), [])
  const wsClientRef = useRef<GameWsClient | null>(null)
  const [showMaritime, setShowMaritime] = useState(false)
  const [showDiscard, setShowDiscard] = useState(false)
  const [showRobber, setShowRobber] = useState(false)
  const [dismissedEndGame, setDismissedEndGame] = useState(false)

  const storeState = useSyncExternalStore(
    (listener) => gameStore.subscribe(listener),
    () => gameStore.getState(),
  )

  useEffect(() => {
    if (!session || !gameId) {
      return
    }

    void resyncGameState(session.accessToken, gameId, gameStore).catch((error) => {
      const message = error instanceof Error ? error.message : 'No se pudo sincronizar partida'
      gameStore.setError(message)
      showToast(message)
    })
  }, [session, gameId, gameStore])

  useEffect(() => {
    if (!session || !gameId) {
      return
    }

    const wsClient = new GameWsClient({
      wsUrl: WS_URL,
      accessToken: session.accessToken,
      onAck: () => {
        void resyncGameState(session.accessToken, gameId, gameStore).catch((error) => {
          const message = error instanceof Error ? error.message : 'No se pudo aplicar deltas'
          gameStore.setError(message)
          showToast(message)
        })
      },
      onError: (message) => {
        gameStore.setError(message)
        showToast(message)
      },
    })

    wsClientRef.current = wsClient
    wsClient.connect()
    gameStore.setConnected(true)

    return () => {
      wsClient.disconnect()
      wsClientRef.current = null
      gameStore.setConnected(false)
    }
  }, [session, gameId, gameStore])

  function sendCommand(type: string, payload: Record<string, unknown>) {
    if (!gameId) {
      return
    }

    if (!wsClientRef.current) {
      const message = 'El socket aún no está conectado.'
      gameStore.setError(message)
      showToast(message)
      return
    }

    const envelope: CommandEnvelope = {
      commandId: crypto.randomUUID(),
      gameId,
      type,
      sentAt: new Date().toISOString(),
      payload,
    }

    try {
      wsClientRef.current.send(envelope)
    } catch {
      const message = 'No se pudo enviar comando. Reintenta.'
      gameStore.setError(message)
      showToast(message)
    }
  }

  function handleProposeTrade(wantRaw: string, giveRaw: string) {
    const projection = storeState.snapshot
    const targetIds = (projection?.players ?? []).filter((player) => !player.self).map((player) => player.playerId)

    sendCommand('propose_trade', {
      toPlayerIds: targetIds,
      want: parseResourceInput(wantRaw),
      give: parseResourceInput(giveRaw),
    })
  }

  const snapshot = storeState.snapshot
  const selfPlayer = snapshot?.players.find((player) => player.self)
  const discardRequired =
    snapshot?.specialFlow === 'DISCARD_RESOLUTION' && (selfPlayer?.resourceCount ?? 0) > 7
      ? Math.floor((selfPlayer?.resourceCount ?? 0) / 2)
      : 1
  const robberHexOptions = buildRobberHexOptions(snapshot)
  const robberVictimsByHex = buildRobberVictimsByHex(snapshot)
  const robberHexFallback = snapshot?.board?.hexes.find((hex) => hex.hasRobber)?.hexIndex ?? robberHexOptions[0]?.hexIndex ?? 0

  if (ENABLE_3D_BOARD) {
    return (
      <>
        <GamePage3D
          gameId={gameId}
          connected={storeState.connected}
          lastSequence={storeState.lastSequence}
          snapshot={snapshot}
          events={storeState.events}
          error={storeState.error}
          onSendCommand={sendCommand}
          onOpenMaritime={() => setShowMaritime(true)}
          onOpenDiscard={() => setShowDiscard(true)}
          onOpenRobber={() => setShowRobber(true)}
        />
        <DiscardModal
          open={showDiscard}
          requiredCount={discardRequired}
          availableResources={selfPlayer?.resources ?? null}
          onClose={() => setShowDiscard(false)}
          onDiscard={(cards) => {
            const command = discardResourcesCommand(cards ?? { WOOD: 1 })
            sendCommand(command.type, command.payload)
          }}
        />
        <RobberModal
          open={showRobber}
          defaultHexIndex={robberHexFallback}
          hexOptions={robberHexOptions}
          victimsByHex={robberVictimsByHex}
          onClose={() => setShowRobber(false)}
          onMove={(targetHexIndex, targetPlayerId) => {
            const command = moveRobberCommand(targetHexIndex ?? robberHexFallback, targetPlayerId ?? null)
            sendCommand(command.type, command.payload)
          }}
        />
        <MaritimeTradeModal
          open={showMaritime}
          onClose={() => setShowMaritime(false)}
          onSubmit={(ratio, giveType, getType) => {
            const command = maritimeTradeCommand(ratio, asResourceType(giveType), asResourceType(getType))
            sendCommand(command.type, command.payload)
            setShowMaritime(false)
          }}
        />
        <EndGameModal
          open={Boolean(snapshot?.finished) && !dismissedEndGame}
          winnerId={snapshot?.winnerPlayerId ?? null}
          onClose={() => setDismissedEndGame(true)}
        />
      </>
    )
  }

  return (
    <main className="page-shell game-shell">
      <header className="topbar">
        <div>
          <h1>Partida {gameId.slice(0, 8)}</h1>
          <p>
            Estado: {storeState.connected ? 'Conectado' : 'Reconectando'} · Seq: {storeState.lastSequence}
          </p>
        </div>
        <Link className="ghost link-button" to="/lobby">
          Salir a lobby
        </Link>
      </header>

      <section className="game-grid">
        <BoardRenderer projection={snapshot} />
        <TurnPhasePanel projection={snapshot} onEndTurn={() => sendCommand('end_turn', {})} />
        <BuildPanel onBuyDevCard={() => showToast('Compra de desarrollo en integración final', 'info')} />
        <PlayerHandPanel projection={snapshot} />
        <TradePanel onProposeTrade={handleProposeTrade} onOpenMaritime={() => setShowMaritime(true)} />
        <DevCardsPanel onPlayKnight={() => sendCommand('play_dev_knight', {})} />
        <AwardsPanel projection={snapshot} />
        <ActionLogPanel events={storeState.events} />
      </section>

      <div className="room-actions">
        <button className="ghost" onClick={() => setShowDiscard(true)}>
          Abrir modal descarte
        </button>
        <button className="ghost" onClick={() => setShowRobber(true)}>
          Abrir modal ladrón
        </button>
      </div>

      <DiscardModal
        open={showDiscard}
        requiredCount={discardRequired}
        availableResources={selfPlayer?.resources ?? null}
        onClose={() => setShowDiscard(false)}
        onDiscard={(cards) => {
          const command = discardResourcesCommand(cards ?? { WOOD: 1 })
          sendCommand(command.type, command.payload)
        }}
      />
      <RobberModal
        open={showRobber}
        defaultHexIndex={robberHexFallback}
        hexOptions={robberHexOptions}
        victimsByHex={robberVictimsByHex}
        onClose={() => setShowRobber(false)}
        onMove={(targetHexIndex, targetPlayerId) => {
          const command = moveRobberCommand(targetHexIndex ?? robberHexFallback, targetPlayerId ?? null)
          sendCommand(command.type, command.payload)
        }}
      />
      <MaritimeTradeModal
        open={showMaritime}
        onClose={() => setShowMaritime(false)}
        onSubmit={(ratio, giveType, getType) => {
          const command = maritimeTradeCommand(ratio, asResourceType(giveType), asResourceType(getType))
          sendCommand(command.type, command.payload)
          setShowMaritime(false)
        }}
      />
      <EndGameModal
        open={Boolean(snapshot?.finished) && !dismissedEndGame}
        winnerId={snapshot?.winnerPlayerId ?? null}
        onClose={() => setDismissedEndGame(true)}
      />

      {storeState.error ? <p className="error-banner">{storeState.error}</p> : null}
    </main>
  )
}
