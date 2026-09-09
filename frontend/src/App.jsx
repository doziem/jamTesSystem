import { useState } from 'react'
import { Link, NavLink, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import DashboardPage from './pages/DashboardPage'
import PatientPage from './pages/PatientPage'
import DoctorPage from './pages/DoctorPage'
import PharmacyPage from './pages/PharmacyPage'
import BillingPage from './pages/BillingPage'
import RegisterPage from './pages/RegisterPage'
import { API_BASE, STORAGE_KEY } from './lib/api'

const USER_STORAGE_KEY = 'jamtes-auth-user'

async function loginUser({ identifier, password }) {
  const response = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ emailOrPhone: identifier, password }),
  })

  const data = await response.json()
  if (!response.ok) throw new Error(data.message || 'Unable to sign in. Please try again.')
  return data
}

function LoginPage({ onLogin, loading, error }) {
  const [identifier, setIdentifier] = useState('')
  const [password, setPassword] = useState('')
  const location = useLocation()
  const successMessage = location.state?.message || ''

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <main className="mx-auto flex min-h-screen w-full max-w-7xl items-center px-4 py-10 sm:px-6 lg:px-8">
        <section className="w-full max-w-lg rounded-3xl border border-slate-200 bg-white p-6 shadow-sm sm:p-8">
          <p className="text-sm font-semibold uppercase tracking-[0.2em] text-blue-500">React frontend</p>
          <h1 className="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">Hospital management portal</h1>
          <p className="mt-3 text-sm text-slate-600 sm:text-base">
            Sign in with your registered email or phone number to access the hospital dashboard.
          </p>

          {successMessage ? <div className="mt-4 rounded-xl bg-blue-50 px-4 py-3 text-sm text-blue-900">{successMessage}</div> : null}

          <form
            className="mt-6 grid gap-4"
            onSubmit={async (event) => {
              event.preventDefault()
              await onLogin({ identifier, password })
            }}
          >
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Email or phone
              <input
                className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-base outline-none ring-0 transition focus:border-blue-500"
                type="text"
                value={identifier}
                onChange={(event) => setIdentifier(event.target.value)}
                placeholder="name@example.com"
                required
              />
            </label>

            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Password
              <input
                className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-base outline-none ring-0 transition focus:border-blue-500"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="Enter password"
                required
              />
            </label>

            <button
              type="submit"
              disabled={loading}
              className="mt-1 rounded-xl bg-blue-600 px-4 py-3 text-base font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70"
            >
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </form>

          {error ? <div className="mt-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

          <p className="mt-5 text-center text-sm text-slate-600">
            Need an account? <Link to="/register" className="font-semibold text-blue-600 hover:underline">Create one</Link>
          </p>
        </section>
      </main>
    </div>
  )
}

function ProtectedRoute({ token, children }) {
  if (!token) return <Navigate to="/login" replace />
  return children
}

function AppLayout({ user, onLogout, children }) {
  const navClass = 'rounded-xl px-3 py-2 text-sm font-semibold text-slate-600 transition hover:bg-blue-50 hover:text-blue-700'
  const activeClass = 'bg-blue-100 text-blue-700'

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 to-indigo-50 text-slate-900">
      <header className="mx-auto flex w-full max-w-7xl flex-col gap-4 px-4 py-5 sm:px-6 lg:flex-row lg:items-center lg:justify-between lg:px-8">
        <div className="flex items-center gap-3">
          <div className="text-2xl font-bold tracking-tight">JamTes System</div>
          <span className="rounded-full bg-blue-100 px-3 py-1 text-xs font-bold uppercase tracking-[0.2em] text-blue-700">
            {user?.role || 'USER'}
          </span>
        </div>

        <nav className="flex flex-wrap gap-2">
          <NavLink to="/" end className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`}>Overview</NavLink>
          <NavLink to="/patients" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`}>Patients</NavLink>
          <NavLink to="/doctors" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`}>Doctors</NavLink>
          <NavLink to="/pharmacies" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`}>Pharmacies</NavLink>
          <NavLink to="/billing" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`}>Billing</NavLink>
        </nav>

        <button
          type="button"
          onClick={onLogout}
          className="rounded-full bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-800"
        >
          Log out
        </button>
      </header>

      <main className="mx-auto w-full max-w-7xl px-4 pb-10 sm:px-6 lg:px-8">
        <div className="mb-6 rounded-2xl border border-slate-200 bg-white/80 p-4 shadow-sm backdrop-blur sm:p-5">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Authenticated</p>
          <h2 className="mt-1 text-xl font-bold sm:text-2xl">{user?.name || 'Hospital staff'}</h2>
          <p className="text-sm text-slate-600">{user?.email || 'No email available'}</p>
        </div>

        {children}
      </main>
    </div>
  )
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
      const nextUser = { name: data.name || 'Authenticated user', email: data.email || identifier, role: data.role || 'USER' }
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

  const handleRegister = async (form) => {
    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ...form, role: form.role || 'STAFF' }),
      })
      const data = await response.json().catch(() => ({}))
      if (!response.ok) throw new Error(data.message || 'Unable to create your account. Please try again.')
      navigate('/login', { replace: true, state: { message: 'Registration successful. Please check your email to verify your account before logging in.' } })
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <Routes>
      <Route path="/login" element={token ? <Navigate to="/" replace /> : <LoginPage onLogin={handleLogin} loading={loading} error={error} />} />
      <Route path="/register" element={token ? <Navigate to="/" replace /> : <RegisterPage onRegister={handleRegister} loading={loading} error={error} />} />
      <Route path="/" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><DashboardPage /></AppLayout></ProtectedRoute>} />
      <Route path="/patients" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><PatientPage /></AppLayout></ProtectedRoute>} />
      <Route path="/doctors" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><DoctorPage /></AppLayout></ProtectedRoute>} />
      <Route path="/pharmacies" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><PharmacyPage /></AppLayout></ProtectedRoute>} />
      <Route path="/billing" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><BillingPage /></AppLayout></ProtectedRoute>} />
      <Route path="*" element={<Navigate to={token ? '/' : '/login'} replace />} />
    </Routes>
  )
}

export default App
