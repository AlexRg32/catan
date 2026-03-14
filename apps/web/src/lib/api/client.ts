import type {
  AuthResponse,
  GameEventsResponse,
  GameSnapshotResponse,
  RoomResponse,
  StartGameResponse,
} from '../types'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''
const NORMALIZED_API_BASE_URL =
  API_BASE_URL.endsWith('/') && API_BASE_URL.length > 1
    ? API_BASE_URL.slice(0, -1)
    : API_BASE_URL

async function request<T>(
  path: string,
  options: RequestInit = {},
  accessToken?: string,
): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')
  if (accessToken) {
    headers.set('Authorization', `Bearer ${accessToken}`)
  }

  const response = await fetch(`${NORMALIZED_API_BASE_URL}${path}`, {
    ...options,
    headers,
  })

  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || `Request failed (${response.status})`)
  }

  if (response.status === 204) {
    return {} as T
  }

  return (await response.json()) as T
}

export const apiClient = {
  register(email: string, password: string, nickname: string): Promise<AuthResponse> {
    return request<AuthResponse>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({ email, password, nickname }),
    })
  },

  login(email: string, password: string): Promise<AuthResponse> {
    return request<AuthResponse>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    })
  },

  me(accessToken: string): Promise<{ id: string; email: string; nickname: string }> {
    return request('/api/auth/me', { method: 'GET' }, accessToken)
  },

  createRoom(accessToken: string, roomName?: string): Promise<RoomResponse> {
    return request<RoomResponse>(
      '/api/rooms',
      {
        method: 'POST',
        body: JSON.stringify(roomName ? { name: roomName } : {}),
      },
      accessToken,
    )
  },

  joinRoom(accessToken: string, roomCode: string): Promise<RoomResponse> {
    return request<RoomResponse>(
      `/api/rooms/${encodeURIComponent(roomCode)}/join`,
      { method: 'POST' },
      accessToken,
    )
  },

  setReady(accessToken: string, roomCode: string, ready: boolean): Promise<RoomResponse> {
    return request<RoomResponse>(
      `/api/rooms/${encodeURIComponent(roomCode)}/ready`,
      {
        method: 'POST',
        body: JSON.stringify({ ready }),
      },
      accessToken,
    )
  },

  startGame(accessToken: string, roomCode: string): Promise<StartGameResponse> {
    return request<StartGameResponse>(
      `/api/rooms/${encodeURIComponent(roomCode)}/start`,
      { method: 'POST' },
      accessToken,
    )
  },

  getRoom(accessToken: string, roomCode: string): Promise<RoomResponse> {
    return request<RoomResponse>(
      `/api/rooms/${encodeURIComponent(roomCode)}`,
      { method: 'GET' },
      accessToken,
    )
  },

  getGameSnapshot(accessToken: string, gameId: string): Promise<GameSnapshotResponse> {
    return request<GameSnapshotResponse>(
      `/api/games/${encodeURIComponent(gameId)}`,
      { method: 'GET' },
      accessToken,
    )
  },

  getGameEvents(
    accessToken: string,
    gameId: string,
    fromSeq: number,
  ): Promise<GameEventsResponse> {
    return request<GameEventsResponse>(
      `/api/games/${encodeURIComponent(gameId)}/events?fromSeq=${fromSeq}`,
      { method: 'GET' },
      accessToken,
    )
  },
}
