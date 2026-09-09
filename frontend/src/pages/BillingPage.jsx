import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { API_BASE, normalizeList, readJson } from '../lib/api'

function BillingPage() {
  const [billings, setBillings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadBillings = async () => {
      setLoading(true)
      setError('')

      try {
        const payload = await readJson(`${API_BASE}/api/billing/patient/all?page=0&size=20`)
        setBillings(normalizeList(payload))
      } catch (err) {
        setError(err.message || 'Unable to load billing records.')
      } finally {
        setLoading(false)
      }
    }

    loadBillings()
  }, [])

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Billing operations</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Billing</h1>
        </div>
        <Link to="/billing/new" className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-700">
          Create billing
        </Link>
      </div>

      {loading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading billing entries...</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

      {!loading && !error ? (
        <div className="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {billings.length > 0 ? billings.map((bill, index) => {
            const patientId = bill.patientId || bill.patient?.id || 'N/A'
            const amount = bill.amount ?? bill.total ?? 0
            const status = bill.status || bill.paymentStatus || 'Pending'

            return (
              <div key={bill.id || `${patientId}-${index}`} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <strong className="block text-base font-semibold text-slate-900">Patient #{patientId}</strong>
                    <span className="mt-1 block text-sm text-slate-600">{status}</span>
                  </div>
                  <small className="text-sm font-semibold text-slate-900">${Number(amount).toFixed(2)}</small>
                </div>
              </div>
            )
          }) : <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No billing records available.</div>}
        </div>
      ) : null}
    </div>
  )
}

export default BillingPage
