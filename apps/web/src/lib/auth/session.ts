import type { User } from '../types'

const SESSION_KEY = 'catan.session.v1'

export type Session = {
  accessToken: string
  user: User
}

export function loadSession(): Session | null {
  const raw = window.localStorage.getItem(SESSION_KEY)
  if (!raw) {
    return null
  }

  try {
    const parsed = JSON.parse(raw) as Session
    if (!parsed.accessToken || !parsed.user?.id) {
      return null
    }
    return parsed
  } catch {
    return null
  }
}

export function saveSession(session: Session): void {
  window.localStorage.setItem(SESSION_KEY, JSON.stringify(session))
}

export function clearSession(): void {
  window.localStorage.removeItem(SESSION_KEY)
}
