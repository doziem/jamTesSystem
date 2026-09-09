import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { API_BASE, getAuthHeaders } from '../lib/api'

const initialForm = {
  patientId: '',
  totalAmount: '',
  isPaid: false,
  paymentMethod: 'CASH',
  billingDate: '',
}

function BillingCreatePage() {
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
        patientId: form.patientId,
        totalAmount: Number(form.totalAmount || 0),
        isPaid: Boolean(form.isPaid),
        paymentMethod: form.paymentMethod,
        billingDate: form.billingDate || null,
      }

      const response = await fetch(`${API_BASE}/api/billing/create`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...getAuthHeaders(),
        },
        body: JSON.stringify(payload),
      })

      const data = await response.json().catch(() => ({}))
      if (!response.ok) {
        throw new Error(data.message || 'Unable to create billing record.')
      }

      setSuccess('Billing record created successfully.')
      setTimeout(() => navigate('/billing'), 500)
    } catch (err) {
      setError(err.message || 'Unable to create billing record.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Billing create</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Create billing</h1>
        </div>
        <Link to="/billing" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
          Back to billing
        </Link>
      </div>

      <form className="mt-6 grid gap-5" onSubmit={handleSubmit}>
        <div className="grid gap-4 md:grid-cols-2">
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Patient ID
            <input name="patientId" required value={form.patientId} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="patient-uuid" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Total amount
            <input type="number" step="0.01" min="0" name="totalAmount" required value={form.totalAmount} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="2500.00" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Payment method
            <select name="paymentMethod" value={form.paymentMethod} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
              <option value="CASH">Cash</option>
              <option value="CARD">Card</option>
              <option value="BANK_TRANSFER">Bank transfer</option>
              <option value="INSURANCE">Insurance</option>
            </select>
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Billing date
            <input type="date" name="billingDate" value={form.billingDate} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" />
          </label>
        </div>

        <label className="flex items-center gap-3 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-medium text-slate-700">
          <input type="checkbox" name="isPaid" checked={form.isPaid} onChange={handleChange} />
          Mark as paid
        </label>

        {error ? <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
        {success ? <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{success}</div> : null}

        <div className="flex items-center justify-end gap-3">
          <Link to="/billing" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
            Cancel
          </Link>
          <button type="submit" disabled={saving} className="rounded-xl bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70">
            {saving ? 'Saving...' : 'Create billing'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default BillingCreatePage
