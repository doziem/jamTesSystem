import { useEffect, useState } from 'react'
import DetailModal from '../components/DetailModal'
import { API_BASE, normalizeList, readJson } from '../lib/api'

function DoctorPage() {
  const [doctors, setDoctors] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedDoctor, setSelectedDoctor] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)

  useEffect(() => {
    const loadDoctors = async () => {
      setLoading(true)
      setError('')

      try {
        const payload = await readJson(`${API_BASE}/api/doctors/all`)
        setDoctors(normalizeList(payload))
      } catch (err) {
        setError(err.message || 'Unable to load doctor records.')
      } finally {
        setLoading(false)
      }
    }

    loadDoctors()
  }, [])

  const openDoctorDetails = async (doctor) => {
    const doctorId = doctor?.id || doctor?.doctorId
    if (!doctorId) {
      return
    }

    setDetailLoading(true)
    setSelectedDoctor(null)

    try {
      const detail = await readJson(`${API_BASE}/api/doctors/${doctorId}/single`)
      setSelectedDoctor(detail)
    } catch (err) {
      setSelectedDoctor({
        firstName: doctor.firstName || '',
        lastName: doctor.lastName || '',
        error: err.message || 'Unable to fetch doctor details.',
      })
    } finally {
      setDetailLoading(false)
    }
  }

  const detailFields = selectedDoctor
    ? [
        ['ID', selectedDoctor.id || selectedDoctor.doctorId || 'N/A'],
        ['Full name', [selectedDoctor.firstName, selectedDoctor.lastName].filter(Boolean).join(' ') || 'N/A'],
        ['Specialization', selectedDoctor.specialization || selectedDoctor.specialty || 'N/A'],
        ['Experience', selectedDoctor.experience ? `${selectedDoctor.experience} years` : 'N/A'],
        ['Availability', selectedDoctor.availability || 'N/A'],
        ['User ID', selectedDoctor.userId || 'N/A'],
        ['Error', selectedDoctor.error || ''],
      ].filter(([, value]) => value !== '')
    : []

  return (
    <div className="page-panel">
      <div className="page-head">
        <div>
          <div className="eyebrow">Doctor operations</div>
          <h1>Doctors</h1>
        </div>
      </div>

      {loading ? <div className="status info">Loading doctors...</div> : null}
      {error ? <div className="status error">{error}</div> : null}

      {!loading && !error ? (
        <div className="module-list">
          {doctors.length > 0 ? doctors.map((doctor, index) => {
            const name = doctor.name || doctor.fullName || [doctor.firstName, doctor.lastName].filter(Boolean).join(' ') || `Doctor ${index + 1}`
            const specialty = doctor.specialty || doctor.specialization || doctor.department || 'General Practice'
            const email = doctor.email || 'N/A'

            return (
              <div key={doctor.id || doctor.doctorId || `${name}-${index}`} className="module-card">
                <div className="module-main">
                  <strong>{name}</strong>
                  <span>{specialty}</span>
                </div>
                <div className="module-actions">
                  <small>{email}</small>
                  <button type="button" className="secondary-button" onClick={() => openDoctorDetails(doctor)}>
                    View details
                  </button>
                </div>
              </div>
            )
          }) : <div className="status info">No doctor records available.</div>}
        </div>
      ) : null}

      <DetailModal
        isOpen={Boolean(selectedDoctor)}
        title={[selectedDoctor?.firstName, selectedDoctor?.lastName].filter(Boolean).join(' ') || 'Doctor details'}
        fields={detailFields}
        loading={detailLoading}
        onClose={() => setSelectedDoctor(null)}
      />
    </div>
  )
}

export default DoctorPage
