import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { API_BASE, readJson, writeJson } from '../lib/api'

const availableTests = [
  'Complete Blood Count (CBC)', 'Urinalysis', 'Blood Glucose', 'Liver Function Test',
  'Kidney Function Test', 'Lipid Profile', 'Malaria Parasite', 'Widal Test',
]

function LabReportCreatePage() {
  const { id } = useParams()
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [patientId, setPatientId] = useState(searchParams.get('patientId') || '')
  const [tests, setTests] = useState([])
  const [selectedTest, setSelectedTest] = useState('')
  const [loading, setLoading] = useState(Boolean(id))
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!id) return
    const load = async () => {
      try {
        const report = await readJson(`${API_BASE}/api/lab-reports/${id}`)
        setPatientId(report.patientId)
        setTests(report.testResults || [])
      } catch (err) {
        setError(err.message || 'Unable to load the lab request.')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [id])

  const addTest = () => {
    if (!selectedTest || tests.some((test) => test.testName === selectedTest)) {
      setError('Select a test that has not already been added.')
      return
    }
    setTests((current) => [...current, { testName: selectedTest, result: '' }])
    setSelectedTest('')
    setError('')
  }

  const submit = async (event) => {
    event.preventDefault()
    if (!tests.length) {
      setError('Select at least one test.')
      return
    }
    if (id && tests.some((test) => !test.result.trim())) {
      setError('Enter a result for each requested test.')
      return
    }
    setSaving(true)
    setError('')
    try {
      await writeJson(id ? `${API_BASE}/api/lab-reports/${id}` : `${API_BASE}/api/lab-reports/create`, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify({ patientId, testResults: tests.map(({ testName, result }) => ({ testName, result })) }),
      })
      navigate(id ? '/lab-reports' : `/patients/${patientId}`)
    } catch (err) {
      setError(err.message || 'Unable to save the lab request.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex items-center justify-between gap-3 border-b border-slate-200 pb-4">
        <h1 className="text-2xl font-bold text-slate-900">{id ? 'Submit lab results' : 'Request lab tests'}</h1>
        <Link to={patientId && !id ? `/patients/${patientId}` : '/lab-reports'} className="text-sm font-semibold text-blue-700">Back</Link>
      </div>
      {loading ? <p className="mt-4 text-sm text-slate-600">Loading request...</p> : (
        <form className="mt-5 grid gap-4" onSubmit={submit}>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Patient ID
            <input required value={patientId} readOnly={Boolean(id || searchParams.get('patientId'))} onChange={(event) => setPatientId(event.target.value)} className="rounded-xl border border-slate-300 px-4 py-3" />
          </label>
          {!id ? (
            <div className="flex flex-wrap gap-2">
              <select value={selectedTest} onChange={(event) => setSelectedTest(event.target.value)} className="rounded-xl border border-slate-300 px-4 py-3">
                <option value="">Select a test</option>
                {availableTests.map((test) => <option key={test} value={test}>{test}</option>)}
              </select>
              <button type="button" onClick={addTest} className="rounded-xl border border-blue-300 px-4 py-2 text-sm font-semibold text-blue-700">Add test</button>
            </div>
          ) : null}
          <div className="space-y-3">
            {tests.map((test, index) => (
              <div key={test.testName} className="rounded-xl border border-slate-200 p-4">
                <div className="flex justify-between gap-2">
                  <strong>{test.testName}</strong>
                  {!id ? <button type="button" onClick={() => setTests((current) => current.filter((_, i) => i !== index))} className="text-sm text-red-700">Remove</button> : null}
                </div>
                {id ? <textarea required value={test.result || ''} onChange={(event) => setTests((current) => current.map((item, i) => i === index ? { ...item, result: event.target.value } : item))} aria-label={`Result for ${test.testName}`} className="mt-2 w-full rounded-xl border border-slate-300 px-4 py-3" /> : null}
              </div>
            ))}
          </div>
          {error ? <div role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
          <button type="submit" disabled={saving} className="justify-self-start rounded-xl btn-brand px-5 py-2.5 text-sm font-semibold disabled:opacity-60">{saving ? 'Saving...' : id ? 'Submit results' : 'Send request'}</button>
        </form>
      )}
    </div>
  )
}

export default LabReportCreatePage
