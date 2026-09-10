import { useEffect, useState } from 'react'
import { API_BASE, normalizeList, readJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

function DashboardPage() {
  const [summary, setSummary] = useState({ patients: [], doctors: [], pharmacies: [], billings: [] })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const showError = useErrorRedirect()

  useEffect(() => {
    const loadDashboard = async () => {
      setLoading(true)
      setError('')

      try {
        const [patientsResponse, doctorsResponse, pharmaciesResponse, billingsResponse] = await Promise.all([
          readJson(`${API_BASE}/api/patients/all?page=0&size=5`),
          readJson(`${API_BASE}/api/doctors/all`),
          readJson(`${API_BASE}/api/pharmacies/all?page=0&size=5&sortBy=name&sortDirection=asc`),
          readJson(`${API_BASE}/api/billing/patient/all?page=0&size=5`),
        ])

        setSummary({
          patients: normalizeList(patientsResponse),
          doctors: normalizeList(doctorsResponse),
          pharmacies: normalizeList(pharmaciesResponse),
          billings: normalizeList(billingsResponse),
        })
      } catch (err) {
        const message = err.message || 'Unable to load dashboard data.'
        setError(message)
        showError(message)
      } finally {
        setLoading(false)
      }
    }

    loadDashboard()
  }, [])

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Overview</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Hospital dashboard</h1>
        </div>
      </div>

      {loading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading dashboard...</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

      {!loading && !error ? (
        <div className="mt-5 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {[
            ['Patients', summary.patients.length],
            ['Doctors', summary.doctors.length],
            ['Pharmacies', summary.pharmacies.length],
            ['Billing entries', summary.billings.length],
          ].map(([label, value]) => (
            <div key={label} className="rounded-2xl border border-slate-200 bg-slate-50 p-5">
              <span className="text-sm text-slate-600">{label}</span>
              <strong className="mt-2 block text-3xl font-bold text-slate-900">{value}</strong>
            </div>
          ))}
        </div>
      ) : null}
    </div>
  )
}

export default DashboardPage
