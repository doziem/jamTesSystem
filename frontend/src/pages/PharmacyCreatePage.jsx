import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { API_BASE, getAuthHeaders } from '../lib/api'

const departmentOptions = ['MAIN_PHARMACY', 'GENERAL', 'CARDIOLOGY', 'ORTHOPEDICS', 'PEDIATRICS', 'NEUROLOGY', 'DERMATOLOGY', 'LABORATORY']

const initialForm = {
  name: '',
  department: 'GENERAL',
  mainPharmacy: true,
  mainPharmacyId: '',
}

function PharmacyCreatePage() {
  const navigate = useNavigate()
  const [form, setForm] = useState(initialForm)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

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
        name: form.name,
        department: form.department,
        mainPharmacy: form.mainPharmacy,
        mainPharmacyId: form.mainPharmacyId,
      }

      if (!form.mainPharmacy && !form.mainPharmacyId.trim()) {
        throw new Error('Main pharmacy ID is required for department pharmacy creation.')
      }

      const endpoint = form.mainPharmacy ? `${API_BASE}/api/pharmacies/main` : `${API_BASE}/api/pharmacies/${form.mainPharmacyId}/department`
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...getAuthHeaders(),
        },
        body: JSON.stringify(payload),
      })

      const data = await response.json().catch(() => ({}))
      if (!response.ok) {
        throw new Error(data.message || 'Unable to create pharmacy.')
      }

      setSuccess('Pharmacy created successfully.')
      setTimeout(() => navigate('/pharmacies'), 500)
    } catch (err) {
      setError(err.message || 'Unable to create pharmacy.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Pharmacy create</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Create pharmacy</h1>
        </div>
        <Link to="/pharmacies" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
          Back to pharmacies
        </Link>
      </div>

      <form className="mt-6 grid gap-5" onSubmit={handleSubmit}>
        <div className="grid gap-4 md:grid-cols-2">
          <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
            Pharmacy name
            <input name="name" required value={form.name} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="City Hospital Pharmacy" />
          </label>

          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Department
            <select name="department" value={form.department} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
              {departmentOptions.map((option) => (
                <option key={option} value={option}>{option}</option>
              ))}
            </select>
          </label>

          <label className="flex items-center gap-3 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-medium text-slate-700">
            <input type="checkbox" name="mainPharmacy" checked={form.mainPharmacy} onChange={handleChange} />
            This is the main pharmacy
          </label>
        </div>

        {!form.mainPharmacy ? (
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Parent main pharmacy ID
            <input name="mainPharmacyId" value={form.mainPharmacyId} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="main-pharmacy-id" />
          </label>
        ) : null}

        {error ? <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
        {success ? <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{success}</div> : null}

        <div className="flex items-center justify-end gap-3">
          <Link to="/pharmacies" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
            Cancel
          </Link>
          <button type="submit" disabled={saving} className="rounded-xl bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70">
            {saving ? 'Saving...' : 'Create pharmacy'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default PharmacyCreatePage
