import { apiClient } from '../api/client'
import { GameStore } from '../state/gameStore'

export async function resyncGameState(
  accessToken: string,
  gameId: string,
  store: GameStore,
): Promise<void> {
  const snapshot = await apiClient.getGameSnapshot(accessToken, gameId)
  store.applySnapshot(snapshot)

  const delta = await apiClient.getGameEvents(accessToken, gameId, snapshot.lastSequence)
  store.applyDelta(delta)
}
