import { describe, expect, it } from 'vitest'
import { createGameStore } from './gameStore'

describe('gameStore', () => {
  it('applies snapshot then delta in sequence order', () => {
    const store = createGameStore()

    store.applySnapshot({
      gameId: 'g1',
      lastSequence: 4,
      state: {
        gameId: 'g1',
        phase: 'TRADING',
        specialFlow: 'NONE',
        activePlayerId: 'p1',
        turnNumber: 1,
        lastRoll: 8,
        setupCompleted: true,
        finished: false,
        winnerPlayerId: null,
        lastSequence: 4,
        players: [],
      },
    })

    store.applyDelta({
      gameId: 'g1',
      fromSequence: 4,
      lastSequence: 6,
      events: [
        { seq: 6, type: 'end_turn', actorPlayerId: 'p1', payload: '{}', createdAt: new Date().toISOString() },
        { seq: 5, type: 'roll_dice', actorPlayerId: 'p1', payload: '{}', createdAt: new Date().toISOString() },
      ],
    })

    const state = store.getState()
    expect(state.lastSequence).toBe(6)
    expect(state.events.map((event) => event.seq)).toEqual([5, 6])
  })

  it('keeps deduplicated events by sequence', () => {
    const store = createGameStore()
    store.applyDelta({
      gameId: 'g1',
      fromSequence: 0,
      lastSequence: 1,
      events: [{ seq: 1, type: 'roll_dice', actorPlayerId: 'p1', payload: '{}', createdAt: new Date().toISOString() }],
    })

    store.applyDelta({
      gameId: 'g1',
      fromSequence: 1,
      lastSequence: 1,
      events: [{ seq: 1, type: 'roll_dice', actorPlayerId: 'p1', payload: '{}', createdAt: new Date().toISOString() }],
    })

    expect(store.getState().events).toHaveLength(1)
  })
})
