import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { API_BASE, readJson, writeJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

const defaultSearch = {
  mrn: '',
  phone: '',
  name: '',
  dob: '',
}

const defaultPatient = {
  mrn: '',
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  dateOfBirth: '',
  gender: 'MALE',
  address: {
    street: '',
    city: '',
    state: '',
    zipCode: '',
    country: '',
  },
  visitType: 'OPD',
  departmentName: 'Reception',
  assignedDoctorId: '',
  triageNotes: '',
}

function PatientArrivalPage() {
  const navigate = useNavigate()
  const showError = useErrorRedirect()
  const [search, setSearch] = useState(defaultSearch)
  const [patientForm, setPatientForm] = useState(defaultPatient)
  const [loading, setLoading] = useState(false)
  const [searchResult, setSearchResult] = useState(null)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const handleSearchChange = (event) => {
    const { name, value } = event.target
    setSearch((prev) => ({ ...prev, [name]: value }))
  }

  const handlePatientChange = (event) => {
    const { name, value } = event.target
    setPatientForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleAddressChange = (event) => {
    const { name, value } = event.target
    setPatientForm((prev) => ({
      ...prev,
      address: {
        ...prev.address,
        [name]: value,
      },
    }))
  }

  const handleSearch = async (event) => {
    event.preventDefault()
    setLoading(true)
    setError('')
    setSearchResult(null)

    try {
      const params = new URLSearchParams()
      if (search.mrn) params.set('mrn', search.mrn)
      if (search.phone) params.set('phone', search.phone)
      if (search.name) params.set('name', search.name)
      if (search.dob) params.set('dob', search.dob)

      const payload = await readJson(`${API_BASE}/api/patients/search?${params.toString()}`)
      const results = Array.isArray(payload?.data) ? payload.data : payload?.data ? [payload.data] : []
      if (results.length === 0) {
        setMessage('No matching patient found. You can register a new patient below.')
        return
      }

      setSearchResult(results[0])
      setMessage(`Matching patient found: ${results[0].mrn || results[0].id}`)
    } catch (err) {
      const nextMessage = err.message || 'Unable to search for patient.'
      setError(nextMessage)
      showError(nextMessage)
    } finally {
      setLoading(false)
    }
  }

  const handleArrival = async (event) => {
    event.preventDefault()
    setLoading(true)
    setError('')
    setMessage('')

    try {
      const payload = {
        ...patientForm,
        dateOfBirth: patientForm.dateOfBirth || null,
        address: {
          street: patientForm.address.street,
          city: patientForm.address.city,
          state: patientForm.address.state,
          zipCode: patientForm.address.zipCode,
          country: patientForm.address.country,
        },
      }

      const response = await writeJson(`${API_BASE}/api/patients/arrival`, {
        method: 'POST',
        body: JSON.stringify(payload),
      })

      const patient = response?.data?.patient || response?.patient
      const encounter = response?.data?.encounter || response?.encounter
      setMessage(
        `Patient checked in successfully. ${patient?.mrn || 'MRN assigned'} • ${encounter?.status || 'ARRIVED'}`,
      )
      setTimeout(() => navigate('/patients'), 700)
    } catch (err) {
      const nextMessage = err.message || 'Unable to complete patient arrival.'
      setError(nextMessage)
      showError(nextMessage)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex items-end justify-between gap-3 border-b border-slate-200 pb-4">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Patient arrival</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Check in patient</h1>
        </div>
      </div>

      {message ? <div className="mt-4 rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{message}</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

      <div className="mt-6 grid gap-6 xl:grid-cols-[1fr_1.2fr]">
        <section className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
          <h2 className="text-lg font-semibold text-slate-900">Search existing patient</h2>
          <form className="mt-4 grid gap-4" onSubmit={handleSearch}>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              MRN
              <input name="mrn" value={search.mrn} onChange={handleSearchChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="JAM-2026-24581" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Phone
              <input name="phone" value={search.phone} onChange={handleSearchChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="+2348012345678" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Name
              <input name="name" value={search.name} onChange={handleSearchChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Jane Doe" />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Date of birth
              <input type="date" name="dob" value={search.dob} onChange={handleSearchChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" />
            </label>
            <button type="submit" disabled={loading} className="rounded-xl btn-brand px-4 py-3 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-70">
              {loading ? 'Searching...' : 'Search patient'}
            </button>
          </form>

          {searchResult ? (
            <div className="mt-4 rounded-2xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-900">
              <div className="font-semibold">Patient found</div>
              <div className="mt-2">MRN: {searchResult.mrn || 'N/A'}</div>
              <div>Name: {searchResult.firstName} {searchResult.lastName}</div>
              <div>Phone: {searchResult.phone || 'N/A'}</div>
            </div>
          ) : null}
        </section>

        <section className="rounded-2xl border border-slate-200 bg-white p-4">
          <h2 className="text-lg font-semibold text-slate-900">Register new patient for arrival</h2>
          <form className="mt-4 grid gap-4" onSubmit={handleArrival}>
            <div className="grid gap-4 md:grid-cols-2">
              <label className="grid gap-2 text-sm font-medium text-slate-700">
                First name
                <input name="firstName" required value={patientForm.firstName} onChange={handlePatientChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Jane" />
              </label>
              <label className="grid gap-2 text-sm font-medium text-slate-700">
                Last name
                <input name="lastName" required value={patientForm.lastName} onChange={handlePatientChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Doe" />
              </label>
              <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
                Email
                <input type="email" name="email" required value={patientForm.email} onChange={handlePatientChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="jane@example.com" />
              </label>
              <label className="grid gap-2 text-sm font-medium text-slate-700">
                Phone
                <input name="phone" required value={patientForm.phone} onChange={handlePatientChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="+2348012345678" />
              </label>
              <label className="grid gap-2 text-sm font-medium text-slate-700">
                Gender
                <select name="gender" value={patientForm.gender} onChange={handlePatientChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                </select>
              </label>
              <label className="grid gap-2 text-sm font-medium text-slate-700">
                Date of birth
                <input type="date" name="dateOfBirth" required value={patientForm.dateOfBirth} onChange={handlePatientChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" />
              </label>
              <label className="grid gap-2 text-sm font-medium text-slate-700">
                Visit type
                <select name="visitType" value={patientForm.visitType} onChange={handlePatientChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500">
                  <option value="OPD">OPD</option>
                  <option value="EMERGENCY">Emergency</option>
                  <option value="ADMISSION">Admission</option>
                  <option value="FOLLOW_UP">Follow up</option>
                </select>
              </label>
              <label className="grid gap-2 text-sm font-medium text-slate-700">
                Department
                <input name="departmentName" value={patientForm.departmentName} onChange={handlePatientChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Reception" />
              </label>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
              <h3 className="text-base font-semibold text-slate-900">Address</h3>
              <div className="mt-4 grid gap-4 md:grid-cols-2">
                <label className="grid gap-2 text-sm font-medium text-slate-700 md:col-span-2">
                  Street
                  <input name="street" value={patientForm.address.street} onChange={handleAddressChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="12 Oba Akin" />
                </label>
                <label className="grid gap-2 text-sm font-medium text-slate-700">
                  City
                  <input name="city" value={patientForm.address.city} onChange={handleAddressChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Lagos" />
                </label>
                <label className="grid gap-2 text-sm font-medium text-slate-700">
                  State
                  <input name="state" value={patientForm.address.state} onChange={handleAddressChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Lagos State" />
                </label>
                <label className="grid gap-2 text-sm font-medium text-slate-700">
                  ZIP code
                  <input name="zipCode" value={patientForm.address.zipCode} onChange={handleAddressChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="100001" />
                </label>
                <label className="grid gap-2 text-sm font-medium text-slate-700">
                  Country
                  <input name="country" value={patientForm.address.country} onChange={handleAddressChange} className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Nigeria" />
                </label>
              </div>
            </div>

            <label className="grid gap-2 text-sm font-medium text-slate-700">
              Triage notes
              <textarea name="triageNotes" value={patientForm.triageNotes} onChange={handlePatientChange} className="min-h-24 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500" placeholder="Patient arrived with fever and chest pain" />
            </label>

            <button type="submit" disabled={loading} className="rounded-xl bg-emerald-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-70">
              {loading ? 'Checking in...' : 'Check in patient'}
            </button>
          </form>
        </section>
      </div>
    </div>
  )
}

export default PatientArrivalPage
