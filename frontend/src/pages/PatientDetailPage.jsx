import { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
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
const DEPARTMENT_OPTIONS = ['GENERAL', 'CARDIOLOGY', 'ORTHOPEDICS', 'PEDIATRICS', 'NEUROLOGY', 'DERMATOLOGY', 'LABORATORY']

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
        <p className="mt-3 text-sm text-slate-600">Patient: <strong>{visit?.patientName || 'N/A'}</strong> ({visit?.mrn || 'N/A'})</p>
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

function ReferModal({ visit, doctors, currentDoctorId, onClose, onSubmit, submitting }) {
  const [form, setForm] = useState({ assignedDoctorId: '', departmentName: visit?.departmentName || '', triageNotes: '' })
  const [error, setError] = useState('')

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (!form.assignedDoctorId) {
      setError('Select a specialist to refer this patient to.')
      return
    }
    setError('')
    try {
      await onSubmit(form)
    } catch (err) {
      setError(err.message || 'Unable to refer patient.')
    }
  }

  const specialists = (doctors || []).filter((doctor) => doctor.id !== currentDoctorId)

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4" onClick={onClose} role="dialog" aria-modal="true">
      <div className="w-full max-w-lg rounded-3xl bg-white p-5 shadow-2xl" onClick={(event) => event.stopPropagation()}>
        <div className="flex items-start justify-between gap-4 border-b border-slate-200 pb-4">
          <h3 className="text-xl font-bold text-slate-900">Refer to specialist</h3>
          <button type="button" className="rounded-full border border-slate-300 px-3 py-1 text-lg leading-none text-slate-600" onClick={onClose} aria-label="Close">×</button>
        </div>
        <p className="mt-3 text-sm text-slate-600">Patient: <strong>{visit?.patientName || 'N/A'}</strong> ({visit?.mrn || 'N/A'})</p>
        <form className="mt-4 grid gap-4" onSubmit={handleSubmit}>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Refer to doctor
            <select name="assignedDoctorId" required value={form.assignedDoctorId} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
              <option value="">Select doctor</option>
              {specialists.map((doctor) => (
                <option key={doctor.id} value={doctor.id}>
                  {doctor.fullName || doctor.id}{doctor.specialization ? ` - ${doctor.specialization}` : ''}
                </option>
              ))}
            </select>
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Department
            <input name="departmentName" value={form.departmentName} onChange={handleChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Cardiology" />
          </label>
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Referral notes
            <textarea name="triageNotes" value={form.triageNotes} onChange={handleChange} className="min-h-24 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Reason for referral" />
          </label>
          {error ? <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
          <div className="flex items-center justify-end gap-3">
            <button type="button" onClick={onClose} className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">Cancel</button>
            <button type="submit" disabled={submitting} className="rounded-xl bg-emerald-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-70">
              {submitting ? 'Referring...' : 'Refer patient'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

function PatientDetailPage() {
  const { id } = useParams()
  const showError = useErrorRedirect()
  const [patient, setPatient] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [updating, setUpdating] = useState(false)
  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('success')
  const [doctors, setDoctors] = useState([])
  const [workflowForm, setWorkflowForm] = useState({})
  const [currentDoctor, setCurrentDoctor] = useState(null)
  const [prescribeTarget, setPrescribeTarget] = useState(null)
  const [referTarget, setReferTarget] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const handlePrescribe = async (form) => {
    if (!currentDoctor?.userId) {
      throw new Error('Only a logged-in doctor can prescribe medication.')
    }
    setSubmitting(true)
    try {
      await writeJson(`${API_BASE}/api/prescriptions/create`, {
        method: 'POST',
        body: JSON.stringify({
          patientId: prescribeTarget.patientId,
          medicationName: form.medicationName,
          dosage: form.dosage,
          frequency: form.frequency,
          quantity: form.quantity,
          department: form.department,
          prescribedBy: currentDoctor.userId,
          prescriptionDate: new Date().toISOString().slice(0, 10),
        }),
      })
      setMessage(`Prescription created for ${prescribeTarget.patientName || 'patient'}.`)
      setMessageType('success')
      setPrescribeTarget(null)
    } finally {
      setSubmitting(false)
    }
  }

  const handleRefer = async (form) => {
    if (!referTarget?.encounterId) {
      throw new Error('No active encounter found to refer.')
    }
    setSubmitting(true)
    try {
      await writeJson(`${API_BASE}/api/patients/encounters/${referTarget.encounterId}/status`, {
        method: 'PATCH',
        body: JSON.stringify({
          status: referTarget.status || 'ARRIVED',
          assignedDoctorId: form.assignedDoctorId,
          departmentName: form.departmentName,
          triageNotes: form.triageNotes,
        }),
      })
      setMessage(`Patient ${referTarget.patientName || ''} referred successfully.`)
      setMessageType('success')
      setReferTarget(null)
      await loadPatient()
    } finally {
      setSubmitting(false)
    }
  }

  const fullName = useMemo(() => {
    if (!patient) return 'Patient'
    return [patient.firstName, patient.lastName].filter(Boolean).join(' ') || patient.name || 'Patient'
  }, [patient])

  const activeEncounter = history[0] || null

  const activeVisit = useMemo(() => {
    if (!patient) return null
    return {
      encounterId: activeEncounter?.id || null,
      patientId: patient.id,
      patientName: fullName,
      mrn: patient.mrn,
      departmentName: activeEncounter?.departmentName || '',
      status: activeEncounter?.status || 'ARRIVED',
    }
  }, [patient, fullName, activeEncounter])

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

      try {
        const currentDoctorPayload = await readJson(`${API_BASE}/api/doctors/me`)
        setCurrentDoctor(currentDoctorPayload?.data || currentDoctorPayload)
      } catch {
        setCurrentDoctor(null)
      }
    } catch (err) {
      const nextMessage = err.message || 'Unable to load patient details.'
      showError(nextMessage)
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
            {currentDoctor ? (
              <>
                <button
                  type="button"
                  disabled={!activeVisit?.encounterId}
                  onClick={() => setPrescribeTarget(activeVisit)}
                  className="rounded-lg btn-brand px-3 py-1.5 text-xs font-semibold disabled:cursor-not-allowed disabled:opacity-60"
                >
                  Prescribe
                </button>
                <Link
                  to={`/lab-reports/new?patientId=${patient.id}&requestedBy=${currentDoctor?.id || ''}`}
                  className="rounded-lg border border-blue-300 bg-blue-50 px-3 py-1.5 text-xs font-semibold text-blue-700 transition hover:bg-blue-100"
                >
                  Request lab
                </Link>
                <button
                  type="button"
                  disabled={!activeVisit?.encounterId}
                  onClick={() => setReferTarget(activeVisit)}
                  className="rounded-lg border border-amber-300 bg-amber-50 px-3 py-1.5 text-xs font-semibold text-amber-700 transition hover:bg-amber-100 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  Refer
                </button>
              </>
            ) : null}
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

      {prescribeTarget ? (
        <PrescribeModal
          visit={prescribeTarget}
          submitting={submitting}
          onClose={() => setPrescribeTarget(null)}
          onSubmit={handlePrescribe}
        />
      ) : null}

      {referTarget ? (
        <ReferModal
          visit={referTarget}
          doctors={doctors}
          currentDoctorId={currentDoctor?.id}
          submitting={submitting}
          onClose={() => setReferTarget(null)}
          onSubmit={handleRefer}
        />
      ) : null}
    </div>
  )
}



export default PatientDetailPage
