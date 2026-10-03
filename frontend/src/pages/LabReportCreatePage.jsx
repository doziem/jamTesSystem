import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { API_BASE, normalizeList, readJson, writeJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'
import { loadUsersByRole } from '../lib/userOptions'

const initialForm = {
  patientId: '',
  requestedBy: '',
  selectedTestName: '',
  selectedTestResult: '',
  testResults: [],
  reportDate: '',
  requestDate: '',
  conductedBy: '',
  labRequestId: '',
}

const availableTests = [
  'Complete Blood Count (CBC)',
  'Urinalysis',
  'Blood Glucose',
  'Liver Function Test',
  'Kidney Function Test',
  'Lipid Profile',
  'Malaria Parasite',
  'Widal Test',
]

function LabReportCreatePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const showError = useErrorRedirect()
  const [searchParams] = useSearchParams()
  const isEditMode = Boolean(id)
  const [form, setForm] = useState(initialForm)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [doctors, setDoctors] = useState([])
  const [labScientistUsers, setLabScientistUsers] = useState([])

  const handleAddTestResult = () => {
    const testName = form.selectedTestName.trim()
    const result = form.selectedTestResult.trim()
    if (!testName) {
      setError('Please select a test name.')
      return
    }
    if (!result) {
      setError('Please enter a result for the selected test.')
      return
    }
    setForm((prev) => ({
      ...prev,
      testResults: [...prev.testResults, { id: Date.now(), testName, result }],
      selectedTestName: '',
      selectedTestResult: '',
    }))
    setError('')
  }

  useEffect(() => {
    const loadPage = async () => {
      try {
        const [doctorPayload, labScientists] = await Promise.all([
          readJson(`${API_BASE}/api/patients/doctors/assignable`),
          loadUsersByRole('LAB_SCIENTIST'),
        ])
        setDoctors(normalizeList(doctorPayload))
        setLabScientistUsers(labScientists)

        if (isEditMode) {
          const detail = await readJson(`${API_BASE}/api/lab-reports/${id}`)
          setForm({
            patientId: detail?.patientId || '',
            requestedBy: detail?.requestedBy || '',
            selectedTestName: '',
            selectedTestResult: '',
            testResults: Array.isArray(detail?.testResults) ? detail.testResults : [],
            reportDate: detail?.reportDate || '',
            requestDate: detail?.requestDate || '',
            conductedBy: detail?.conductedBy || '',
            labRequestId: detail?.labRequestId || '',
          })
        } else {
          const prefilledPatientId = searchParams.get('patientId')
          const prefilledRequestedBy = searchParams.get('requestedBy')
          if (prefilledPatientId || prefilledRequestedBy) {
            setForm((prev) => ({
              ...prev,
              patientId: prefilledPatientId || prev.patientId,
              requestedBy: prefilledRequestedBy || prev.requestedBy,
              requestDate: prev.requestDate || new Date().toISOString().slice(0, 10),
            }))
          }
        }
      } catch (err) {
        const message = err.message || (isEditMode ? 'Unable to load lab report details.' : 'Unable to load assignable users.')
        setError(message)
        showError(message)
      }
    }

    loadPage()
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
      if (form.testResults.length === 0) {
        throw new Error('Please attach at least one test with result.')
      }
      const payload = {
        patientId: form.patientId,
        requestedBy: form.requestedBy,
        conductedBy: form.conductedBy || null,
        labRequestId: form.labRequestId || null,
        reportDate: form.reportDate || null,
        requestDate: form.requestDate || null,
        testResults: form.testResults.map((item) => ({
          id: item.id,
          testName: item.testName,
          result: item.result,
        })),
      }

      const url = isEditMode ? `${API_BASE}/api/lab-reports/${id}` : `${API_BASE}/api/lab-reports/create`
      const method = isEditMode ? 'PUT' : 'POST'
      const response = await writeJson(url, { method, body: JSON.stringify(payload) })

      setSuccess(isEditMode ? 'Lab report updated successfully.' : 'Lab report created successfully.')
      setTimeout(() => navigate('/lab-reports'), 500)
      if (response) {
        console.info('Lab report save response:', response)
      }

      const handleAddTestResult = () => {
        const testName = form.selectedTestName.trim()
        const result = form.selectedTestResult.trim()
        if (!testName) {
          setError('Please select a test name.')
          return
        }
        if (!result) {
          setError('Please enter a result for the selected test.')
          return
        }

        const duplicate = form.testResults.some((item) => item.testName.toLowerCase() === testName.toLowerCase())
        if (duplicate) {
          setError('This test is already attached. Remove it first to change the result.')
          return
        }

        setError('')
        setForm((prev) => ({
          ...prev,
          selectedTestName: '',
          selectedTestResult: '',
          testResults: [...prev.testResults, { testName, result }],
        }))
      }

      const handleRemoveTestResult = (testName) => {
        setForm((prev) => ({
          ...prev,
          testResults: prev.testResults.filter((item) => item.testName !== testName),
        }))
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
            <select name="requestedBy" required value={form.requestedBy} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
              <option value="">Select doctor</option>
              {doctors.map((doctor) => (
                <option key={doctor.id} value={doctor.id}>
                  {doctor.fullName || [doctor.firstName, doctor.lastName].filter(Boolean).join(' ') || doctor.id}
                </option>
              ))}
            </select>
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Test name
            <select name="selectedTestName" value={form.selectedTestName} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
              <option value="">Select a test</option>
              {availableTests.map((testName) => (
                <option key={testName} value={testName}>
                  {testName}
                </option>
              ))}
            </select>
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Conducted by
            <select name="conductedBy" value={form.conductedBy} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
              <option value="">Select lab scientist user</option>
              {labScientistUsers.map((user) => (
                <option key={user.value} value={user.value}>
                  {user.label}
                </option>
              ))}
            </select>
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
            Result
            <textarea name="selectedTestResult" value={form.selectedTestResult} onChange={handleChange} className="min-h-28 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Result for selected test..." />
          </label>
          <div className="md:col-span-2">
            <button type="button" onClick={handleAddTestResult} className="rounded-xl border border-blue-300 bg-blue-50 px-4 py-2 text-sm font-semibold text-blue-700 transition hover:bg-blue-100">
              Attach test result
            </button>
          </div>
          <div className="md:col-span-2 rounded-2xl border border-slate-200 bg-slate-50 p-4">
            <p className="text-sm font-semibold text-slate-700">Attached tests and results</p>
            {form.testResults.length > 0 ? (
              <div className="mt-3 grid gap-2">
                {form.testResults.map((item) => (
                  <div key={item.testName} className="flex items-start justify-between gap-3 rounded-xl border border-slate-200 bg-white p-3">
                    <div>
                      <p className="text-sm font-semibold text-slate-800">{item.testName}</p>
                      <p className="text-sm text-slate-600">{item.result}</p>
                    </div>
                    <button type="button" onClick={() => handleRemoveTestResult(item.testName)} className="rounded-lg border border-red-200 bg-red-50 px-3 py-1.5 text-xs font-semibold text-red-700 hover:bg-red-100">
                      Remove
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <p className="mt-2 text-sm text-slate-500">No tests attached yet.</p>
            )}
          </div>
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
