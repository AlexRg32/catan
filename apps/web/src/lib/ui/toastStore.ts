export type ToastKind = 'error' | 'info'

export type ToastMessage = {
  id: string
  kind: ToastKind
  message: string
}

type Listener = (messages: ToastMessage[]) => void

const listeners = new Set<Listener>()
let queue: ToastMessage[] = []

function notify() {
  for (const listener of listeners) {
    listener(queue)
  }
}

export function showToast(message: string, kind: ToastKind = 'error'): void {
  queue = [...queue, { id: crypto.randomUUID(), kind, message }]
  notify()

  window.setTimeout(() => {
    queue = queue.slice(1)
    notify()
  }, 3200)
}

export function subscribeToasts(listener: Listener): () => void {
  listeners.add(listener)
  listener(queue)
  return () => listeners.delete(listener)
}
