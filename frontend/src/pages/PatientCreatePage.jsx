import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { API_BASE, readJson, writeJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

const initialForm = {
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  dateOfBirth: '',
  gender: 'MALE',
  street: '',
  city: '',
  state: '',
  zipCode: '',
  country: '',
  active: true,
}

function PatientCreatePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const showError = useErrorRedirect()
  const isEditMode = Boolean(id)
  const [form, setForm] = useState(initialForm)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    if (!isEditMode) return

    const loadPatient = async () => {
      try {
        const payload = await readJson(`${API_BASE}/api/patients/${id}`)
        const detail = payload?.data || payload
        const address = detail?.address || {}
        setForm({
          firstName: detail?.firstName || '',
          lastName: detail?.lastName || '',
          email: detail?.email || '',
          phone: detail?.phone || '',
          dateOfBirth: detail?.dateOfBirth || '',
          gender: detail?.gender || 'MALE',
          street: address.street || '',
          city: address.city || '',
          state: address.state || '',
          zipCode: address.zipCode || '',
          country: address.country || '',
          active: Boolean(detail?.active ?? true),
        })
      } catch (err) {
        const message = err.message || 'Unable to load patient details.'
        setError(message)
        showError(message)
      }
    }

    loadPatient()
  }, [id, isEditMode])

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target
    setForm((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    setSuccess('')

    try {
      const payload = {
        ...form,
        dateOfBirth: form.dateOfBirth || null,
        address: {
          street: form.street,
          city: form.city,
          state: form.state,
          zipCode: form.zipCode,
          country: form.country,
        },
      }

      const url = isEditMode ? `${API_BASE}/api/patients/${id}` : `${API_BASE}/api/patients/register`
      const method = isEditMode ? 'PUT' : 'POST'
      const response = await writeJson(url, { method, body: JSON.stringify(payload) })

      setSuccess(isEditMode ? 'Patient updated successfully.' : 'Patient created successfully.')
      setTimeout(() => navigate('/patients'), 500)
      if (response) {
        console.info('Patient save response:', response)
      }
    } catch (err) {
      const message = err.message || (isEditMode ? 'Unable to update patient.' : 'Unable to create patient.')
      setError(message)
      showError(message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">{isEditMode ? 'Patient update' : 'Patient create'}</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">{isEditMode ? 'Update patient' : 'Create patient'}</h1>
        </div>
        <Link to="/patients" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
          Back to patients
        </Link>
      </div>

      <form className="mt-6 grid gap-5" onSubmit={handleSubmit}>
        <div className="grid gap-4 md:grid-cols-2">
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            First name
            <input name="firstName" required value={form.firstName} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Jane" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Last name
            <input name="lastName" required value={form.lastName} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Doe" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
            Email
            <input type="email" name="email" required value={form.email} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="jane@example.com" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Phone
            <input name="phone" required value={form.phone} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="+234 801 234 5678" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Date of birth
            <input type="date" name="dateOfBirth" value={form.dateOfBirth} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Gender
            <select name="gender" value={form.gender} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          </label>
        </div>

        <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
          <h2 className="text-lg font-semibold text-slate-900">Address</h2>
          <div className="mt-4 grid gap-4 md:grid-cols-2">
            <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
              Street
              <input name="street" value={form.street} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="12 Oba Akin" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              City
              <input name="city" value={form.city} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Lagos" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              State
              <input name="state" value={form.state} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Lagos State" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Zip code
              <input name="zipCode" value={form.zipCode} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="100001" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Country
              <input name="country" value={form.country} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Nigeria" />
            </label>
          </div>
        </div>

        <label className="flex items-center gap-3 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-medium text-slate-700">
          <input type="checkbox" name="active" checked={form.active} onChange={handleChange} />
          Patient is active
        </label>

        {error ? <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
        {success ? <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{success}</div> : null}

        <div className="flex items-center justify-end gap-3">
          <Link to="/patients" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
            Cancel
          </Link>
          <button type="submit" disabled={saving} className="rounded-xl bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70">
            {saving ? 'Saving...' : isEditMode ? 'Update patient' : 'Create patient'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default PatientCreatePage
