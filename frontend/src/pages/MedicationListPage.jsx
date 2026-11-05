import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { API_BASE, readJson } from '../lib/api'

function MedicationListPage() {
  const [medications, setMedications] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    readJson(`${API_BASE}/api/medications`)
      .then((payload) => setMedications(Array.isArray(payload) ? payload : []))
      .catch((err) => setError(err.message || 'Unable to load medications.'))
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Main pharmacy</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Inventory medications</h1>
        </div>
        <div className="flex gap-2">
          <Link to="/main-pharmacy" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">Back</Link>
          <Link to="/main-pharmacy/medications/new" className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-700">New medication</Link>
        </div>
      </div>

      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

      <table className="mt-4 w-full text-left text-sm">
        <thead className="text-slate-500">
          <tr><th className="py-2">Name</th><th className="py-2">Category</th><th className="py-2 text-right">Price</th><th className="py-2 text-right">Inventory</th><th className="py-2">Status</th><th /></tr>
        </thead>
        <tbody>
          {medications.map((medication) => (
            <tr key={medication.id} className="border-t border-slate-100">
              <td className="py-2 font-medium text-slate-900">{medication.name}</td>
              <td className="py-2 text-slate-600">{medication.category || '-'}</td>
              <td className="py-2 text-right">{medication.unitPrice}</td>
              <td className="py-2 text-right">{medication.quantity}</td>
              <td className="py-2">{medication.active ? 'Active' : 'Inactive'}</td>
              <td className="py-2 text-right"><Link to={`/main-pharmacy/medications/${medication.id}/edit`} className="font-semibold text-blue-600 hover:underline">Update</Link></td>
            </tr>
          ))}
          {!loading && medications.length === 0 ? <tr><td colSpan={6} className="py-4 text-slate-500">No medications yet.</td></tr> : null}
        </tbody>
      </table>
    </div>
  )
}

export default MedicationListPage
