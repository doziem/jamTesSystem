import { useState } from 'react'
import { API_BASE, writeJson } from '../lib/api'

const STEPS = [
  { title: 'Personal details' },
  { title: 'Role & employment' },
  { title: 'Emergency contact' },
  { title: 'Qualifications' },
]

const DEPARTMENT_OPTIONS = [
  ['', 'Select a department'],
  ...[
    'Accident and Emergency',
    'Administration',
    'Anesthesiology',
    'Billing and Finance',
    'Cardiology',
    'Dermatology',
    'General Medicine',
    'Human Resources',
    'Intensive Care',
    'Laboratory',
    'Main Pharmacy',
    'Medical Records',
    'Neurology',
    'Nursing',
    'Obstetrics and Gynecology',
    'Oncology',
    'Operating Theatre',
    'Orthopedics',
    'Outpatient',
    'Pediatrics',
    'Pharmacy',
    'Physiotherapy',
    'Radiology',
    'Reception',
    'Surgery',
  ].map((department) => [department, department]),
]

const ROLE_OPTIONS = [
  ['STAFF', 'Other staff'],
  ['NURSE', 'Nurse'],
  ['DOCTOR', 'Doctor'],
  ['PHARMACIST', 'Pharmacist'],
  ['LAB_SCIENTIST', 'Lab scientist'],
  ['RADIOLOGIST', 'Radiologist'],
  ['RECEPTIONIST', 'Receptionist'],
  ['ACCOUNTANT', 'Accountant'],
  ['SUPERVISOR', 'Supervisor'],
  ['HR', 'Human resources'],
]
const CLINICAL_ROLES = ['NURSE', 'DOCTOR', 'PHARMACIST', 'LAB_SCIENTIST', 'RADIOLOGIST']

const INPUT_CLASS = 'w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100'

function getNameParts(name = '') {
  const [firstName = '', ...remaining] = name.trim().split(/\s+/).filter(Boolean)
  return { firstName, lastName: remaining.pop() || '', middleName: remaining.join(' ') }
}

function toDateTime(value) {
  return value ? `${value}T00:00:00Z` : null
}

function Field({ label, name, value, onChange, type = 'text', required = false, children, className = '', ...props }) {
  return (
    <label className={`grid gap-2 text-sm font-medium text-slate-700 ${className}`}>
      {label}
      {children || (
        <input
          className={INPUT_CLASS}
          name={name}
          type={type}
          value={value}
          onChange={onChange}
          required={required}
          {...props}
        />
      )}
    </label>
  )
}

function SelectField({ label, name, value, onChange, required = false, options }) {
  return (
    <Field label={label} name={name} value={value} onChange={onChange} required={required}>
      <select className={INPUT_CLASS} name={name} value={value} onChange={onChange} required={required}>
        {options.map(([optionValue, optionLabel]) => (
          <option key={optionValue} value={optionValue}>{optionLabel}</option>
        ))}
      </select>
    </Field>
  )
}

