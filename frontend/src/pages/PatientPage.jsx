import { useEffect, useState } from 'react'
import DetailModal from '../components/DetailModal'
import { API_BASE, normalizeList, readJson } from '../lib/api'

function PatientPage() {
  const [patients, setPatients] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedPatient, setSelectedPatient] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)

  useEffect(() => {
    const loadPatients = async () => {
      setLoading(true)
      setError('')

      try {
        const payload = await readJson(`${API_BASE}/api/patients/all?page=0&size=20`)
        setPatients(normalizeList(payload))
      } catch (err) {
        setError(err.message || 'Unable to load patient records.')
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
      setSelectedPatient({
        name: patient.name || patient.fullName || 'Patient',
        error: err.message || 'Unable to fetch patient details.',
      })
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
    <div className="page-panel">
      <div className="page-head">
        <div>
          <div className="eyebrow">Patient operations</div>
          <h1>Patients</h1>
        </div>
      </div>

      {loading ? <div className="status info">Loading patients...</div> : null}
      {error ? <div className="status error">{error}</div> : null}

      {!loading && !error ? (
        <div className="module-list">
          {patients.length > 0 ? patients.map((patient, index) => {
            const name = patient.name || patient.fullName || [patient.firstName, patient.lastName].filter(Boolean).join(' ') || `Patient ${index + 1}`
            const email = patient.email || patient.emailAddress || 'N/A'
            const phone = patient.phone || patient.phoneNumber || 'N/A'

            return (
              <div key={patient.id || patient.patientId || `${name}-${index}`} className="module-card">
                <div className="module-main">
                  <strong>{name}</strong>
                  <span>{email}</span>
                </div>
                <div className="module-actions">
                  <small>{phone}</small>
                  <button type="button" className="secondary-button" onClick={() => openPatientDetails(patient)}>
                    View details
                  </button>
                </div>
              </div>
            )
          }) : <div className="status info">No patient records available.</div>}
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
