import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { API_BASE, USER_STORAGE_KEY, readJson, writeJson } from '../lib/api'

const emptyForm = { name: '', category: '', unitPrice: '', quantity: '', active: true }
const inputClass = 'rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500'

function currentUserId() {
  try {
    return JSON.parse(localStorage.getItem(USER_STORAGE_KEY) || 'null')?.id || ''
  } catch {
    return ''
  }
}

function MedicationFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [form, setForm] = useState(emptyForm)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    if (!id) return
    readJson(`${API_BASE}/api/medications`)
      .then((payload) => {
        const medication = (Array.isArray(payload) ? payload : []).find((item) => item.id === id)
        if (!medication) throw new Error('Medication not found.')
        setForm({
          name: medication.name || '',
          category: medication.category || '',
          unitPrice: medication.unitPrice ?? '',
          quantity: medication.quantity ?? '',
          active: medication.active,
        })
      })
      .catch((err) => setError(err.message || 'Unable to load medication.'))
  }, [id])

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target
    setForm((prev) => ({ ...prev, [name]: type === 'checkbox' ? checked : value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    setSuccess('')
    try {
      const body = JSON.stringify({
        name: form.name.trim(),
        category: form.category.trim(),
        unitPrice: Number(form.unitPrice) || 0,
        quantity: Number(form.quantity) || 0,
        active: form.active,
      })
      if (id) {
        await writeJson(`${API_BASE}/api/medications/${id}`, { method: 'PUT', body })
      } else {
        const supervisorId = currentUserId()
        if (!supervisorId) throw new Error('Unable to identify the signed-in user. Please log in again.')
        await writeJson(`${API_BASE}/create/${supervisorId}`, { method: 'POST', body })
      }
      setSuccess(id ? 'Medication updated.' : 'Medication created.')
      setTimeout(() => navigate('/main-pharmacy/medications'), 500)
    } catch (err) {
      setError(err.message || 'Unable to save medication.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Main pharmacy</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">{id ? 'Update inventory medication' : 'Create inventory medication'}</h1>
        </div>
        <Link to="/main-pharmacy" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
          Back to main pharmacy
        </Link>
      </div>

      <form className="mt-6 grid gap-5" onSubmit={handleSubmit}>
        <div className="grid gap-4 md:grid-cols-2">
          <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
            Name
            <input name="name" required value={form.name} onChange={handleChange} className={inputClass} placeholder="Paracetamol 500mg" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Category
            <input name="category" value={form.category} onChange={handleChange} className={inputClass} placeholder="Analgesic" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Unit price
            <input name="unitPrice" type="number" min="0" step="0.01" required value={form.unitPrice} onChange={handleChange} className={inputClass} />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Inventory quantity
            <input name="quantity" type="number" min="0" step="1" required value={form.quantity} onChange={handleChange} className={inputClass} />
          </label>
          <label className="flex items-center gap-3 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-medium text-slate-700">
            <input type="checkbox" name="active" checked={form.active} onChange={handleChange} />
            Active
          </label>
        </div>

        {error ? <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
        {success ? <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{success}</div> : null}

        <div className="flex items-center justify-end gap-3">
          <Link to="/main-pharmacy/medications" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">Cancel</Link>
          <button type="submit" disabled={saving} className="rounded-xl btn-brand px-5 py-2.5 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-70">
            {saving ? 'Saving...' : id ? 'Save changes' : 'Create medication'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default MedicationFormPage