function StaffOnboardingPage({ user, onComplete, onLogout }) {
  const nameParts = getNameParts(user?.name)
  const [step, setStep] = useState(0)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [form, setForm] = useState({
    ...nameParts,
    gender: 'MALE',
    dateOfBirth: '',
    maritalStatus: '',
    nationality: '',
    stateOfOrigin: '',
    address: '',
    nationalId: '',
    department: '',
    jobTitle: '',
    role: 'STAFF',
    staffType: 'NON_CLINICAL',
    employmentType: 'FULL_TIME',
    dateOfEmployment: '',
    probationEndDate: '',
    nextOfKinName: '',
    nextOfKinRelationship: '',
    nextOfKinPhone: '',
    nextOfKinAddress: '',
    licenseNumber: '',
    licenseBody: '',
    licenseIssueDate: '',
    licenseExpiryDate: '',
    specialty: '',
    highestQualification: '',
  })

  const updateForm = (event) => {
    const { name, value } = event.target
    setForm((current) => ({
      ...current,
      [name]: value,
      ...(name === 'role' ? { staffType: CLINICAL_ROLES.includes(value) ? 'CLINICAL' : 'NON_CLINICAL' } : {}),
    }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (step < STEPS.length - 1) {
      setStep((current) => current + 1)
      setError('')
      return
    }

    setSaving(true)
    setError('')
    try {
      const payload = {
        ...form,
        email: user?.email || '',
        phone: user?.phone
          ? `${user.phone.trim().startsWith('+') ? '+' : ''}${user.phone.replace(/\D/g, '')}`
          : '',
        dateOfBirth: toDateTime(form.dateOfBirth),
        dateOfEmployment: toDateTime(form.dateOfEmployment),
        probationEndDate: toDateTime(form.probationEndDate),
        licenseIssueDate: toDateTime(form.licenseIssueDate),
        licenseExpiryDate: toDateTime(form.licenseExpiryDate),
      }
      const staff = await writeJson(`${API_BASE}/api/staff/onboard`, {
        method: 'POST',
        body: JSON.stringify(payload),
      })
      onComplete(staff)
    } catch (submitError) {
      setError(submitError.message || 'Unable to save your staff information.')
    } finally {
      setSaving(false)
    }
  }

  const now = new Date()
  const today = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 10)

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <header className="border-b border-slate-200 bg-white">
        <nav className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8" aria-label="Onboarding navigation">
          <div>
            <div className="text-lg font-bold tracking-tight sm:text-xl">JamTes System</div>
            <div className="text-xs uppercase tracking-[0.18em] text-slate-500">Hospital Portal</div>
          </div>
          <div className="flex min-w-0 items-center gap-3 sm:gap-4">
            <span className="hidden max-w-48 truncate text-sm text-slate-600 sm:block">{user?.name || 'Hospital staff'}</span>
            <button type="button" onClick={onLogout} disabled={saving} className="shrink-0 rounded-xl border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-70">
              Log out
            </button>
          </div>
        </nav>
      </header>
      <main className="px-4 py-8 sm:px-6 lg:px-8">
      <section className="mx-auto w-full max-w-4xl rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-8">
        <div className="border-b border-slate-200 pb-6">
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-600">Staff onboarding</div>
          <h1 className="mt-2 text-2xl font-bold sm:text-3xl">Complete your staff profile</h1>
          <p className="mt-2 text-sm text-slate-600">
            A few steps to set up your JamTes System account. Your login email and phone are linked automatically.
          </p>
        </div>

        <ol className="flex gap-2 py-6 sm:gap-3" aria-label="Onboarding progress">
          {STEPS.map((item, index) => (
            <li
              key={item.title}
              aria-current={index === step ? 'step' : undefined}
              className="min-w-0 flex-1"
            >
              <span aria-hidden="true" className={`block h-1.5 rounded-full ${index <= step ? 'bg-blue-600' : 'bg-slate-200'}`} />
              <span className={`mt-2 block text-xs font-semibold sm:text-sm ${index === step ? 'text-blue-700' : 'text-slate-500'}`}>
                <span className="sm:hidden">Step {index + 1}</span>
                <span className="hidden sm:inline">{index + 1}. {item.title}</span>
                <span className="sr-only sm:hidden">: {item.title}</span>
              </span>
            </li>
          ))}
        </ol>

        <form onSubmit={handleSubmit}>
          <h2 className="text-xl font-bold">{STEPS[step].title}</h2>
          {step === 0 ? (
            <>
              <p className="mt-1 text-sm text-slate-600">Account: {user?.email || 'Email unavailable'}{user?.phone ? ` · ${user.phone}` : ''}</p>
              <div className="mt-5 grid gap-4 sm:grid-cols-2">
                <Field label="First name" name="firstName" value={form.firstName} onChange={updateForm} required autoComplete="given-name" />
                <Field label="Middle name" name="middleName" value={form.middleName} onChange={updateForm} autoComplete="additional-name" />
                <Field label="Last name" name="lastName" value={form.lastName} onChange={updateForm} required autoComplete="family-name" />
                <SelectField label="Gender" name="gender" value={form.gender} onChange={updateForm} required options={[['MALE', 'Male'], ['FEMALE', 'Female'], ['OTHER', 'Other']]} />
                <Field label="Date of birth" name="dateOfBirth" type="date" value={form.dateOfBirth} onChange={updateForm} required max={today} />
                <Field label="Marital status" name="maritalStatus" value={form.maritalStatus} onChange={updateForm} />
                <Field label="Nationality" name="nationality" value={form.nationality} onChange={updateForm} />
                <Field label="State of origin" name="stateOfOrigin" value={form.stateOfOrigin} onChange={updateForm} />
                <Field label="Address" name="address" value={form.address} onChange={updateForm} className="sm:col-span-2" />
                <Field label="National ID (optional)" name="nationalId" value={form.nationalId} onChange={updateForm} />
              </div>
            </>
          ) : null}

          {step === 1 ? (
            <div className="mt-5 grid gap-4 sm:grid-cols-2">
              <SelectField label="Department" name="department" value={form.department} onChange={updateForm} required options={DEPARTMENT_OPTIONS} />
              <Field label="Job title" name="jobTitle" value={form.jobTitle} onChange={updateForm} required placeholder="e.g. Registered nurse" />
              <SelectField label="Staff role" name="role" value={form.role} onChange={updateForm} required options={ROLE_OPTIONS} />
              <SelectField label="Staff type" name="staffType" value={form.staffType} onChange={updateForm} required options={[['CLINICAL', 'Clinical'], ['NON_CLINICAL', 'Non-clinical'], ['SUPPORT', 'Support']]} />
              <SelectField label="Employment type" name="employmentType" value={form.employmentType} onChange={updateForm} required options={[['FULL_TIME', 'Full-time'], ['PART_TIME', 'Part-time'], ['LOCUM', 'Locum'], ['CONTRACT', 'Contract']]} />
              <Field label="Date of employment" name="dateOfEmployment" type="date" value={form.dateOfEmployment} onChange={updateForm} required max={today} />
              <Field label="Probation end date (optional)" name="probationEndDate" type="date" value={form.probationEndDate} onChange={updateForm} />
            </div>
          ) : null}

          {step === 2 ? (
            <div className="mt-5 grid gap-4 sm:grid-cols-2">
              <Field label="Contact name" name="nextOfKinName" value={form.nextOfKinName} onChange={updateForm} />
              <Field label="Relationship" name="nextOfKinRelationship" value={form.nextOfKinRelationship} onChange={updateForm} />
              <Field label="Contact phone" name="nextOfKinPhone" type="tel" value={form.nextOfKinPhone} onChange={updateForm} />
              <Field label="Contact address" name="nextOfKinAddress" value={form.nextOfKinAddress} onChange={updateForm} />
            </div>
          ) : null}

          {step === 3 ? (
            <>
              <div className="mt-5 grid gap-4 sm:grid-cols-2">
                <Field label={`License number${form.staffType === 'CLINICAL' ? '' : ' (optional)'}`} name="licenseNumber" value={form.licenseNumber} onChange={updateForm} required={form.staffType === 'CLINICAL'} />
                <Field label={`Licensing body${form.staffType === 'CLINICAL' ? '' : ' (optional)'}`} name="licenseBody" value={form.licenseBody} onChange={updateForm} required={form.staffType === 'CLINICAL'} />
                <Field label="License issue date" name="licenseIssueDate" type="date" value={form.licenseIssueDate} onChange={updateForm} max={today} />
                <Field label="License expiry date" name="licenseExpiryDate" type="date" value={form.licenseExpiryDate} onChange={updateForm} />
                <Field label="Specialty" name="specialty" value={form.specialty} onChange={updateForm} />
                <Field label={`Highest qualification${form.staffType === 'CLINICAL' ? '' : ' (optional)'}`} name="highestQualification" value={form.highestQualification} onChange={updateForm} required={form.staffType === 'CLINICAL'} />
              </div>
              <div className="mt-5 rounded-2xl bg-slate-50 p-4 text-sm text-slate-700">
                <h3 className="font-semibold text-slate-900">Account details</h3>
                <p className="mt-2">{user?.name || `${form.firstName} ${form.lastName}`}</p>
                <p>{user?.email}</p>
                <p>{user?.phone}</p>
                <p className="mt-2">{form.jobTitle || 'Job title'} · {form.department || 'Department'} · {form.role.replaceAll('_', ' ')}</p>
              </div>
            </>
          ) : null}

          {error ? <div className="mt-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">{error}</div> : null}

          <div className="mt-7 flex flex-wrap items-center justify-between gap-3 border-t border-slate-200 pt-5">
            <button
              type="button"
              onClick={() => setStep((current) => Math.max(0, current - 1))}
              disabled={step === 0 || saving}
              className="rounded-xl border border-slate-300 px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:invisible"
            >
              Back
            </button>
            <span className="text-sm text-slate-500">Step {step + 1} of {STEPS.length}</span>
            <button type="submit" disabled={saving} className="rounded-xl btn-brand px-5 py-2.5 text-sm font-semibold disabled:cursor-not-allowed disabled:opacity-70">
              {saving ? 'Saving profile...' : step === STEPS.length - 1 ? 'Complete onboarding' : 'Continue'}
            </button>
          </div>
        </form>
      </section>
      </main>
    </div>
  )
}

export default StaffOnboardingPage
