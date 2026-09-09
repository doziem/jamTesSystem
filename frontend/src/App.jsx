import { useState } from 'react'
import { Link, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import './App.css'
import AppLayout from './components/AppLayout'
import { API_BASE, STORAGE_KEY } from './lib/api'
import DashboardPage from './pages/DashboardPage'
import PatientPage from './pages/PatientPage'
import DoctorPage from './pages/DoctorPage'
import PharmacyPage from './pages/PharmacyPage'
import BillingPage from './pages/BillingPage'
import RegisterPage from './pages/RegisterPage'

const USER_STORAGE_KEY = 'jamtes-auth-user'

async function loginUser({ identifier, password }) {
  const response = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ emailOrPhone: identifier, password }),
  })

  const data = await response.json()

  if (!response.ok) {
    throw new Error(data.message || 'Unable to sign in. Please try again.')
  }

  return data
}

function LoginPage({ onLogin, loading, error }) {
  const [identifier, setIdentifier] = useState('')
  const [password, setPassword] = useState('')
  const location = useLocation()
  const successMessage = location.state?.message || ''

  return (
    <div className="app-shell">
      <main className="content">
        <section className="panel login-panel">
          <div className="eyebrow">React frontend</div>
          <h1>Hospital management portal</h1>
          <p className="subtitle">
            Sign in with your registered email or phone number to access the hospital dashboard.
          </p>

          {successMessage ? <div className="status info">{successMessage}</div> : null}

          <form
            className="login-form"
            onSubmit={async (event) => {
              event.preventDefault()
              await onLogin({ identifier, password })
            }}
          >
            <label>
              Email or phone
              <input
                type="text"
                value={identifier}
                onChange={(event) => setIdentifier(event.target.value)}
                placeholder="name@example.com"
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="Enter password"
                required
              />
            </label>

            <button type="submit" disabled={loading}>
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </form>

          {error ? <div className="status error">{error}</div> : null}

          <p className="auth-switch">
            Need an account? <Link to="/register" className="auth-link">Create one</Link>
          </p>
        </section>
      </main>
    </div>
  )
}

function ProtectedRoute({ token, children }) {
  if (!token) {
    return <Navigate to="/login" replace />
  }

  return children
}

function App() {
  const [token, setToken] = useState(() => localStorage.getItem(STORAGE_KEY) || '')
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem(USER_STORAGE_KEY)
    return savedUser ? JSON.parse(savedUser) : null
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleLogin = async ({ identifier, password }) => {
    setLoading(true)
    setError('')

    try {
      const data = await loginUser({ identifier, password })
      const nextToken = data.token || ''
      const nextUser = {
        name: data.name || 'Authenticated user',
        email: data.email || identifier,
        role: data.role || 'USER',
      }

      setToken(nextToken)
      setUser(nextUser)

      if (nextToken) {
        localStorage.setItem(STORAGE_KEY, nextToken)
        localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(nextUser))
      }

      navigate('/', { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    setToken('')
    setUser(null)
    localStorage.removeItem(STORAGE_KEY)
    localStorage.removeItem(USER_STORAGE_KEY)
    setError('')
    navigate('/login', { replace: true })
  }

  const handleRegister = async ({ name, email, phone, password, role }) => {
    setLoading(true)
    setError('')

    try {
      const response = await fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name,
          email,
          phone,
          password,
          role: role || 'STAFF',
        }),
      })

      const data = await response.json().catch(() => ({}))

      if (!response.ok) {
        throw new Error(data.message || 'Unable to create your account. Please try again.')
      }

      navigate('/login', {
        replace: true,
        state: {
          message: 'Registration successful. Please check your email to verify your account before logging in.',
        },
      })
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={
          token ? (
            <Navigate to="/" replace />
          ) : (
            <LoginPage onLogin={handleLogin} loading={loading} error={error} />
          )
        }
      />

      <Route
        path="/register"
        element={
          token ? (
            <Navigate to="/" replace />
          ) : (
            <RegisterPage onRegister={handleRegister} loading={loading} error={error} />
          )
        }
      />

      <Route
        path="/"
        element={
          <ProtectedRoute token={token}>
            <AppLayout user={user} onLogout={handleLogout}>
              <DashboardPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/patients"
        element={
          <ProtectedRoute token={token}>
            <AppLayout user={user} onLogout={handleLogout}>
              <PatientPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/doctors"
        element={
          <ProtectedRoute token={token}>
            <AppLayout user={user} onLogout={handleLogout}>
              <DoctorPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/pharmacies"
        element={
          <ProtectedRoute token={token}>
            <AppLayout user={user} onLogout={handleLogout}>
              <PharmacyPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/billing"
        element={
          <ProtectedRoute token={token}>
            <AppLayout user={user} onLogout={handleLogout}>
              <BillingPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<Navigate to={token ? '/' : '/login'} replace />} />
    </Routes>
  )
}

export default App
