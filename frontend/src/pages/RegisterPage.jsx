import { useState } from 'react'
import { Link } from 'react-router-dom'

const DEFAULT_FORM = {
  name: '',
  email: '',
  phone: '',
  password: '',
  role: 'STAFF',
}

function RegisterPage({ onRegister, loading, error }) {
  const [form, setForm] = useState(DEFAULT_FORM)

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    await onRegister(form)
  }

  return (
    <div className="app-shell">
      <main className="content">
        <section className="panel login-panel">
          <div className="eyebrow">Create account</div>
          <h1>Register for JamTes System</h1>
          <p className="subtitle">
            Create a new account to access patient, doctor, pharmacy, and billing information.
          </p>

          <form className="login-form" onSubmit={handleSubmit}>
            <label>
              Full name
              <input
                type="text"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="Jane Doe"
                required
              />
            </label>

            <label>
              Email
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="name@example.com"
                required
              />
            </label>

            <label>
              Phone number
              <input
                type="tel"
                name="phone"
                value={form.phone}
                onChange={handleChange}
                placeholder="+234 ..."
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="Create a password"
                required
              />
            </label>

            <label>
              Role
              <select name="role" value={form.role} onChange={handleChange}>
                <option value="STAFF">Staff</option>
                <option value="DOCTOR">Doctor</option>
                <option value="PHARMACIST">Pharmacist</option>
                <option value="PATIENT">Patient</option>
                <option value="LAB_SCIENTIST">Lab scientist</option>
                <option value="ADMIN">Admin</option>
              </select>
            </label>

            <button type="submit" disabled={loading}>
              {loading ? 'Creating account...' : 'Create account'}
            </button>
          </form>

          {error ? <div className="status error">{error}</div> : null}

          <p className="auth-switch">
            Already have an account? <Link to="/login" className="auth-link">Sign in</Link>
          </p>
        </section>
      </main>
    </div>
  )
}

export default RegisterPage
