import type { GameEventsResponse, GameEventResponse, GameProjection, GameSnapshotResponse } from '../types'

type StoreState = {
  connected: boolean
  lastSequence: number
  snapshot: GameProjection | null
  events: GameEventResponse[]
  error: string | null
}

type Listener = () => void

export class GameStore {
  private state: StoreState = {
    connected: false,
    lastSequence: 0,
    snapshot: null,
    events: [],
    error: null,
  }

  private listeners = new Set<Listener>()

  subscribe(listener: Listener): () => void {
    this.listeners.add(listener)
    return () => this.listeners.delete(listener)
  }

  getState(): StoreState {
    return this.state
  }

  setConnected(connected: boolean): void {
    this.state = { ...this.state, connected }
    this.notify()
  }

  setError(error: string | null): void {
    this.state = { ...this.state, error }
    this.notify()
  }

  applySnapshot(snapshot: GameSnapshotResponse): void {
    this.state = {
      ...this.state,
      snapshot: snapshot.state,
      lastSequence: snapshot.lastSequence,
      events: [],
      error: null,
    }
    this.notify()
  }

  applyDelta(delta: GameEventsResponse): void {
    if (delta.events.length === 0) {
      return
    }

    const deduped = new Map<number, GameEventResponse>()
    for (const event of this.state.events) {
      deduped.set(event.seq, event)
    }
    for (const event of delta.events) {
      deduped.set(event.seq, event)
    }

    this.state = {
      ...this.state,
      lastSequence: delta.lastSequence,
      events: Array.from(deduped.values()).sort((a, b) => a.seq - b.seq),
    }
    this.notify()
  }

  private notify(): void {
    for (const listener of this.listeners) {
      listener()
    }
  }
}

export function createGameStore(): GameStore {
  return new GameStore()
}
