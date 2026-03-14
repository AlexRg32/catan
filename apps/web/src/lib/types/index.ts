export type User = {
  id: string
  email: string
  nickname: string
}

export type AuthResponse = {
  user: User
  accessToken: string
}

export type RoomSeat = {
  seatIndex: number
  userId: string
  nickname: string
  ready: boolean
}

export type RoomResponse = {
  roomId: string
  roomCode: string
  roomName: string
  status: 'WAITING' | 'IN_GAME'
  hostUserId: string
  maxPlayers: number
  seats: RoomSeat[]
}

export type StartGameResponse = {
  gameId: string
}

export type GamePlayerProjection = {
  playerId: string
  self: boolean
  visibleVictoryPoints: number
  totalVictoryPoints: number
  resourceCount: number
  resources: Record<string, number> | null
  devCardCount: number
  devCards: Record<string, number> | null
  playedKnights: number
  hasLongestRoad: boolean
  hasLargestArmy: boolean
  hiddenVictoryPointCards: number | null
}

export type BoardHexProjection = {
  hexIndex: number
  terrain: string
  numberToken: number | null
  hasRobber: boolean
  x: number
  y: number
  z: number
}

export type BoardIntersectionProjection = {
  nodeIndex: number
  adjacentHexIndexes: number[]
  adjacentNodeIndexes: number[]
  ownerPlayerId: string | null
  buildingType: 'SETTLEMENT' | 'CITY' | null
  x: number
  y: number
  z: number
}

export type BoardEdgeProjection = {
  edgeIndex: number
  nodeA: number
  nodeB: number
  ownerPlayerId: string | null
  x1: number
  y1: number
  z1: number
  x2: number
  y2: number
  z2: number
  adjacentHexIndexes: number[]
}

export type BoardPortProjection = {
  portIndex: number
  edgeIndex: number
  ratio: number
  resourceType: string | null
}

export type BoardProjection = {
  hexes: BoardHexProjection[]
  intersections: BoardIntersectionProjection[]
  edges: BoardEdgeProjection[]
  ports: BoardPortProjection[]
}

export type LegalActionProjection = {
  actionType: string
  enabled: boolean
  allowedNodeIndexes: number[]
  allowedEdgeIndexes: number[]
  allowedHexIndexes: number[]
}

export type GameProjection = {
  gameId: string
  phase: string
  specialFlow: string
  activePlayerId: string
  turnNumber: number
  lastRoll: number
  setupCompleted: boolean
  finished: boolean
  winnerPlayerId: string | null
  lastSequence: number
  board?: BoardProjection
  legalActions?: LegalActionProjection[]
  players: GamePlayerProjection[]
}

export type GameSnapshotResponse = {
  gameId: string
  lastSequence: number
  state: GameProjection
}

export type GameEventResponse = {
  seq: number
  type: string
  actorPlayerId: string | null
  payload: string
  createdAt: string
}

export type GameEventsResponse = {
  gameId: string
  fromSequence: number
  lastSequence: number
  events: GameEventResponse[]
}
