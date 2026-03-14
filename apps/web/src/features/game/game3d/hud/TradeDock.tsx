import { useMemo, useState } from 'react'
import type { GameProjection } from '../../../../lib/types'
import {
  oneResource,
  proposeTradeCommand,
  type ResourceType,
} from '../adapters/commandBuilder'

type TradeDockProps = {
  projection: GameProjection | null
  onSendCommand: (type: string, payload: Record<string, unknown>) => void
  onOpenMaritime: () => void
}

const RESOURCE_OPTIONS: ResourceType[] = ['WOOD', 'WOOL', 'GRAIN', 'CLAY', 'ORE']
const LABELS: Record<ResourceType, string> = {
  WOOD: 'Madera',
  WOOL: 'Lana',
  GRAIN: 'Trigo',
  CLAY: 'Arcilla',
  ORE: 'Mineral',
}

export function TradeDock({ projection, onSendCommand, onOpenMaritime }: TradeDockProps) {
  const [giveType, setGiveType] = useState<ResourceType>('WOOD')
  const [wantType, setWantType] = useState<ResourceType>('GRAIN')
  const [giveCount, setGiveCount] = useState(1)
  const [wantCount, setWantCount] = useState(1)

  const targetIds = useMemo(
    () => (projection?.players ?? []).filter((player) => !player.self).map((player) => player.playerId),
    [projection],
  )

  return (
    <section className="panel">
      <h3>Comercio</h3>
      <div className="trade-grid">
        <div className="trade-block">
          <p className="muted-inline">Ofrezco</p>
          <div className="resource-chip-grid">
            {RESOURCE_OPTIONS.map((resource) => (
              <button
                key={`give-${resource}`}
                className={giveType === resource ? 'trade-chip trade-chip--active' : 'trade-chip ghost'}
                onClick={() => setGiveType(resource)}
                type="button"
              >
                {LABELS[resource]}
              </button>
            ))}
          </div>
          <div className="stepper-row">
            <button className="ghost" type="button" onClick={() => setGiveCount((value) => Math.max(1, value - 1))}>
              -
            </button>
            <strong className="stepper-count">{giveCount}</strong>
            <button className="ghost" type="button" onClick={() => setGiveCount((value) => Math.min(4, value + 1))}>
              +
            </button>
          </div>
        </div>
        <div className="trade-block">
          <p className="muted-inline">Pido</p>
          <div className="resource-chip-grid">
            {RESOURCE_OPTIONS.map((resource) => (
              <button
                key={`want-${resource}`}
                className={wantType === resource ? 'trade-chip trade-chip--active' : 'trade-chip ghost'}
                onClick={() => setWantType(resource)}
                type="button"
              >
                {LABELS[resource]}
              </button>
            ))}
          </div>
          <div className="stepper-row">
            <button className="ghost" type="button" onClick={() => setWantCount((value) => Math.max(1, value - 1))}>
              -
            </button>
            <strong className="stepper-count">{wantCount}</strong>
            <button className="ghost" type="button" onClick={() => setWantCount((value) => Math.min(4, value + 1))}>
              +
            </button>
          </div>
        </div>
      </div>
      <div className="room-actions">
        <button
          onClick={() => {
            const command = proposeTradeCommand(
              targetIds,
              oneResource(giveType, giveCount),
              oneResource(wantType, wantCount),
            )
            onSendCommand(command.type, command.payload)
          }}
          disabled={targetIds.length === 0 || giveType === wantType}
        >
          Proponer intercambio
        </button>
        <button className="ghost" onClick={onOpenMaritime}>
          Comercio marítimo
        </button>
      </div>
    </section>
  )
}
