import type { SceneBoard } from '../adapters/projectionToScene'
import type { DragPiece } from '../interaction/useDragPlacement'
import type { PickedTarget } from '../interaction/useBoardPicking'
import type { LegalHighlights } from '../interaction/useLegalHighlights'

type HighlightLayerProps = {
  scene: SceneBoard
  highlights: LegalHighlights
  setupMode: boolean
  activePiece: DragPiece | null
  hoveredTarget: PickedTarget | null
}

export function HighlightLayer({
  scene,
  highlights,
  setupMode,
  activePiece,
  hoveredTarget,
}: HighlightLayerProps) {
  const roadSet = setupMode ? highlights.setupRoad : highlights.road
  const settlementSet = setupMode ? highlights.setupSettlement : highlights.settlement

  return (
    <group>
      {scene.edges.map((edge) => {
        const isLegal = roadSet.has(edge.edgeIndex)
        const isHovered = hoveredTarget?.kind === 'edge' && hoveredTarget.index === edge.edgeIndex
        if (!isLegal) return null
        return (
          <mesh key={`hl-edge-${edge.edgeIndex}`} position={[edge.midpoint[0], edge.midpoint[1] + 0.04, edge.midpoint[2]]} rotation={[0, -edge.angleY, 0]}>
            <boxGeometry args={[Math.max(edge.length - 0.05, 0.1), 0.01, 0.2]} />
            <meshStandardMaterial
              color={activePiece === 'road' && isHovered ? '#f97316' : '#fbbf24'}
              emissive={activePiece === 'road' && isHovered ? '#f97316' : '#fbbf24'}
              emissiveIntensity={0.35}
              transparent
              opacity={0.6}
            />
          </mesh>
        )
      })}

      {scene.nodes.map((node) => {
        const isLegalSettlement = settlementSet.has(node.nodeIndex)
        const isLegalCity = !setupMode && highlights.city.has(node.nodeIndex)
        if (!isLegalSettlement && !isLegalCity) return null
        const isHovered = hoveredTarget?.kind === 'node' && hoveredTarget.index === node.nodeIndex
        const isMatchingDrag =
          (activePiece === 'settlement' && isLegalSettlement) || (activePiece === 'city' && isLegalCity)

        return (
          <mesh key={`hl-node-${node.nodeIndex}`} position={[node.position[0], node.position[1] + 0.05, node.position[2]]}>
            <sphereGeometry args={[isHovered ? 0.2 : 0.16, 16, 16]} />
            <meshStandardMaterial
              color={isLegalCity ? '#ef4444' : '#22c55e'}
              emissive={isMatchingDrag && isHovered ? '#ffffff' : '#000000'}
              emissiveIntensity={isMatchingDrag && isHovered ? 0.5 : 0}
              transparent
              opacity={0.45}
            />
          </mesh>
        )
      })}
    </group>
  )
}
