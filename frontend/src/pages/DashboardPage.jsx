import { useEffect, useState } from 'react'
import { API_BASE, normalizeList, readJson } from '../lib/api'

function DashboardPage() {
  const [summary, setSummary] = useState({ patients: [], doctors: [], pharmacies: [], billings: [] })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

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
        setError(err.message || 'Unable to load dashboard data.')
      } finally {
        setLoading(false)
      }
    }

    loadDashboard()
  }, [])

  return (
    <div className="page-panel">
      <div className="page-head">
        <div>
          <div className="eyebrow">Overview</div>
          <h1>Hospital dashboard</h1>
        </div>
      </div>

      {loading ? <div className="status info">Loading dashboard...</div> : null}
      {error ? <div className="status error">{error}</div> : null}

      {!loading && !error ? (
        <div className="stats-grid">
          <div className="stat-card">
            <span>Patients</span>
            <strong>{summary.patients.length}</strong>
          </div>
          <div className="stat-card">
            <span>Doctors</span>
            <strong>{summary.doctors.length}</strong>
          </div>
          <div className="stat-card">
            <span>Pharmacies</span>
            <strong>{summary.pharmacies.length}</strong>
          </div>
          <div className="stat-card">
            <span>Billing entries</span>
            <strong>{summary.billings.length}</strong>
          </div>
        </div>
      ) : null}
    </div>
  )
}

export default DashboardPage
