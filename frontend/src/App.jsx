import { useMemo, useState } from 'react'
import { Link, NavLink, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import DashboardPage from './pages/DashboardPage'
import PatientPage from './pages/PatientPage'
import PatientCreatePage from './pages/PatientCreatePage'
import PatientArrivalPage from './pages/PatientArrivalPage'
import PatientDetailPage from './pages/PatientDetailPage'
import DoctorPage from './pages/DoctorPage'
import DoctorCreatePage from './pages/DoctorCreatePage'
import PharmacyPage from './pages/PharmacyPage'
import PharmacyCreatePage from './pages/PharmacyCreatePage'
import BillingPage from './pages/BillingPage'
import BillingCreatePage from './pages/BillingCreatePage'
import LabReportPage from './pages/LabReportPage'
import LabReportCreatePage from './pages/LabReportCreatePage'
import RegisterPage from './pages/RegisterPage'
import ProfilePage from './pages/ProfilePage'
import ErrorPage from './pages/ErrorPage'
import { API_BASE, STORAGE_KEY, writeJson } from './lib/api'

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
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [accountMenuOpen, setAccountMenuOpen] = useState(false)
  const navClass =
    'flex items-center rounded-xl px-3 py-2 text-sm font-semibold text-slate-600 transition hover:bg-blue-50 hover:text-blue-700'
  const activeClass = 'bg-blue-100 text-blue-700'
  const initials = useMemo(() => {
    const name = user?.name?.trim()
    if (!name) return 'U'
    const parts = name.split(/\s+/).filter(Boolean)
    return parts.slice(0, 2).map((part) => part[0]?.toUpperCase() || '').join('') || 'U'
  }, [user?.name])

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <div className="flex min-h-screen">
        {sidebarOpen ? (
          <button
            type="button"
            aria-label="Close menu overlay"
            className="fixed inset-0 z-20 bg-slate-900/40 lg:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        ) : null}

        <aside
          className={`fixed inset-y-0 left-0 z-30 w-72 flex-col border-r border-slate-200 bg-white px-4 py-6 transition-transform duration-200 ease-out lg:static lg:z-auto lg:flex lg:translate-x-0 ${
            sidebarOpen ? 'flex translate-x-0' : 'flex -translate-x-full'
          }`}
        >
          <div className="mb-6 px-2">
            <h1 className="text-xl font-bold tracking-tight">JamTes System</h1>
            <p className="mt-1 text-xs uppercase tracking-[0.18em] text-slate-500">Hospital Portal</p>
          </div>

          <nav className="flex flex-1 flex-col gap-1" aria-label="Sidebar navigation">
            <NavLink to="/" end className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`} onClick={() => setSidebarOpen(false)}>Overview</NavLink>
            <NavLink to="/arrival" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`} onClick={() => setSidebarOpen(false)}>Patient arrival</NavLink>
            <NavLink to="/patients" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`} onClick={() => setSidebarOpen(false)}>Patients</NavLink>
            <NavLink to="/doctors" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`} onClick={() => setSidebarOpen(false)}>Doctors</NavLink>
            <NavLink to="/pharmacies" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`} onClick={() => setSidebarOpen(false)}>Pharmacies</NavLink>
            <NavLink to="/billing" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`} onClick={() => setSidebarOpen(false)}>Billing</NavLink>
            <NavLink to="/lab-reports" className={({ isActive }) => `${navClass} ${isActive ? activeClass : ''}`} onClick={() => setSidebarOpen(false)}>Lab reports</NavLink>
          </nav>

          <button
            type="button"
            onClick={onLogout}
            className="mt-6 rounded-xl bg-slate-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-600"
          >
            Log out
          </button>
        </aside>

        <div className="flex min-w-0 flex-1 flex-col">
          <header className="sticky top-0 z-10 border-b border-slate-200 bg-white/95 px-4 py-4 backdrop-blur sm:px-6 lg:px-8">
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <div className="flex items-center gap-3">
                  <button
                    type="button"
                    aria-label="Open menu"
                    className="inline-flex h-10 w-10 items-center justify-center rounded-lg border border-slate-300 text-slate-700 transition hover:bg-slate-100 lg:hidden"
                    onClick={() => setSidebarOpen(true)}
                  >
                    <svg viewBox="0 0 24 24" fill="none" className="h-5 w-5" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
                      <path d="M4 7h16M4 12h16M4 17h16" />
                    </svg>
                  </button>
                  <div className="text-xl font-bold tracking-tight sm:text-2xl">JamTes System</div>
                </div>
                <p className="text-xs uppercase tracking-[0.18em] text-slate-500 sm:hidden">Hospital Portal</p>
              </div>
              <div className="relative flex items-center justify-between gap-3 sm:justify-end">
                <button
                  type="button"
                  onClick={() => setAccountMenuOpen((isOpen) => !isOpen)}
                  className="inline-flex h-10 w-10 items-center justify-center rounded-full bg-blue-100 text-sm font-bold text-blue-700 transition hover:bg-blue-200"
                  aria-haspopup="menu"
                  aria-expanded={accountMenuOpen}
                  aria-label="Open user menu"
                >
                  {initials}
                </button>
                {accountMenuOpen ? (
                  <div className="absolute right-0 top-12 z-20 min-w-44 rounded-xl border border-slate-200 bg-white p-1 shadow-lg" role="menu">
                    <NavLink
                      to="/profile"
                      onClick={() => setAccountMenuOpen(false)}
                      className="block rounded-lg px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-100"
                      role="menuitem"
                    >
                      Profile
                    </NavLink>
                    <button
                      type="button"
                      onClick={() => {
                        setAccountMenuOpen(false)
                        onLogout()
                      }}
                      className="block w-full rounded-lg px-3 py-2 text-left text-sm font-semibold text-red-600 transition hover:bg-red-50"
                      role="menuitem"
                    >
                      Log out
                    </button>
                  </div>
                ) : null}
              </div>
            </div>

          </header>

          <main className="flex-1 px-4 py-6 sm:px-6 lg:px-8">
            <div className="mb-6 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-5">
              <h2 className="mt-1 text-xl font-bold sm:text-2xl">{user?.name || 'Hospital staff'}</h2>
              <p className="text-sm text-slate-600">{user?.email || 'No email available'}</p>
            </div>

            {children}
          </main>
        </div>
      </div>
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
      const nextUser = { id: data.userId || data.id || '', name: data.name || 'Authenticated user', email: data.email || identifier, phone: data.phone || '', role: data.role || 'USER' }
      setToken(nextToken)
      setUser(nextUser)
      if (nextToken) {
        localStorage.setItem(STORAGE_KEY, nextToken)
        localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(nextUser))
      }
      navigate('/', { replace: true })
    } catch (err) {
      navigate('/error', { replace: true, state: { message: err.message || 'Unable to sign in.' } })
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
      await writeJson(`${API_BASE}/auth/register`, {
        method: 'POST',
        body: JSON.stringify({ ...form, role: form.role || 'STAFF' }),
      })
      navigate('/login', { replace: true, state: { message: 'Registration successful. Please check your email to verify your account before logging in.' } })
    } catch (err) {
      navigate('/error', { replace: true, state: { message: err.message || 'Unable to create your account.' } })
    } finally {
      setLoading(false)
    }
  }

  const handleUserUpdate = (nextUser) => {
    const mergedUser = { ...user, ...(nextUser || {}) }
    setUser(mergedUser)
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(mergedUser))
  }

  return (
    <Routes>
      <Route path="/login" element={token ? <Navigate to="/" replace /> : <LoginPage onLogin={handleLogin} loading={loading} error={error} />} />
      <Route path="/register" element={token ? <Navigate to="/" replace /> : <RegisterPage onRegister={handleRegister} loading={loading} error={error} />} />
      <Route path="/" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><DashboardPage /></AppLayout></ProtectedRoute>} />
      <Route path="/arrival" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><PatientArrivalPage /></AppLayout></ProtectedRoute>} />
      <Route path="/patients" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><PatientPage /></AppLayout></ProtectedRoute>} />
      <Route path="/patients/:id" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><PatientDetailPage /></AppLayout></ProtectedRoute>} />
      <Route path="/patients/new" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><PatientCreatePage /></AppLayout></ProtectedRoute>} />
      <Route path="/patients/:id/edit" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><PatientCreatePage /></AppLayout></ProtectedRoute>} />
      <Route path="/doctors" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><DoctorPage /></AppLayout></ProtectedRoute>} />
      <Route path="/doctors/new" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><DoctorCreatePage /></AppLayout></ProtectedRoute>} />
      <Route path="/doctors/:id/edit" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><DoctorCreatePage /></AppLayout></ProtectedRoute>} />
      <Route path="/pharmacies" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><PharmacyPage /></AppLayout></ProtectedRoute>} />
      <Route path="/pharmacies/new" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><PharmacyCreatePage /></AppLayout></ProtectedRoute>} />
      <Route path="/billing" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><BillingPage /></AppLayout></ProtectedRoute>} />
      <Route path="/billing/new" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><BillingCreatePage /></AppLayout></ProtectedRoute>} />
      <Route path="/lab-reports" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><LabReportPage /></AppLayout></ProtectedRoute>} />
      <Route path="/lab-reports/new" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><LabReportCreatePage /></AppLayout></ProtectedRoute>} />
      <Route path="/lab-reports/:id/edit" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><LabReportCreatePage /></AppLayout></ProtectedRoute>} />
      <Route path="/profile" element={<ProtectedRoute token={token}><AppLayout user={user} onLogout={handleLogout}><ProfilePage user={user} token={token} onUserUpdate={handleUserUpdate} /></AppLayout></ProtectedRoute>} />
      <Route path="/error" element={<ErrorPage />} />
      <Route path="*" element={<Navigate to={token ? '/' : '/login'} replace />} />
    </Routes>
  )
}

export default App
