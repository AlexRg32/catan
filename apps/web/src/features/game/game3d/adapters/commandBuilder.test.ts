import { describe, expect, it } from 'vitest'
import {
  buildCityCommand,
  buildRoadCommand,
  buildSettlementCommand,
  buyDevCardCommand,
  discardResourcesCommand,
  endTurnCommand,
  maritimeTradeCommand,
  moveRobberCommand,
  oneResource,
  playKnightCommand,
  playProgressCommand,
  proposeTradeCommand,
  rollDiceCommand,
  setupRoadCommand,
  setupSettlementCommand,
} from './commandBuilder'

describe('commandBuilder', () => {
  it('builds simple commands with empty payload', () => {
    expect(rollDiceCommand()).toEqual({ type: 'roll_dice', payload: {} })
    expect(endTurnCommand()).toEqual({ type: 'end_turn', payload: {} })
    expect(buyDevCardCommand()).toEqual({ type: 'buy_dev_card', payload: {} })
    expect(playKnightCommand()).toEqual({ type: 'play_dev_knight', payload: {} })
  })

  it('builds build/setup commands with indexes', () => {
    expect(buildRoadCommand(8)).toEqual({ type: 'build_road', payload: { edgeIndex: 8 } })
    expect(buildSettlementCommand(12)).toEqual({ type: 'build_settlement', payload: { nodeIndex: 12 } })
    expect(buildCityCommand(4)).toEqual({ type: 'build_city', payload: { nodeIndex: 4 } })
    expect(setupSettlementCommand(7)).toEqual({ type: 'setup_place_settlement', payload: { nodeIndex: 7 } })
    expect(setupRoadCommand(16)).toEqual({ type: 'setup_place_road', payload: { edgeIndex: 16 } })
  })

  it('builds robber command with optional target player', () => {
    expect(moveRobberCommand(5)).toEqual({
      type: 'move_robber',
      payload: { targetHexIndex: 5, targetPlayerId: null },
    })
    expect(moveRobberCommand(9, 'player-1')).toEqual({
      type: 'move_robber',
      payload: { targetHexIndex: 9, targetPlayerId: 'player-1' },
    })
  })

  it('sanitizes discard payload and helper resource maps', () => {
    expect(discardResourcesCommand({ WOOD: 2, ORE: 0, GRAIN: -1 })).toEqual({
      type: 'discard_resources',
      payload: { cards: { WOOD: 2 } },
    })
    expect(oneResource('CLAY', 2)).toEqual({ CLAY: 2 })
    expect(oneResource('CLAY', 0)).toEqual({})
  })

  it('builds trade and progress payloads', () => {
    expect(maritimeTradeCommand(3, 'WOOL', 'ORE')).toEqual({
      type: 'maritime_trade',
      payload: { ratio: 3, giveType: 'WOOL', giveCount: 3, getType: 'ORE' },
    })

    expect(
      proposeTradeCommand(['p2'], { WOOD: 1 }, { ORE: 1 }),
    ).toEqual({
      type: 'propose_trade',
      payload: { toPlayerIds: ['p2'], give: { WOOD: 1 }, want: { ORE: 1 } },
    })

    expect(playProgressCommand('MONOPOLY', { resourceType: 'GRAIN' })).toEqual({
      type: 'play_dev_progress',
      payload: { cardType: 'MONOPOLY', resourceType: 'GRAIN' },
    })
  })
})
