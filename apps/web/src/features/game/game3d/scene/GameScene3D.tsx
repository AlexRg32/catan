import { OrbitControls, Text } from '@react-three/drei'
import { Canvas } from '@react-three/fiber'
import { useGesture } from '@use-gesture/react'
import { useMemo, useState } from 'react'
import type { GameProjection } from '../../../../lib/types'
import type { DragPiece } from '../interaction/useDragPlacement'
import type { PickedTarget } from '../interaction/useBoardPicking'
import type { LegalHighlights } from '../interaction/useLegalHighlights'
import { projectionToScene } from '../adapters/projectionToScene'
import { HighlightLayer } from './HighlightLayer'

type GameScene3DProps = {
  projection: GameProjection | null
  highlights: LegalHighlights
  setupMode: boolean
  activeDragPiece: DragPiece | null
  previewTarget: PickedTarget | null
  onHoverTarget: (target: PickedTarget | null) => void
  onSelectTarget: (target: PickedTarget) => void
  onMoveRobber: (hexIndex: number) => void
}

const TERRAIN_COLORS: Record<string, string> = {
  WOOD: '#4f8c4b',
  WOOL: '#8fbe64',
  GRAIN: '#d8b24c',
  CLAY: '#b56b45',
  ORE: '#7d7f91',
  DESERT: '#d6c396',
}

function resolveTerrainColor(terrain: string): string {
  return TERRAIN_COLORS[terrain] ?? '#cdb893'
}

export function GameScene3D({
  projection,
  highlights,
  setupMode,
  activeDragPiece,
  previewTarget,
  onHoverTarget,
  onSelectTarget,
  onMoveRobber,
}: GameScene3DProps) {
  const scene = useMemo(() => projectionToScene(projection), [projection])
  const [cursor, setCursor] = useState<[number, number]>([0, 0])

  const bind = useGesture({
    onMove: ({ xy }) => {
      setCursor([xy[0], xy[1]])
    },
  })

  const allowedRobberHexes = highlights.robber

  return (
    <section className="panel game3d-panel">
      <div className="game3d-titlebar">
        <h2>Tablero 3D (beta)</h2>
        <p>
          {setupMode
            ? 'Setup guiado: arrastra poblado y luego carretera sobre objetivos resaltados.'
            : 'Arrastra piezas desde el dock o usa click directo en objetivos válidos.'}
        </p>
      </div>
      <div className="game3d-canvas-wrap" {...bind()}>
        <Canvas camera={{ position: [0, 10, 14], fov: 45 }}>
          <ambientLight intensity={0.8} />
          <directionalLight intensity={1.2} position={[10, 20, 8]} />
          <group>
            {scene.hexes.map((hex) => {
              const isRobberMoveTarget = allowedRobberHexes.has(hex.hexIndex)
              return (
                <group key={hex.hexIndex} position={hex.position}>
                  <mesh
                    onPointerOver={() => onHoverTarget({ kind: 'hex', index: hex.hexIndex })}
                    onPointerOut={() => onHoverTarget(null)}
                    onClick={() => {
                      if (isRobberMoveTarget) onMoveRobber(hex.hexIndex)
                      onSelectTarget({ kind: 'hex', index: hex.hexIndex })
                    }}
                  >
                    <cylinderGeometry args={[1.2, 1.2, 0.15, 6]} />
                    <meshStandardMaterial
                      color={isRobberMoveTarget ? '#ffd166' : resolveTerrainColor(hex.terrain)}
                    />
                  </mesh>
                  {hex.numberToken ? (
                    <Text
                      position={[0, 0.18, 0]}
                      fontSize={0.26}
                      color="#2a241a"
                      anchorX="center"
                      anchorY="middle"
                    >
                      {String(hex.numberToken)}
                    </Text>
                  ) : null}
                  {hex.hasRobber ? (
                    <mesh position={[0, 0.32, 0]}>
                      <sphereGeometry args={[0.18, 20, 20]} />
                      <meshStandardMaterial color="#5b4b3f" />
                    </mesh>
                  ) : null}
                </group>
              )
            })}

            {scene.ports.map((port) => (
              <group key={`port-${port.portIndex}`} position={port.position}>
                <mesh>
                  <cylinderGeometry args={[0.36, 0.36, 0.06, 18]} />
                  <meshStandardMaterial color="#f0d8a5" />
                </mesh>
                <Text
                  position={[0, 0.06, 0]}
                  fontSize={0.14}
                  color="#3f3323"
                  anchorX="center"
                  anchorY="middle"
                >
                  {port.resourceType ? `${port.resourceType} ${port.ratio}:1` : `${port.ratio}:1`}
                </Text>
              </group>
            ))}

            {scene.edges.map((edge) => {
              const isOwned = Boolean(edge.ownerPlayerId)
              const isAllowed = setupMode
                ? highlights.setupRoad.has(edge.edgeIndex)
                : highlights.road.has(edge.edgeIndex)
              const color = isOwned ? '#1d4ed8' : isAllowed ? '#f59e0b' : '#c7b8a0'
              return (
                <mesh
                  key={edge.edgeIndex}
                  position={edge.midpoint}
                  rotation={[0, -edge.angleY, 0]}
                  onPointerOver={() => onHoverTarget({ kind: 'edge', index: edge.edgeIndex })}
                  onPointerOut={() => onHoverTarget(null)}
                  onClick={() => onSelectTarget({ kind: 'edge', index: edge.edgeIndex })}
                >
                  <boxGeometry args={[Math.max(edge.length - 0.05, 0.1), 0.08, 0.16]} />
                  <meshStandardMaterial color={color} />
                </mesh>
              )
            })}

            {scene.nodes.map((node) => {
              const isSettlementTarget = setupMode
                ? highlights.setupSettlement.has(node.nodeIndex)
                : highlights.settlement.has(node.nodeIndex)
              const isCityTarget = !setupMode && highlights.city.has(node.nodeIndex)
              const color = node.ownerPlayerId
                ? node.buildingType === 'CITY'
                  ? '#2563eb'
                  : '#60a5fa'
                : isCityTarget
                  ? '#ef4444'
                  : isSettlementTarget
                    ? '#22c55e'
                    : '#a59076'
              return (
                <mesh
                  key={node.nodeIndex}
                  position={[node.position[0], node.position[1] + 0.12, node.position[2]]}
                  onPointerOver={() => onHoverTarget({ kind: 'node', index: node.nodeIndex })}
                  onPointerOut={() => onHoverTarget(null)}
                  onClick={() => onSelectTarget({ kind: 'node', index: node.nodeIndex })}
                >
                  <cylinderGeometry
                    args={[0.13, 0.13, node.buildingType === 'CITY' ? 0.35 : 0.22, 16]}
                  />
                  <meshStandardMaterial color={color} />
                </mesh>
              )
            })}

            <HighlightLayer
              scene={scene}
              highlights={highlights}
              setupMode={setupMode}
              activePiece={activeDragPiece}
              hoveredTarget={previewTarget}
            />
          </group>
          <OrbitControls
            makeDefault
            minDistance={8}
            maxDistance={26}
            maxPolarAngle={Math.PI / 2.1}
          />
        </Canvas>

        {activeDragPiece ? (
          <div className="drag-indicator" style={{ left: cursor[0], top: cursor[1] }}>
            Arrastrando {activeDragPiece}
          </div>
        ) : null}
      </div>
    </section>
  )
}
