import { useCallback, useMemo, useState } from 'react'
import type { PickedTarget } from './useBoardPicking'
import type { LegalHighlights } from './useLegalHighlights'

export type DragPiece = 'road' | 'settlement' | 'city'
export type SetupStep = 'SETTLEMENT' | 'ROAD' | null

type UseDragPlacementOptions = {
  highlights: LegalHighlights
  setupMode: boolean
  setupStep: SetupStep
  onBuildRoad: (edgeIndex: number, setupMode: boolean) => void
  onBuildSettlement: (nodeIndex: number, setupMode: boolean) => void
  onBuildCity: (nodeIndex: number) => void
  onInvalidAction?: (message: string) => void
}

export function useDragPlacement({
  highlights,
  setupMode,
  setupStep,
  onBuildRoad,
  onBuildSettlement,
  onBuildCity,
  onInvalidAction,
}: UseDragPlacementOptions) {
  const [activePiece, setActivePiece] = useState<DragPiece | null>(null)
  const [hoveredTarget, setHoveredTarget] = useState<PickedTarget | null>(null)

  const canDropOnTarget = useCallback(
    (piece: DragPiece, target: PickedTarget | null): boolean => {
      if (!target) return false

      if (piece === 'road' && target.kind === 'edge') {
        return setupMode ? highlights.setupRoad.has(target.index) : highlights.road.has(target.index)
      }

      if (piece === 'settlement' && target.kind === 'node') {
        return setupMode
          ? highlights.setupSettlement.has(target.index)
          : highlights.settlement.has(target.index)
      }

      if (piece === 'city' && target.kind === 'node' && !setupMode) {
        return highlights.city.has(target.index)
      }

      return false
    },
    [highlights.city, highlights.road, highlights.settlement, highlights.setupRoad, highlights.setupSettlement, setupMode],
  )

  const startDrag = useCallback(
    (piece: DragPiece) => {
      if (setupMode && setupStep === 'SETTLEMENT' && piece !== 'settlement') {
        onInvalidAction?.('En setup debes colocar primero un poblado.')
        return
      }
      if (setupMode && setupStep === 'ROAD' && piece !== 'road') {
        onInvalidAction?.('En setup debes colocar ahora una carretera conectada.')
        return
      }
      if (setupMode && piece === 'city') {
        onInvalidAction?.('La ciudad no se puede colocar durante setup.')
        return
      }
      setActivePiece(piece)
    },
    [onInvalidAction, setupMode, setupStep],
  )

  const cancelDrag = useCallback(() => {
    setActivePiece(null)
    setHoveredTarget(null)
  }, [])

  const setHoverTarget = useCallback((target: PickedTarget | null) => {
    setHoveredTarget(target)
  }, [])

  const dropOn = useCallback(
    (target: PickedTarget | null): boolean => {
      if (!activePiece || !target) {
        return false
      }
      if (!canDropOnTarget(activePiece, target)) {
        onInvalidAction?.('Objetivo no válido para la pieza seleccionada.')
        return false
      }

      if (activePiece === 'road' && target.kind === 'edge') {
        onBuildRoad(target.index, setupMode)
      } else if (activePiece === 'settlement' && target.kind === 'node') {
        onBuildSettlement(target.index, setupMode)
      } else if (activePiece === 'city' && target.kind === 'node') {
        onBuildCity(target.index)
      }

      setActivePiece(null)
      setHoveredTarget(null)
      return true
    },
    [activePiece, canDropOnTarget, onBuildCity, onBuildRoad, onBuildSettlement, onInvalidAction, setupMode],
  )

  const previewTarget = useMemo(() => {
    if (!activePiece) {
      return null
    }
    if (!hoveredTarget) {
      return null
    }
    return canDropOnTarget(activePiece, hoveredTarget) ? hoveredTarget : null
  }, [activePiece, canDropOnTarget, hoveredTarget])

  return {
    activePiece,
    hoveredTarget,
    previewTarget,
    startDrag,
    cancelDrag,
    setHoverTarget,
    canDropOnTarget,
    dropOn,
    setupStep,
  }
}
