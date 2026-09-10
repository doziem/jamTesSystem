import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import DetailModal from '../components/DetailModal'
import { API_BASE, normalizeList, readJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

function DoctorPage() {
  const [doctors, setDoctors] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedDoctor, setSelectedDoctor] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const showError = useErrorRedirect()

  useEffect(() => {
    const loadDoctors = async () => {
      setLoading(true)
      setError('')

      try {
        const payload = await readJson(`${API_BASE}/api/doctors/all`)
        setDoctors(normalizeList(payload))
      } catch (err) {
        const message = err.message || 'Unable to load doctor records.'
        setError(message)
        showError(message)
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
      const message = err.message || 'Unable to fetch doctor details.'
      setSelectedDoctor({
        firstName: doctor.firstName || '',
        lastName: doctor.lastName || '',
        error: message,
      })
      showError(message)
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
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Doctor operations</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Doctors</h1>
        </div>
        <Link to="/doctors/new" className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-700">
          Create doctor
        </Link>
      </div>

      {loading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading doctors...</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

      {!loading && !error ? (
        <div className="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {doctors.length > 0 ? doctors.map((doctor, index) => {
            const name = doctor.name || doctor.fullName || [doctor.firstName, doctor.lastName].filter(Boolean).join(' ') || `Doctor ${index + 1}`
            const specialty = doctor.specialty || doctor.specialization || doctor.department || 'General Practice'
            const email = doctor.email || 'N/A'

            return (
              <div key={doctor.id || doctor.doctorId || `${name}-${index}`} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <strong className="block text-base font-semibold text-slate-900">{name}</strong>
                    <span className="mt-1 block text-sm text-slate-600">{specialty}</span>
                  </div>
                  <button type="button" className="rounded-xl bg-blue-600 px-3 py-2 text-sm font-semibold text-white" onClick={() => openDoctorDetails(doctor)}>
                    View details
                  </button>
                </div>
                <small className="mt-3 block text-sm text-slate-500">{email}</small>
              </div>
            )
          }) : <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No doctor records available.</div>}
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
