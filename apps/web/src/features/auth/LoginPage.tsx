import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../app/auth'
import { saveSession } from '../../lib/auth/session'
import { apiClient } from '../../lib/api/client'

export function LoginPage() {
  const navigate = useNavigate()
  const { setSession } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function onSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setError(null)

    try {
      const response = await apiClient.login(email.trim(), password)
      const session = { accessToken: response.accessToken, user: response.user }
      saveSession(session)
      setSession(session)
      navigate('/lobby')
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'No se pudo iniciar sesión')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="auth-shell">
      <section className="auth-card">
        <h1>Entrar a Catan Online</h1>
        <p>Conéctate para ir al lobby y unirte a una partida de 4 jugadores.</p>
        <form className="auth-form" onSubmit={onSubmit}>
          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </label>
          <label>
            Contraseña
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
              minLength={6}
            />
          </label>
          {error ? <p className="error-banner">{error}</p> : null}
          <button type="submit" disabled={submitting}>
            {submitting ? 'Entrando...' : 'Iniciar sesión'}
          </button>
        </form>
        <p>
          ¿No tienes cuenta? <Link to="/register">Regístrate</Link>
        </p>
      </section>
    </main>
  )
}
