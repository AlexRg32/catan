export type ResourceType = 'WOOD' | 'WOOL' | 'GRAIN' | 'CLAY' | 'ORE'

export type CommandSpec = {
  type: string
  payload: Record<string, unknown>
}

export function rollDiceCommand(): CommandSpec {
  return { type: 'roll_dice', payload: {} }
}

export function endTurnCommand(): CommandSpec {
  return { type: 'end_turn', payload: {} }
}

export function buyDevCardCommand(): CommandSpec {
  return { type: 'buy_dev_card', payload: {} }
}

export function playKnightCommand(): CommandSpec {
  return { type: 'play_dev_knight', payload: {} }
}

export function playProgressCommand(cardType: 'ROAD_BUILDING' | 'YEAR_OF_PLENTY' | 'MONOPOLY', payload: Record<string, unknown>): CommandSpec {
  return { type: 'play_dev_progress', payload: { cardType, ...payload } }
}

export function buildRoadCommand(edgeIndex: number): CommandSpec {
  return { type: 'build_road', payload: { edgeIndex } }
}

export function buildSettlementCommand(nodeIndex: number): CommandSpec {
  return { type: 'build_settlement', payload: { nodeIndex } }
}

export function buildCityCommand(nodeIndex: number): CommandSpec {
  return { type: 'build_city', payload: { nodeIndex } }
}

export function setupSettlementCommand(nodeIndex: number): CommandSpec {
  return { type: 'setup_place_settlement', payload: { nodeIndex } }
}

export function setupRoadCommand(edgeIndex: number): CommandSpec {
  return { type: 'setup_place_road', payload: { edgeIndex } }
}

export function moveRobberCommand(targetHexIndex: number, targetPlayerId?: string | null): CommandSpec {
  return { type: 'move_robber', payload: { targetHexIndex, targetPlayerId: targetPlayerId ?? null } }
}

export function discardResourcesCommand(cards: Partial<Record<ResourceType, number>>): CommandSpec {
  const sanitized: Record<string, number> = {}
  for (const [resource, amount] of Object.entries(cards)) {
    const parsed = Number(amount ?? 0)
    if (parsed > 0) {
      sanitized[resource] = parsed
    }
  }
  return { type: 'discard_resources', payload: { cards: sanitized } }
}

export function maritimeTradeCommand(ratio: 4 | 3 | 2, giveType: ResourceType, getType: ResourceType): CommandSpec {
  return {
    type: 'maritime_trade',
    payload: {
      ratio,
      giveType,
      giveCount: ratio,
      getType,
    },
  }
}

export function proposeTradeCommand(
  toPlayerIds: string[],
  give: Partial<Record<ResourceType, number>>,
  want: Partial<Record<ResourceType, number>>,
): CommandSpec {
  return {
    type: 'propose_trade',
    payload: {
      toPlayerIds,
      give,
      want,
    },
  }
}

export function oneResource(type: ResourceType, amount: number): Partial<Record<ResourceType, number>> {
  return amount > 0 ? { [type]: amount } : {}
}
