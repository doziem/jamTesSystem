import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { API_BASE, normalizeList, readJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

function LabReportPage() {
  const [reports, setReports] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const showError = useErrorRedirect()

  useEffect(() => {
    const loadReports = async () => {
      setLoading(true)
      setError('')

      try {
        const payload = await readJson(`${API_BASE}/api/lab-reports/patients/all`)
        setReports(normalizeList(payload))
      } catch (err) {
        const message = err.message || 'Unable to load lab reports.'
        setError(message)
        showError(message)
      } finally {
        setLoading(false)
      }
    }

    loadReports()
  }, [])

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Lab report operations</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Lab reports</h1>
        </div>
        <Link to="/lab-reports/new" className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-700">
          Create report
        </Link>
      </div>

      {loading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading lab reports...</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

      {!loading && !error ? (
        <div className="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {reports.length > 0 ? reports.map((report, index) => {
            const reportId = report.id || report.labReportId
            const patientId = report.patientId || report.patient?.id || 'N/A'
            const requestedBy = report.requestedBy || report.requestedById || 'N/A'
            const testName = report.testName || report.testNames?.[0] || 'General test'
            const result = report.result || 'No result recorded'

            return (
              <div key={reportId || `${patientId}-${index}`} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <strong className="block text-base font-semibold text-slate-900">{testName}</strong>
                    <span className="mt-1 block text-sm text-slate-600">Patient #{patientId}</span>
                  </div>
                  <Link to={`/lab-reports/${reportId}/edit`} className="rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-100">
                    Edit
                  </Link>
                </div>
                <p className="mt-3 text-sm text-slate-500">Requested by: {requestedBy}</p>
                <p className="mt-2 text-sm text-slate-700 line-clamp-3">{result}</p>
              </div>
            )
          }) : <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No lab reports available.</div>}
        </div>
      ) : null}
    </div>
  )
}

export default LabReportPage
