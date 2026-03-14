import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../app/auth'
import { saveSession } from '../../lib/auth/session'
import { apiClient } from '../../lib/api/client'

export function RegisterPage() {
  const navigate = useNavigate()
  const { setSession } = useAuth()
  const [nickname, setNickname] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function onSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setError(null)

    try {
      const response = await apiClient.register(email.trim(), password, nickname.trim())
      const session = { accessToken: response.accessToken, user: response.user }
      saveSession(session)
      setSession(session)
      navigate('/lobby')
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'No se pudo registrar')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="auth-shell">
      <section className="auth-card">
        <h1>Crear cuenta</h1>
        <p>Regístrate para entrar al lobby de Catan.</p>
        <form className="auth-form" onSubmit={onSubmit}>
          <label>
            Nickname
            <input
              type="text"
              value={nickname}
              onChange={(event) => setNickname(event.target.value)}
              required
              minLength={3}
              maxLength={32}
            />
          </label>
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
            {submitting ? 'Creando...' : 'Crear cuenta'}
          </button>
        </form>
        <p>
          ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
        </p>
      </section>
    </main>
  )
}
