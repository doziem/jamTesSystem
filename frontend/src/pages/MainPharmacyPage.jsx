import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { API_BASE, normalizeList, readJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

const cardClass = 'rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:border-blue-400 hover:shadow-md'

function MainPharmacyPage() {
  const showError = useErrorRedirect()
  const [mainPharmacy, setMainPharmacy] = useState(null)
  const [inventory, setInventory] = useState([])
  const [mainStock, setMainStock] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      try {
        const [pharmacyPayload, medicationPayload] = await Promise.all([
          readJson(`${API_BASE}/api/pharmacies/all?page=0&size=100&sortBy=name&sortDirection=asc`),
          readJson(`${API_BASE}/api/medications`),
        ])
        const main = normalizeList(pharmacyPayload).find((pharmacy) => pharmacy.mainPharmacy) || null
        setMainPharmacy(main)
        setInventory(normalizeList(medicationPayload))
        if (main) {
          setMainStock(normalizeList(await readJson(`${API_BASE}/api/pharmacies/${main.id}/medications`)))
        }
      } catch (err) {
        showError(err.message || 'Unable to load main pharmacy.')
      } finally {
        setLoading(false)
      }
    }
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  return (
    <div className="grid gap-6">
      <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
        <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Main pharmacy</div>
        <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">{mainPharmacy?.name || 'Main pharmacy'}</h1>
        <p className="mt-2 text-sm text-slate-600">
          The main pharmacy is stocked from the central inventory and supplies every branch pharmacy on request.
        </p>
        {!loading && !mainPharmacy ? (
          <div className="mt-4 rounded-2xl bg-amber-50 px-4 py-3 text-sm text-amber-800">
            No main pharmacy exists yet. <Link to="/pharmacies/new" className="font-semibold underline">Create one</Link> first.
          </div>
        ) : null}
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <Link to="/main-pharmacy/medications/new" className={cardClass}>
          <div className="text-lg font-semibold text-slate-900">Create medication (inventory)</div>
          <p className="mt-1 text-sm text-slate-600">Medications are created in the central inventory, not directly in the main pharmacy.</p>
        </Link>
        <Link to="/main-pharmacy/medications" className={cardClass}>
          <div className="text-lg font-semibold text-slate-900">Update inventory medication</div>
          <p className="mt-1 text-sm text-slate-600">Edit price, category, status and inventory quantity.</p>
        </Link>
        <Link to="/main-pharmacy/supply" className={cardClass}>
          <div className="text-lg font-semibold text-slate-900">Supply branches</div>
          <p className="mt-1 text-sm text-slate-600">Move inventory stock to the main pharmacy, then supply branches on request.</p>
        </Link>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <StockTable title="Central inventory" rows={inventory.map((m) => ({ id: m.id, name: m.name, quantity: m.quantity }))} loading={loading} />
        <StockTable title="Main pharmacy stock" rows={mainStock.map((m) => ({ id: m.medicationId, name: m.medicationName, quantity: m.quantityInStock }))} loading={loading} />
      </div>
    </div>
  )
}

function StockTable({ title, rows, loading }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
      <h2 className="text-lg font-semibold text-slate-900">{title}</h2>
      <table className="mt-3 w-full text-left text-sm">
        <thead className="text-slate-500">
          <tr><th className="py-2">Medication</th><th className="py-2 text-right">Quantity</th></tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={row.id} className="border-t border-slate-100">
              <td className="py-2 text-slate-800">{row.name}</td>
              <td className="py-2 text-right font-medium text-slate-900">{row.quantity}</td>
            </tr>
          ))}
          {!loading && rows.length === 0 ? <tr><td colSpan={2} className="py-4 text-slate-500">Nothing to show yet.</td></tr> : null}
        </tbody>
      </table>
    </div>
  )
}

export default MainPharmacyPage
