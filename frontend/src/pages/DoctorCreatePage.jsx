import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { API_BASE, readJson, writeJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

const initialForm = {
  firstName: '',
  lastName: '',
  specialization: '',
  experience: 0,
  userId: '',
  availability: '',
}

function DoctorCreatePage() {
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

    const loadDoctor = async () => {
      try {
        const detail = await readJson(`${API_BASE}/api/doctors/${id}/single`)
        setForm({
          firstName: detail?.firstName || '',
          lastName: detail?.lastName || '',
          specialization: detail?.specialization || '',
          experience: detail?.experience || 0,
          userId: detail?.userId || '',
          availability: detail?.availability || '',
        })
      } catch (err) {
        const message = err.message || 'Unable to load doctor details.'
        setError(message)
        showError(message)
      }
    }

    loadDoctor()
  }, [id, isEditMode])

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({
      ...prev,
      [name]: name === 'experience' ? Number(value) : value,
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
        experience: Number(form.experience || 0),
      }

      const url = isEditMode ? `${API_BASE}/api/doctors/${id}/update` : `${API_BASE}/api/doctors/register-doctor`
      const method = isEditMode ? 'PUT' : 'POST'
      const response = await writeJson(url, { method, body: JSON.stringify(payload) })

      setSuccess(isEditMode ? 'Doctor updated successfully.' : 'Doctor created successfully.')
      setTimeout(() => navigate('/doctors'), 500)
      if (response) {
        console.info('Doctor save response:', response)
      }
    } catch (err) {
      const message = err.message || (isEditMode ? 'Unable to update doctor.' : 'Unable to create doctor.')
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
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">{isEditMode ? 'Doctor update' : 'Doctor create'}</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">{isEditMode ? 'Update doctor' : 'Create doctor'}</h1>
        </div>
        <Link to="/doctors" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
          Back to doctors
        </Link>
      </div>

      <form className="mt-6 grid gap-5" onSubmit={handleSubmit}>
        <div className="grid gap-4 md:grid-cols-2">
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            First name
            <input name="firstName" required value={form.firstName} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Ada" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Last name
            <input name="lastName" required value={form.lastName} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Okafor" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Specialization
            <input name="specialization" required value={form.specialization} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Cardiology" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Experience years
            <input type="number" min="0" name="experience" value={form.experience} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="5" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
            User ID
            <input name="userId" value={form.userId} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="user-uuid" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
            Availability
            <input name="availability" value={form.availability} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Mon-Fri, 8:00 AM - 4:00 PM" />
          </label>
        </div>

        {error ? <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
        {success ? <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{success}</div> : null}

        <div className="flex items-center justify-end gap-3">
          <Link to="/doctors" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
            Cancel
          </Link>
          <button type="submit" disabled={saving} className="rounded-xl bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70">
            {saving ? 'Saving...' : isEditMode ? 'Update doctor' : 'Create doctor'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default DoctorCreatePage
