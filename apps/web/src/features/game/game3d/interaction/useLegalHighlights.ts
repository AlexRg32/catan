import { useMemo } from 'react'
import type { GameProjection } from '../../../../lib/types'

export type LegalHighlights = {
  road: Set<number>
  settlement: Set<number>
  city: Set<number>
  robber: Set<number>
  setupSettlement: Set<number>
  setupRoad: Set<number>
  canRoll: boolean
  canEndTurn: boolean
  canBuyDev: boolean
}

export function useLegalHighlights(projection: GameProjection | null): LegalHighlights {
  return useMemo(() => {
    const actions = projection?.legalActions ?? []
    const find = (actionType: string) => actions.find((action) => action.actionType === actionType)

    const road = new Set(find('build_road')?.allowedEdgeIndexes ?? [])
    const settlement = new Set(find('build_settlement')?.allowedNodeIndexes ?? [])
    const city = new Set(find('build_city')?.allowedNodeIndexes ?? [])
    const robber = new Set(find('move_robber')?.allowedHexIndexes ?? [])
    const setupSettlement = new Set(find('setup_place_settlement')?.allowedNodeIndexes ?? [])
    const setupRoad = new Set(find('setup_place_road')?.allowedEdgeIndexes ?? [])

    return {
      road,
      settlement,
      city,
      robber,
      setupSettlement,
      setupRoad,
      canRoll: Boolean(find('roll_dice')?.enabled),
      canEndTurn: Boolean(find('end_turn')?.enabled),
      canBuyDev: Boolean(find('buy_dev_card')?.enabled),
    }
  }, [projection])
}
