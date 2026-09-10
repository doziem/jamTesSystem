import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { API_BASE, readJson, writeJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

const initialForm = {
  patientId: '',
  requestedBy: '',
  testName: '',
  result: '',
  reportDate: '',
  requestDate: '',
  conductedBy: '',
  labRequestId: '',
}

function LabReportCreatePage() {
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

    const loadReport = async () => {
      try {
        const detail = await readJson(`${API_BASE}/api/lab-reports/${id}`)
        setForm({
          patientId: detail?.patientId || '',
          requestedBy: detail?.requestedBy || '',
          testName: detail?.testName || '',
          result: detail?.result || '',
          reportDate: detail?.reportDate || '',
          requestDate: detail?.requestDate || '',
          conductedBy: detail?.conductedBy || '',
          labRequestId: detail?.labRequestId || '',
        })
      } catch (err) {
        const message = err.message || 'Unable to load lab report details.'
        setError(message)
        showError(message)
      }
    }

    loadReport()
  }, [id, isEditMode])

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({
      ...prev,
      [name]: value,
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
        reportDate: form.reportDate || null,
        requestDate: form.requestDate || null,
        testNames: form.testName ? [form.testName] : [],
      }

      const url = isEditMode ? `${API_BASE}/api/lab-reports/${id}` : `${API_BASE}/api/lab-reports/create`
      const method = isEditMode ? 'PUT' : 'POST'
      const response = await writeJson(url, { method, body: JSON.stringify(payload) })

      setSuccess(isEditMode ? 'Lab report updated successfully.' : 'Lab report created successfully.')
      setTimeout(() => navigate('/lab-reports'), 500)
      if (response) {
        console.info('Lab report save response:', response)
      }
    } catch (err) {
      const message = err.message || (isEditMode ? 'Unable to update lab report.' : 'Unable to create lab report.')
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
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">{isEditMode ? 'Lab report update' : 'Lab report create'}</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">{isEditMode ? 'Update lab report' : 'Create lab report'}</h1>
        </div>
        <Link to="/lab-reports" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
          Back to lab reports
        </Link>
      </div>

      <form className="mt-6 grid gap-5" onSubmit={handleSubmit}>
        <div className="grid gap-4 md:grid-cols-2">
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Patient ID
            <input name="patientId" required value={form.patientId} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="patient-uuid" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Requested by
            <input name="requestedBy" required value={form.requestedBy} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="doctor-uuid" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Test name
            <input name="testName" required value={form.testName} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="CBC" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Conducted by
            <input name="conductedBy" value={form.conductedBy} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="lab technician" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
            Result
            <textarea name="result" required value={form.result} onChange={handleChange} className="min-h-28 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Normal range..." />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Request date
            <input type="date" name="requestDate" value={form.requestDate} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Report date
            <input type="date" name="reportDate" value={form.reportDate} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
            Lab request ID
            <input name="labRequestId" value={form.labRequestId} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="lab-request-uuid" />
          </label>
        </div>

        {error ? <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
        {success ? <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{success}</div> : null}

        <div className="flex items-center justify-end gap-3">
          <Link to="/lab-reports" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">
            Cancel
          </Link>
          <button type="submit" disabled={saving} className="rounded-xl btn-brand px-5 py-2.5 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-70">
            {saving ? 'Saving...' : isEditMode ? 'Update report' : 'Create report'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default LabReportCreatePage
