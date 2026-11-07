import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { API_BASE, USER_STORAGE_KEY, normalizeList, readJson, writeJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

const DEPARTMENT_OPTIONS = ['GENERAL', 'CARDIOLOGY', 'ORTHOPEDICS', 'PEDIATRICS', 'NEUROLOGY', 'DERMATOLOGY', 'LABORATORY']

function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_STORAGE_KEY) || 'null')
  } catch {
    return null
  }
}

function toDateKey(value) {
  if (!value) return 'Unknown date'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return 'Unknown date'
  return date.toISOString().slice(0, 10)
}

function formatDayHeading(dateKey) {
  if (dateKey === 'Unknown date') return dateKey
  const todayKey = new Date().toISOString().slice(0, 10)
  const yesterday = new Date()
  yesterday.setDate(yesterday.getDate() - 1)
  const yesterdayKey = yesterday.toISOString().slice(0, 10)

  if (dateKey === todayKey) return `Today • ${dateKey}`
  if (dateKey === yesterdayKey) return `Yesterday • ${dateKey}`
  return dateKey
}

function calculateAge(dob) {
  if (!dob) return 'N/A'
  const birth = new Date(dob)
  if (Number.isNaN(birth.getTime())) return 'N/A'
  const ageMs = Date.now() - birth.getTime()
  const age = Math.floor(ageMs / (1000 * 60 * 60 * 24 * 365.25))
  return age >= 0 ? `${age}y` : 'N/A'
}

function formatStatusLabel(status) {
  return (status || 'ARRIVED').replaceAll('_', ' ')
}

