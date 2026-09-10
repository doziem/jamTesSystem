import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { API_BASE, normalizeList, readJson, writeJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

const STATUS_FLOW = ['ARRIVED', 'TRIAGED', 'ADMITTED', 'IN_TREATMENT', 'DISCHARGED']

function formatStatusLabel(status) {
  return (status || 'ARRIVED').replaceAll('_', ' ')
}

function PatientDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const showError = useErrorRedirect()
  const [patient, setPatient] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [updating, setUpdating] = useState(false)
  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('success')

  const fullName = useMemo(() => {
    if (!patient) return 'Patient'
    return [patient.firstName, patient.lastName].filter(Boolean).join(' ') || patient.name || 'Patient'
  }, [patient])

  const loadPatient = async () => {
    setLoading(true)
    try {
      const patientPayload = await readJson(`${API_BASE}/api/patients/${id}`)
      const nextPatient = patientPayload?.data || patientPayload
      setPatient(nextPatient)

      const historyPayload = await readJson(`${API_BASE}/api/patients/${id}/history`)
      const nextHistory = normalizeList(historyPayload)
      setHistory(nextHistory)
    } catch (err) {
      const nextMessage = err.message || 'Unable to load patient details.'
      showError(nextMessage)
      navigate('/error', { replace: true, state: { message: nextMessage } })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (id) {
      loadPatient()
    }
  }, [id])

  const updateStatus = async (encounterId, nextStatus) => {
    setUpdating(true)
    setMessage('')
    setMessageType('success')

    try {
      await writeJson(`${API_BASE}/api/patients/encounters/${encounterId}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ status: nextStatus }),
      })
      setMessage(`Encounter status updated to ${formatStatusLabel(nextStatus)}.`)
      await loadPatient()
    } catch (err) {
      const nextMessage = err.message || 'Unable to update encounter status.'
      setMessage(nextMessage)
      setMessageType('error')
      showError(nextMessage)
    } finally {
      setUpdating(false)
    }
  }

  if (loading) {
    return <div className="rounded-3xl border border-slate-200 bg-white p-6 text-sm text-slate-700">Loading patient details...</div>
  }

  if (!patient) {
    return <div className="rounded-3xl border border-slate-200 bg-white p-6 text-sm text-slate-700">Patient not found.</div>
  }

  return (
    <div className="space-y-6">
      <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
        <div className="flex flex-col gap-4 border-b border-slate-200 pb-5 md:flex-row md:items-center md:justify-between">
          <div>
            <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Patient profile</div>
            <h1 className="mt-2 text-2xl font-bold text-slate-900 sm:text-3xl">{fullName}</h1>
          </div>
          <div className="flex items-center gap-3">
            <span className="rounded-full bg-blue-100 px-3 py-1 text-sm font-semibold text-blue-800">MRN: {patient.mrn || 'N/A'}</span>
            <Link to={`/patients/${id}/edit`} className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-700">
              Edit patient
            </Link>
          </div>
        </div>

        <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <div className="rounded-2xl bg-slate-50 p-4"><div className="text-xs uppercase tracking-[0.14em] text-slate-500">Email</div><div className="mt-2 text-sm font-semibold text-slate-900">{patient.email || 'N/A'}</div></div>
          <div className="rounded-2xl bg-slate-50 p-4"><div className="text-xs uppercase tracking-[0.14em] text-slate-500">Phone</div><div className="mt-2 text-sm font-semibold text-slate-900">{patient.phone || 'N/A'}</div></div>
          <div className="rounded-2xl bg-slate-50 p-4"><div className="text-xs uppercase tracking-[0.14em] text-slate-500">Date of birth</div><div className="mt-2 text-sm font-semibold text-slate-900">{patient.dateOfBirth || 'N/A'}</div></div>
          <div className="rounded-2xl bg-slate-50 p-4"><div className="text-xs uppercase tracking-[0.14em] text-slate-500">Gender</div><div className="mt-2 text-sm font-semibold text-slate-900">{patient.gender || 'N/A'}</div></div>
        </div>
      </div>

      <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
        <div className="flex items-center justify-between gap-3 pb-4">
          <div>
            <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Visit history</div>
            <h2 className="mt-1 text-xl font-bold text-slate-900">Encounters</h2>
          </div>
        </div>

        {message ? (
          <div className={`mb-4 rounded-2xl px-4 py-3 text-sm ${messageType === 'error' ? 'bg-red-50 text-red-800' : 'bg-emerald-50 text-emerald-800'}`}>
            {message}
          </div>
        ) : null}

        {history.length === 0 ? (
          <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No visit history recorded for this patient yet.</div>
        ) : (
          <div className="space-y-4">
            {history.map((encounter) => {
              const currentIndex = STATUS_FLOW.indexOf(encounter.status || 'ARRIVED')
              const encounterStatus = encounter.status || 'ARRIVED'
              const nextStep = STATUS_FLOW[currentIndex + 1]

              return (
                <div key={encounter.id} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                  <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                    <div>
                      <div className="text-base font-semibold text-slate-900">{encounter.visitType || 'OPD'}</div>
                      <div className="mt-1 text-sm text-slate-600">Arrival: {encounter.arrivalTime ? new Date(encounter.arrivalTime).toLocaleString() : 'N/A'}</div>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.1em] text-emerald-800">
                        {formatStatusLabel(encounterStatus)}
                      </span>
                    </div>
                  </div>

                  <div className="mt-4 flex flex-wrap items-center gap-2 text-sm text-slate-600">
                    <span>Department: {encounter.departmentName || 'Reception'}</span>
                    <span>•</span>
                    <span>Doctor: {encounter.assignedDoctorId || 'Unassigned'}</span>
                  </div>

                  {encounter.triageNotes ? (
                    <div className="mt-3 rounded-xl border border-slate-200 bg-white p-3 text-sm text-slate-700">
                      {encounter.triageNotes}
                    </div>
                  ) : null}

                  <div className="mt-4 flex flex-wrap gap-2">
                    {STATUS_FLOW.map((status) => {
                      const isCurrent = status === encounterStatus
                      const isNext = status === nextStep
                      const isDisabled = updating || isCurrent || !isNext

                      return (
                        <button
                          key={status}
                          type="button"
                          disabled={isDisabled}
                          onClick={() => updateStatus(encounter.id, status)}
                          className={`rounded-xl px-4 py-2 text-sm font-semibold transition ${
                            isCurrent
                              ? 'bg-blue-600 text-white'
                              : isNext
                                ? 'bg-emerald-600 text-white hover:bg-emerald-700 disabled:opacity-60'
                                : 'border border-slate-300 bg-white text-slate-400'
                          } disabled:cursor-not-allowed`}
                        >
                          {formatStatusLabel(status)}
                        </button>
                      )
                    })}
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}

export default PatientDetailPage
