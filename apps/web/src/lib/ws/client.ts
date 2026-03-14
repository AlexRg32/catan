import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs'

export type CommandEnvelope = {
  commandId: string
  gameId: string
  type: string
  sentAt: string
  payload: Record<string, unknown>
}

type GameWsClientOptions = {
  wsUrl: string
  accessToken: string
  onAck: (ack: Record<string, unknown>) => void
  onError?: (message: string) => void
}

export class GameWsClient {
  private readonly options: GameWsClientOptions
  private stompClient: Client | null = null
  private ackSubscription: StompSubscription | null = null
  private reconnectAttempts = 0
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null
  private closed = false

  constructor(options: GameWsClientOptions) {
    this.options = options
  }

  connect(): void {
    this.closed = false
    this.initializeClient()
    this.stompClient?.activate()
  }

  disconnect(): void {
    this.closed = true
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    this.ackSubscription?.unsubscribe()
    this.ackSubscription = null
    this.stompClient?.deactivate()
    this.stompClient = null
  }

  send(command: CommandEnvelope): void {
    if (!this.stompClient?.connected) {
      throw new Error('WebSocket is not connected')
    }

    this.stompClient.publish({
      destination: '/app/game.command',
      body: JSON.stringify(command),
    })
  }

  private initializeClient(): void {
    this.stompClient = new Client({
      brokerURL: this.options.wsUrl,
      connectHeaders: {
        Authorization: `Bearer ${this.options.accessToken}`,
      },
      reconnectDelay: 0,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        this.reconnectAttempts = 0
        this.ackSubscription?.unsubscribe()
        this.ackSubscription = this.stompClient?.subscribe('/user/queue/ack', (message) => {
          this.handleAck(message)
        }) ?? null
      },
      onStompError: (frame) => {
        this.options.onError?.(frame.body || 'STOMP broker error')
      },
      onWebSocketClose: () => {
        if (!this.closed) {
          this.scheduleReconnect()
        }
      },
      onWebSocketError: () => {
        this.options.onError?.('WebSocket transport error')
      },
    })
  }

  private handleAck(message: IMessage): void {
    try {
      const parsed = JSON.parse(message.body) as Record<string, unknown>
      this.options.onAck(parsed)
    } catch {
      this.options.onError?.('Invalid ACK payload')
    }
  }

  private scheduleReconnect(): void {
    if (this.reconnectTimer) {
      return
    }

    const delayMs = Math.min(1000 * 2 ** this.reconnectAttempts, 10000)
    this.reconnectAttempts += 1

    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = null
      if (!this.closed) {
        this.initializeClient()
        this.stompClient?.activate()
      }
    }, delayMs)
  }
}
