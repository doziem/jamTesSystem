import { useEffect, useState } from 'react'
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
    <div className="page-panel">
      <div className="page-head">
        <div>
          <div className="eyebrow">Billing operations</div>
          <h1>Billing</h1>
        </div>
      </div>

      {loading ? <div className="status info">Loading billing entries...</div> : null}
      {error ? <div className="status error">{error}</div> : null}

      {!loading && !error ? (
        <div className="module-list">
          {billings.length > 0 ? billings.map((bill, index) => {
            const patientId = bill.patientId || bill.patient?.id || 'N/A'
            const amount = bill.amount ?? bill.total ?? 0
            const status = bill.status || bill.paymentStatus || 'Pending'

            return (
              <div key={bill.id || `${patientId}-${index}`} className="module-card">
                <div className="module-main">
                  <strong>Patient #{patientId}</strong>
                  <span>{status}</span>
                </div>
                <small>${Number(amount).toFixed(2)}</small>
              </div>
            )
          }) : <div className="status info">No billing records available.</div>}
        </div>
      ) : null}
    </div>
  )
}

export default BillingPage
