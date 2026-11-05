import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { API_BASE, normalizeList, readJson, writeJson } from '../lib/api'

const inputClass = 'rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500'

function SupplyBranchPage() {
  const [mainPharmacy, setMainPharmacy] = useState(null)
  const [branches, setBranches] = useState([])
  const [medications, setMedications] = useState([])
  const [mainStock, setMainStock] = useState([])
  const [restock, setRestock] = useState({ medicationId: '', quantity: '' })
  const [supply, setSupply] = useState({ branchId: '', medicationId: '', quantity: '' })
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const loadStock = async (mainId) => {
    const [medicationPayload, stockPayload] = await Promise.all([
      readJson(`${API_BASE}/api/medications`),
      readJson(`${API_BASE}/api/pharmacies/${mainId}/medications`),
    ])
    setMedications(normalizeList(medicationPayload))
    setMainStock(normalizeList(stockPayload))
  }

  useEffect(() => {
    const load = async () => {
      try {
        const all = normalizeList(await readJson(`${API_BASE}/api/pharmacies/all?page=0&size=100&sortBy=name&sortDirection=asc`))
        const main = all.find((pharmacy) => pharmacy.mainPharmacy)
        if (!main) {
          setError('No main pharmacy exists yet.')
          return
        }
        setMainPharmacy(main)
        setBranches(all.filter((pharmacy) => !pharmacy.mainPharmacy && pharmacy.mainPharmacyId === main.id))
        await loadStock(main.id)
      } catch (err) {
        setError(err.message || 'Unable to load pharmacy data.')
      }
    }
    load()
  }, [])

  const run = async (url, successMessage, reset) => {
    setBusy(true)
    setError('')
    setSuccess('')
    try {
      await writeJson(url, { method: 'POST' })
      await loadStock(mainPharmacy.id)
      setSuccess(successMessage)
      reset()
    } catch (err) {
      setError(err.message || 'Request failed.')
    } finally {
      setBusy(false)
    }
  }

  const handleRestock = (event) => {
    event.preventDefault()
    run(
      `${API_BASE}/api/pharmacies/${mainPharmacy.id}/restock-from-inventory/${restock.medicationId}?quantity=${Number(restock.quantity)}`,
      'Main pharmacy restocked from inventory.',
      () => setRestock({ medicationId: '', quantity: '' }),
    )
  }

  const handleSupply = (event) => {
    event.preventDefault()
    run(
      `${API_BASE}/api/pharmacies/${supply.branchId}/transfer/${supply.medicationId}?quantity=${Number(supply.quantity)}`,
      'Medication supplied to branch.',
      () => setSupply({ branchId: '', medicationId: '', quantity: '' }),
    )
  }

  const inventoryOf = (id) => medications.find((item) => item.id === id)?.quantity ?? 0
  const mainStockOf = (id) => mainStock.find((item) => item.medicationId === id)?.quantityInStock ?? 0

  return (
    <div className="grid gap-6">
      <div className="flex flex-col gap-3 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:flex-row sm:items-end sm:justify-between sm:p-6">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Main pharmacy</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Supply branches</h1>
        </div>
        <Link to="/main-pharmacy" className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50">Back to main pharmacy</Link>
      </div>

      {error ? <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}
      {success ? <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{success}</div> : null}

      <form onSubmit={handleRestock} className="grid gap-4 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm md:grid-cols-3">
        <h2 className="text-lg font-semibold text-slate-900 md:col-span-3">1. Restock main pharmacy from inventory</h2>
        <select required value={restock.medicationId} onChange={(e) => setRestock((p) => ({ ...p, medicationId: e.target.value }))} className={inputClass}>
          <option value="">Select medication</option>
          {medications.map((m) => <option key={m.id} value={m.id}>{m.name} (inventory: {m.quantity})</option>)}
        </select>
        <input required type="number" min="1" max={restock.medicationId ? inventoryOf(restock.medicationId) : undefined} placeholder="Quantity" value={restock.quantity} onChange={(e) => setRestock((p) => ({ ...p, quantity: e.target.value }))} className={inputClass} />
        <button type="submit" disabled={busy || !mainPharmacy} className="rounded-xl btn-brand px-5 py-2.5 text-sm font-semibold disabled:opacity-70">Restock main pharmacy</button>
      </form>

      <form onSubmit={handleSupply} className="grid gap-4 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm md:grid-cols-4">
        <h2 className="text-lg font-semibold text-slate-900 md:col-span-4">2. Supply a branch on request</h2>
        <select required value={supply.branchId} onChange={(e) => setSupply((p) => ({ ...p, branchId: e.target.value }))} className={inputClass}>
          <option value="">Select branch</option>
          {branches.map((b) => <option key={b.id} value={b.id}>{b.name} ({b.department})</option>)}
        </select>
        <select required value={supply.medicationId} onChange={(e) => setSupply((p) => ({ ...p, medicationId: e.target.value }))} className={inputClass}>
          <option value="">Select medication</option>
          {mainStock.map((s) => <option key={s.medicationId} value={s.medicationId}>{s.medicationName} (main stock: {s.quantityInStock})</option>)}
        </select>
        <input required type="number" min="1" max={supply.medicationId ? mainStockOf(supply.medicationId) : undefined} placeholder="Quantity" value={supply.quantity} onChange={(e) => setSupply((p) => ({ ...p, quantity: e.target.value }))} className={inputClass} />
        <button type="submit" disabled={busy || !mainPharmacy} className="rounded-xl btn-brand px-5 py-2.5 text-sm font-semibold disabled:opacity-70">Supply branch</button>
        {mainPharmacy && branches.length === 0 ? <p className="text-sm text-slate-500 md:col-span-4">No branch pharmacies are linked to the main pharmacy yet.</p> : null}
      </form>
    </div>
  )
}

export default SupplyBranchPage
