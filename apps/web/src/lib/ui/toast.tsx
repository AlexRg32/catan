import { useEffect, useState } from 'react'
import { subscribeToasts, type ToastMessage } from './toastStore'

export function ToastViewport() {
  const [messages, setMessages] = useState<ToastMessage[]>([])

  useEffect(() => {
    return subscribeToasts(setMessages)
  }, [])

  if (messages.length === 0) {
    return null
  }

  return (
    <aside className="toast-viewport" aria-live="polite">
      {messages.map((toast) => (
        <p key={toast.id} className={`toast toast-${toast.kind}`}>
          {toast.message}
        </p>
      ))}
    </aside>
  )
}
