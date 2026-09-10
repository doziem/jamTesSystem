import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import DetailModal from '../components/DetailModal'
import { API_BASE, normalizeList, readJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

function PatientPage() {
  const [patients, setPatients] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedPatient, setSelectedPatient] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const showError = useErrorRedirect()

  useEffect(() => {
    const loadPatients = async () => {
      setLoading(true)
      setError('')

      try {
        const payload = await readJson(`${API_BASE}/api/patients/all?page=0&size=20`)
        setPatients(normalizeList(payload))
      } catch (err) {
        const message = err.message || 'Unable to load patient records.'
        setError(message)
        showError(message)
      } finally {
        setLoading(false)
      }
    }

    loadPatients()
  }, [])

  const openPatientDetails = async (patient) => {
    const patientId = patient?.id || patient?.patientId
    if (!patientId) {
      return
    }

    setDetailLoading(true)
    setSelectedPatient(null)

    try {
      const payload = await readJson(`${API_BASE}/api/patients/${patientId}`)
      const detail = payload?.data || payload
      setSelectedPatient(detail)
    } catch (err) {
      const message = err.message || 'Unable to fetch patient details.'
      setSelectedPatient({
        name: patient.name || patient.fullName || 'Patient',
        error: message,
      })
      showError(message)
    } finally {
      setDetailLoading(false)
    }
  }

  const detailFields = selectedPatient
    ? [
        ['ID', selectedPatient.id || selectedPatient.patientId || 'N/A'],
        ['Full name', selectedPatient.name || selectedPatient.fullName || [selectedPatient.firstName, selectedPatient.lastName].filter(Boolean).join(' ') || 'N/A'],
        ['Email', selectedPatient.email || selectedPatient.emailAddress || 'N/A'],
        ['Phone', selectedPatient.phone || selectedPatient.phoneNumber || 'N/A'],
        ['Gender', selectedPatient.gender || 'N/A'],
        ['Date of birth', selectedPatient.dateOfBirth || 'N/A'],
        ['Status', selectedPatient.active ? 'Active' : 'Inactive'],
        ['Error', selectedPatient.error || ''],
      ].filter(([, value]) => value !== '')
    : []

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Patient operations</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Patients</h1>
        </div>
        <Link to="/patients/new" className="rounded-xl btn-brand px-4 py-2 text-sm font-semibold transition">
          Create patient
        </Link>
      </div>

      {loading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading patients...</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

      {!loading && !error ? (
        <div className="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {patients.length > 0 ? patients.map((patient, index) => {
            const name = patient.name || patient.fullName || [patient.firstName, patient.lastName].filter(Boolean).join(' ') || `Patient ${index + 1}`
            const email = patient.email || patient.emailAddress || 'N/A'
            const phone = patient.phone || patient.phoneNumber || 'N/A'
            const patientId = patient.id || patient.patientId
            const mrn = patient.mrn || 'N/A'

            return (
              <div key={patientId || `${name}-${index}`} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <strong className="block text-base font-semibold text-slate-900">{name}</strong>
                    <span className="mt-1 block text-xs font-semibold uppercase tracking-[0.12em] text-blue-600">MRN: {mrn}</span>
                    <span className="mt-1 block text-sm text-slate-600">{email}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Link to={`/patients/${patientId}`} className="rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-100">
                      View
                    </Link>
                    <Link to={`/patients/${patientId}/edit`} className="rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-100">
                      Edit
                    </Link>
                    <button type="button" className="rounded-xl btn-brand px-3 py-2 text-sm font-semibold" onClick={() => openPatientDetails(patient)}>
                      Quick view
                    </button>
                  </div>
                </div>
                <small className="mt-3 block text-sm text-slate-500">{phone}</small>
              </div>
            )
          }) : <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No patient records available.</div>}
        </div>
      ) : null}

      <DetailModal
        isOpen={Boolean(selectedPatient)}
        title={selectedPatient?.name || selectedPatient?.fullName || 'Patient details'}
        fields={detailFields}
        loading={detailLoading}
        onClose={() => setSelectedPatient(null)}
      />
    </div>
  )
}

export default PatientPage
