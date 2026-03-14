import { createContext, useContext } from 'react'
import type { Session } from '../lib/auth/session'

type AuthContextValue = {
  session: Session | null
  setSession: (session: Session | null) => void
}

export const AuthContext = createContext<AuthContextValue | null>(null)

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthContext.Provider')
  }
  return context
}