function PrescribeModal({ visit, onClose, onSubmit, submitting }) {
  const [form, setForm] = useState({ medicationName: '', dosage: '', frequency: '', quantity: 1, department: 'GENERAL' })
  const [error, setError] = useState('')

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: name === 'quantity' ? Number(value) : value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (!form.medicationName.trim()) {
      setError('Medication name is required.')
      return
    }
    setError('')
    try {
      await onSubmit(form)
    } catch (err) {
      setError(err.message || 'Unable to prescribe medication.')
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4" onClick={onClose} role="dialog" aria-modal="true">
      <div className="w-full max-w-lg rounded-3xl bg-white p-5 shadow-2xl" onClick={(event) => event.stopPropagation()}>
        <div className="flex items-start justify-between gap-4 border-b border-slate-200 pb-4">
          <h3 className="text-xl font-bold text-slate-900">Prescribe medication</h3>
          <button type="button" className="rounded-full border border-slate-300 px-3 py-1 text-lg leading-none text-slate-600" onClick={onClose} aria-label="Close">×</button>
        </div>
        <p className="mt-3 text-sm text-slate-600">Patient: <strong>{visit.patientName || 'N/A'}</strong> ({visit.mrn || 'N/A'})</p>
        <form className="mt-4 grid gap-4" onSubmit={handleSubmit}>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Medication name
            <input name="medicationName" required value={form.medicationName} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Amoxicillin" />
          </label>
          <div className="grid gap-4 sm:grid-cols-2">
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Dosage
              <input name="dosage" value={form.dosage} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="500mg" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Frequency
              <input name="frequency" value={form.frequency} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Twice daily" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Quantity
              <input type="number" min="1" name="quantity" value={form.quantity} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Department
              <select name="department" value={form.department} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
                {DEPARTMENT_OPTIONS.map((option) => (
                  <option key={option} value={option}>{option}</option>
                ))}
              </select>
            </label>
          </div>
          {error ? <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
          <div className="flex items-center justify-end gap-3">
            <button type="button" onClick={onClose} className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">Cancel</button>
            <button type="submit" disabled={submitting} className="rounded-xl btn-brand px-5 py-2.5 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-70">
              {submitting ? 'Prescribing...' : 'Prescribe medication'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

function DoctorMyPatientsPage() {
  const showError = useErrorRedirect()
  const [doctor, setDoctor] = useState(null)
  const [visits, setVisits] = useState([])
  const [dashboard, setDashboard] = useState(null)
  const [assignableDoctors, setAssignableDoctors] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const loadSchedule = async () => {
    setLoading(true)
    setError('')

    try {
      const doctorPayload = await readJson(`${API_BASE}/api/doctors/me`)
      const currentDoctor = doctorPayload?.data || doctorPayload
      setDoctor(currentDoctor)

      const [visitPayload, assignablePayload, dashboardPayload] = await Promise.all([
        readJson(`${API_BASE}/api/doctors/${currentDoctor.id}/patients`),
        readJson(`${API_BASE}/api/patients/doctors/assignable`),
        readJson(`${API_BASE}/api/doctors/${currentDoctor.id}/dashboard`),
      ])
      setVisits(normalizeList(visitPayload))
      setAssignableDoctors(normalizeList(assignablePayload))
      setDashboard(dashboardPayload)
    } catch (err) {
      const nextMessage = err.message || 'Unable to load your assigned patients.'
      setError(nextMessage)
      showError(nextMessage)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadSchedule()
  }, [])

  const groupedVisits = useMemo(() => {
    const groups = new Map()
    visits.forEach((visit) => {
      const dateKey = toDateKey(visit.arrivalTime)
      if (!groups.has(dateKey)) {
        groups.set(dateKey, [])
      }
      groups.get(dateKey).push(visit)
    })

    return Array.from(groups.entries())
      .sort(([a], [b]) => (a < b ? 1 : a > b ? -1 : 0))
      .map(([dateKey, dayVisits]) => [
        dateKey,
        dayVisits.slice().sort((a, b) => new Date(b.arrivalTime) - new Date(a.arrivalTime)),
      ])
  }, [visits])

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Doctor schedule</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">My patients</h1>
          <p className="mt-1 text-sm text-slate-600">{doctor?.fullName ? `Dr. ${doctor.fullName}` : ''} {doctor?.specialization ? `• ${doctor.specialization}` : ''}</p>
        </div>
      </div>

      {loading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading your patients...</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
      {message ? <div className="mt-4 rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{message}</div> : null}
      {!loading && !error ? (
        <section className="mt-5 rounded-2xl border border-slate-200 bg-slate-50 p-4">
          <h2 className="text-lg font-bold text-slate-900">Recent lab requests and results</h2>
          {(dashboard?.recentLabReports || []).length ? dashboard.recentLabReports.map((report) => (
            <div key={report.id} className="mt-3 rounded-xl border border-slate-200 bg-white p-3 text-sm">
              <Link to={`/patients/${report.patientId}`} className="font-semibold text-blue-700">Patient {report.patientId}</Link>
              {(report.testResults || []).map((test) => <p key={test.id || test.testName} className="mt-1 text-slate-700">{test.testName}: {test.result || 'Pending result'}</p>)}
            </div>
          )) : <p className="mt-2 text-sm text-slate-600">No lab requests yet.</p>}
        </section>
      ) : null}

      {!loading && !error ? (
        groupedVisits.length > 0 ? (
          <div className="mt-5 space-y-8">
            {groupedVisits.map(([dateKey, dayVisits]) => (
              <section key={dateKey}>
                <h2 className="text-sm font-bold uppercase tracking-[0.12em] text-blue-700">{formatDayHeading(dateKey)}</h2>
                <div className="mt-3 overflow-x-auto rounded-2xl border border-slate-200">
                  <table className="min-w-full divide-y divide-slate-200 text-sm">
                    <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-[0.08em] text-slate-500">
                      <tr>
                        <th className="px-4 py-3">Patient</th>
                        <th className="px-4 py-3">MRN</th>
                        <th className="px-4 py-3">Contact</th>
                        <th className="px-4 py-3">Gender / Age</th>
                        <th className="px-4 py-3">Visit</th>
                        <th className="px-4 py-3">Department</th>
                        <th className="px-4 py-3">Status</th>
                        <th className="px-4 py-3">Arrival</th>
                        <th className="px-4 py-3">Actions</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200 bg-white">
                      {dayVisits.map((visit) => (
                        <tr key={visit.encounterId}>
                          <td className="px-4 py-3 font-semibold text-slate-900">{visit.patientName || 'N/A'}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.mrn || 'N/A'}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.phone || 'N/A'}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.gender || 'N/A'} / {calculateAge(visit.dateOfBirth)}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.visitType || 'OPD'}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.departmentName || 'Reception'}</td>
                          <td className="px-4 py-3">
                            <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.08em] text-emerald-800">
                              {formatStatusLabel(visit.status)}
                            </span>
                          </td>
                          <td className="px-4 py-3 text-slate-600">{visit.arrivalTime ? new Date(visit.arrivalTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'N/A'}</td>
                          <td className="px-4 py-3">
                            <div className="flex flex-wrap gap-2">
                              <Link to={`/patients/${visit.patientId}`} className="rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-xs font-semibold text-slate-700 transition hover:bg-slate-100">
                                View details
                              </Link>

                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </section>
            ))}
          </div>
        ) : (
          <div className="mt-5 rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No patients have been assigned to you yet.</div>
        )
      ) : null}
    </div>
  )
}

function AllAssignedPatientsPage() {
  const showError = useErrorRedirect()
  const [visits, setVisits] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadAllPatients = async () => {
      setLoading(true)
      setError('')

      try {
        const payload = await readJson(`${API_BASE}/api/doctors/patients/all`)
        setVisits(normalizeList(payload))
      } catch (err) {
        const message = err.message || 'Unable to load assigned patients.'
        setError(message)
        showError(message)
      } finally {
        setLoading(false)
      }
    }

    loadAllPatients()
  }, [])

  const groupedVisits = useMemo(() => {
    const groups = new Map()
    visits.forEach((visit) => {
      const dateKey = toDateKey(visit.arrivalTime)
      if (!groups.has(dateKey)) {
        groups.set(dateKey, [])
      }
      groups.get(dateKey).push(visit)
    })

    return Array.from(groups.entries())
      .sort(([a], [b]) => (a < b ? 1 : a > b ? -1 : 0))
      .map(([dateKey, dayVisits]) => [
        dateKey,
        dayVisits.slice().sort((a, b) => new Date(b.arrivalTime) - new Date(a.arrivalTime)),
      ])
  }, [visits])

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Doctor operations</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Assigned patients</h1>
          <p className="mt-1 text-sm text-slate-600">Patients currently assigned to a doctor, grouped by day.</p>
        </div>
      </div>

      {loading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading assigned patients...</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

      {!loading && !error ? (
        groupedVisits.length > 0 ? (
          <div className="mt-5 space-y-8">
            {groupedVisits.map(([dateKey, dayVisits]) => (
              <section key={dateKey}>
                <h2 className="text-sm font-bold uppercase tracking-[0.12em] text-blue-700">{formatDayHeading(dateKey)}</h2>
                <div className="mt-3 overflow-x-auto rounded-2xl border border-slate-200">
                  <table className="min-w-full divide-y divide-slate-200 text-sm">
                    <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-[0.08em] text-slate-500">
                      <tr>
                        <th className="px-4 py-3">Patient</th>
                        <th className="px-4 py-3">MRN</th>
                        <th className="px-4 py-3">Contact</th>
                        <th className="px-4 py-3">Gender / Age</th>
                        <th className="px-4 py-3">Doctor</th>
                        <th className="px-4 py-3">Department</th>
                        <th className="px-4 py-3">Status</th>
                        <th className="px-4 py-3">Arrival</th>
                        <th className="px-4 py-3">Actions</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200 bg-white">
                      {dayVisits.map((visit) => (
                        <tr key={visit.encounterId}>
                          <td className="px-4 py-3 font-semibold text-slate-900">{visit.patientName || 'N/A'}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.mrn || 'N/A'}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.phone || 'N/A'}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.gender || 'N/A'} / {calculateAge(visit.dateOfBirth)}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.assignedDoctorName || 'Unassigned'}</td>
                          <td className="px-4 py-3 text-slate-600">{visit.departmentName || 'Reception'}</td>
                          <td className="px-4 py-3">
                            <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.08em] text-emerald-800">
                              {formatStatusLabel(visit.status)}
                            </span>
                          </td>
                          <td className="px-4 py-3 text-slate-600">{visit.arrivalTime ? new Date(visit.arrivalTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'N/A'}</td>
                          <td className="px-4 py-3">
                            <Link to={`/patients/${visit.patientId}`} className="rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-xs font-semibold text-slate-700 transition hover:bg-slate-100">
                              View details
                            </Link>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </section>
            ))}
          </div>
        ) : (
          <div className="mt-5 rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No patients have been assigned to a doctor yet.</div>
        )
      ) : null}
    </div>
  )
}

function DoctorPage() {
  const currentUser = useMemo(() => getStoredUser(), [])

  if (currentUser?.role === 'DOCTOR') {
    return <DoctorMyPatientsPage />
  }

  return <AllAssignedPatientsPage />
}

export default DoctorPage
