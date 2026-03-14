import { useState } from 'react'
import type { GameProjection } from '../../../lib/types'
import { BuildingsLayer } from './BuildingsLayer'
import { RoadsLayer } from './RoadsLayer'
import { loadAssetAtlas } from './assetLoader'
import { renderBoardDebugOverlay } from './debugOverlay'

type BoardRendererProps = {
  projection: GameProjection | null
}

const HEX_IDS = [
  'A1',
  'A2',
  'A3',
  'B1',
  'B2',
  'B3',
  'B4',
  'C1',
  'C2',
  'C3',
  'C4',
  'C5',
  'D1',
  'D2',
  'D3',
  'D4',
  'E1',
  'E2',
  'E3',
]

export function BoardRenderer({ projection }: BoardRendererProps) {
  const [debug, setDebug] = useState(false)
  const atlas = loadAssetAtlas()

  return (
    <section className="board-panel panel">
      <div className="topbar">
        <h2>Tablero</h2>
        <button className="ghost" onClick={() => setDebug((value) => !value)}>
          {debug ? 'Ocultar overlay' : 'Mostrar overlay'}
        </button>
      </div>
      <div className="board-hex-grid" role="img" aria-label="Tablero de Catan">
        {HEX_IDS.map((hexId, index) => {
          const terrainKey = ['WOOD', 'WOOL', 'GRAIN', 'CLAY', 'ORE', 'DESERT'][index % 6]
          return (
            <div key={hexId} className="hex-cell">
              <img src={atlas.terrain[terrainKey]} alt="" />
              <span>{hexId}</span>
            </div>
          )
        })}
      </div>
      <div className="roads-layer">
        {Object.entries(atlas.harbor).map(([key, image]) => (
          <span key={key} className="road-chip">
            <img src={image} alt="" width={18} height={18} /> {key}
          </span>
        ))}
      </div>
      {renderBoardDebugOverlay(debug)}
      <div>
        <RoadsLayer projection={projection} />
        <BuildingsLayer projection={projection} />
      </div>
    </section>
  )
}
