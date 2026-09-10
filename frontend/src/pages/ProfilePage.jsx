import { useEffect, useState } from 'react'
import { API_BASE, readJson, writeJson } from '../lib/api'

const EMPTY_FORM = { id: '', name: '', email: '', phone: '', role: '', active: true }

function ProfilePage({ user, token, onUserUpdate }) {
  const [form, setForm] = useState(EMPTY_FORM)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    const loadProfile = async () => {
      setLoading(true)
      setError('')
      try {
        const payload = await readJson(`${API_BASE}/auth/me`, {
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        })
        const profile = payload?.data || payload
        setForm({
          id: profile.id || user?.id || '',
          name: profile.name || '',
          email: profile.email || '',
          phone: profile.phone || '',
          role: profile.role || '',
          active: typeof profile.active === 'boolean' ? profile.active : true,
        })
      } catch (err) {
        setError(err.message || 'Unable to load profile.')
      } finally {
        setLoading(false)
      }
    }

    loadProfile()
  }, [token, user?.id])

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target
    setForm((current) => ({ ...current, [name]: type === 'checkbox' ? checked : value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    setSuccess('')

    try {
      const data = await writeJson(`${API_BASE}/api/users/${form.id}`, {
        method: 'PUT',
        headers: token ? { Authorization: `Bearer ${token}` } : {},
        body: JSON.stringify(form),
      })
      onUserUpdate((data && (data.data || data)) || form)
      setSuccess('Profile updated successfully.')
    } catch (err) {
      setError(err.message || 'Unable to update profile.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="border-b border-slate-200 pb-4">
        <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Account</div>
        <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">User profile</h1>
      </div>

      {loading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading profile...</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
      {success ? <div className="mt-4 rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{success}</div> : null}

      {!loading ? (
        <form className="mt-5 grid gap-4 sm:grid-cols-2" onSubmit={handleSubmit}>
          <label className="grid gap-2 text-sm font-medium text-slate-700 sm:col-span-2">
            Full name
            <input className="rounded-xl border border-slate-300 px-4 py-3" name="name" value={form.name} onChange={handleChange} />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Email
            <input className="rounded-xl border border-slate-300 px-4 py-3" name="email" value={form.email} onChange={handleChange} />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Phone
            <input className="rounded-xl border border-slate-300 px-4 py-3" name="phone" value={form.phone} onChange={handleChange} />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700 sm:col-span-2">
            Role
            <input className="rounded-xl border border-slate-300 bg-slate-100 px-4 py-3" name="role" value={form.role} readOnly />
          </label>
          <label className="flex items-center gap-2 text-sm font-medium text-slate-700 sm:col-span-2">
            <input type="checkbox" name="active" checked={form.active} onChange={handleChange} />
            Active account
          </label>
          <button className="rounded-xl bg-blue-600 px-4 py-3 text-sm font-semibold text-white sm:col-span-2" type="submit" disabled={saving}>
            {saving ? 'Saving...' : 'Save changes'}
          </button>
        </form>
      ) : null}
    </div>
  )
}

export default ProfilePage
