import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { API_BASE, normalizeList, readJson, writeJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

const STATUS_FLOW = ['ARRIVED', 'TRIAGED', 'ADMITTED', 'IN_TREATMENT', 'DISCHARGED']
const DEFAULT_FORM = {
  departmentName: '',
  assignedDoctorId: '',
  triageNotes: '',
  admissionNotes: '',
  dischargeNotes: '',
}

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
  const [doctors, setDoctors] = useState([])
  const [workflowForm, setWorkflowForm] = useState({})

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
      setWorkflowForm(
        nextHistory.reduce((acc, encounter) => {
          acc[encounter.id] = {
            departmentName: encounter.departmentName || '',
            assignedDoctorId: encounter.assignedDoctorId || '',
            triageNotes: encounter.triageNotes || '',
            admissionNotes: encounter.admissionNotes || '',
            dischargeNotes: encounter.dischargeNotes || '',
          }
          return acc
        }, {}),
      )

      const doctorPayload = await readJson(`${API_BASE}/api/patients/doctors/assignable`)
      setDoctors(normalizeList(doctorPayload))
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
        body: JSON.stringify({ status: nextStatus, ...(workflowForm[encounterId] || DEFAULT_FORM) }),
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

    const handleWorkflowChange = (encounterId, field, value) => {
      setWorkflowForm((prev) => ({
        ...prev,
        [encounterId]: {
          ...(prev[encounterId] || DEFAULT_FORM),
          [field]: value,
        },
      }))
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
            <Link to={`/patients/${id}/edit`} className="rounded-xl btn-brand px-4 py-2 text-sm font-semibold transition">
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

      <div className="grid gap-6 xl:grid-cols-3">
        <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Lab activity</div>
          <h2 className="mt-1 text-xl font-bold text-slate-900">Lab reports</h2>
          <div className="mt-4 space-y-3">
            {(patient.labReports || []).length === 0 ? <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No lab activity yet.</div> : patient.labReports.map((report) => (
              <div key={report.id} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="text-sm font-semibold text-slate-900">{report.testName || 'Lab test'}</div>
                <div className="mt-1 text-sm text-slate-600">Requested by: {report.requestedBy || 'N/A'}</div>
                <div className="mt-1 text-sm text-slate-600">Report date: {report.reportDate || 'N/A'}</div>
                <div className="mt-2 text-sm text-slate-700">{report.result || 'Pending result'}</div>
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Pharmacy activity</div>
          <h2 className="mt-1 text-xl font-bold text-slate-900">Prescriptions</h2>
          <div className="mt-4 space-y-3">
            {(patient.prescriptions || []).length === 0 ? <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No pharmacy activity yet.</div> : patient.prescriptions.map((prescription) => (
              <div key={prescription.id} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="text-sm font-semibold text-slate-900">{prescription.medicationName || 'Medication'}</div>
                <div className="mt-1 text-sm text-slate-600">{prescription.dosage || 'N/A'} • {prescription.frequency || 'N/A'}</div>
                <div className="mt-1 text-sm text-slate-600">Status: {prescription.status || 'N/A'}</div>
                <div className="mt-1 text-sm text-slate-600">Total cost: {prescription.totalCost || '0'}</div>
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Billing activity</div>
          <h2 className="mt-1 text-xl font-bold text-slate-900">Billing</h2>
          <div className="mt-4 space-y-3">
            {(patient.billingRecords || []).length === 0 ? <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No billing activity yet.</div> : patient.billingRecords.map((billing) => (
              <div key={billing.id} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="text-sm font-semibold text-slate-900">{billing.paymentMethod || 'Billing record'}</div>
                <div className="mt-1 text-sm text-slate-600">Amount: {billing.totalAmount || '0'}</div>
                <div className="mt-1 text-sm text-slate-600">Date: {billing.billingDate || 'N/A'}</div>
                <div className="mt-1 text-sm text-slate-600">Paid: {billing.paid || billing.isPaid ? 'Yes' : 'No'}</div>
              </div>
            ))}
          </div>
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
                    <span>Doctor: {encounter.assignedDoctorName || encounter.assignedDoctorId || 'Unassigned'}</span>
                  </div>

                  {encounter.triageNotes ? (
                    <div className="mt-3 rounded-xl border border-slate-200 bg-white p-3 text-sm text-slate-700">
                      {encounter.triageNotes}
                    </div>
                  ) : null}

                  <div className="mt-4 grid gap-4 md:grid-cols-2">
                    <label className="grid gap-2 text-sm font-medium text-slate-700">
                      Department
                      <input
                        value={workflowForm[encounter.id]?.departmentName || ''}
                        onChange={(event) => handleWorkflowChange(encounter.id, 'departmentName', event.target.value)}
                        className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500"
                        placeholder="Emergency"
                      />
                    </label>
                    <label className="grid gap-2 text-sm font-medium text-slate-700">
                      Assigned doctor
                      <select
                        value={workflowForm[encounter.id]?.assignedDoctorId || ''}
                        onChange={(event) => handleWorkflowChange(encounter.id, 'assignedDoctorId', event.target.value)}
                        className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500"
                      >
                        <option value="">Unassigned</option>
                        {doctors.map((doctor) => (
                          <option key={doctor.id} value={doctor.id}>
                            {doctor.fullName || [doctor.firstName, doctor.lastName].filter(Boolean).join(' ') || doctor.id}
                          </option>
                        ))}
                      </select>
                    </label>
                    <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
                      Triage notes
                      <textarea
                        value={workflowForm[encounter.id]?.triageNotes || ''}
                        onChange={(event) => handleWorkflowChange(encounter.id, 'triageNotes', event.target.value)}
                        className="min-h-24 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500"
                        placeholder="Vitals, symptoms, and triage assessment"
                      />
                    </label>
                    <label className="grid gap-2 text-sm font-medium text-slate-700">
                      Admission notes
                      <textarea
                        value={workflowForm[encounter.id]?.admissionNotes || ''}
                        onChange={(event) => handleWorkflowChange(encounter.id, 'admissionNotes', event.target.value)}
                        className="min-h-24 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500"
                        placeholder="Ward, bed, and admission summary"
                      />
                    </label>
                    <label className="grid gap-2 text-sm font-medium text-slate-700">
                      Discharge notes
                      <textarea
                        value={workflowForm[encounter.id]?.dischargeNotes || ''}
                        onChange={(event) => handleWorkflowChange(encounter.id, 'dischargeNotes', event.target.value)}
                        className="min-h-24 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500"
                        placeholder="Outcome, medications, and follow-up plan"
                      />
                    </label>
                  </div>

                  {(encounter.admittedAt || encounter.dischargedAt) ? (
                    <div className="mt-4 flex flex-wrap items-center gap-2 text-sm text-slate-600">
                      {encounter.admittedAt ? <span>Admitted: {new Date(encounter.admittedAt).toLocaleString()}</span> : null}
                      {encounter.admittedAt && encounter.dischargedAt ? <span>•</span> : null}
                      {encounter.dischargedAt ? <span>Discharged: {new Date(encounter.dischargedAt).toLocaleString()}</span> : null}
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
