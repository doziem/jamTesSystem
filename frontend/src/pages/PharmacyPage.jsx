import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { API_BASE, normalizeList, readJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

function PharmacyPage() {
  const [pharmacies, setPharmacies] = useState([])
  const [medications, setMedications] = useState([])
  const [searchTerm, setSearchTerm] = useState('')
  const [lowStockOnly, setLowStockOnly] = useState(false)
  const [reorderStatus, setReorderStatus] = useState('')
  const [selectedPharmacyId, setSelectedPharmacyId] = useState('')
  const [loading, setLoading] = useState(true)
  const [inventoryLoading, setInventoryLoading] = useState(false)
  const [actionLoadingId, setActionLoadingId] = useState('')
  const [error, setError] = useState('')
  const [inventoryError, setInventoryError] = useState('')
  const showError = useErrorRedirect()

  useEffect(() => {
    const loadPharmacies = async () => {
      setLoading(true)
      setError('')

      try {
        const payload = await readJson(`${API_BASE}/api/pharmacies/all?page=0&size=20&sortBy=name&sortDirection=asc`)
        const nextPharmacies = normalizeList(payload)
        setPharmacies(nextPharmacies)

        if (nextPharmacies.length > 0) {
          const firstPharmacyId = nextPharmacies[0]?.id || nextPharmacies[0]?.pharmacyId || ''
          setSelectedPharmacyId(firstPharmacyId)
        }
      } catch (err) {
        const message = err.message || 'Unable to load pharmacy records.'
        setError(message)
        showError(message)
      } finally {
        setLoading(false)
      }
    }

    loadPharmacies()
  }, [])

  useEffect(() => {
    if (!selectedPharmacyId) {
      setMedications([])
      return
    }

    const loadMedicationInventory = async () => {
      setInventoryLoading(true)
      setInventoryError('')

      try {
        const payload = await readJson(`${API_BASE}/api/pharmacies/${selectedPharmacyId}/medications`)
        const inventory = normalizeList(payload)
        setMedications(Array.isArray(inventory) ? inventory : [])
      } catch (err) {
        const message = err.message || 'Unable to load medication inventory.'
        setInventoryError(message)
        showError(message)
        setMedications([])
      } finally {
        setInventoryLoading(false)
      }
    }

    loadMedicationInventory()
  }, [selectedPharmacyId])

  const selectedPharmacy = pharmacies.find((pharmacy) => {
    const pharmacyId = pharmacy.id || pharmacy.pharmacyId
    return pharmacyId === selectedPharmacyId
  })

  const filteredMedications = medications.filter((item) => {
    const medicationName = (item.medicationName || '').toLowerCase()
    const matchesSearch = medicationName.includes(searchTerm.trim().toLowerCase())
    const isLowStock = (item.quantityInStock ?? 0) <= (item.reorderLevel ?? 0)

    if (!matchesSearch) {
      return false
    }

    if (lowStockOnly && !isLowStock) {
      return false
    }

    return true
  })

  const getWarningClass = (warningLevel = '') => {
    if (warningLevel.includes('LEVEL_1')) return 'status error'
    if (warningLevel.includes('LEVEL_5')) return 'status info'
    if (warningLevel.includes('LEVEL_10')) return 'status info'
    return 'status info'
  }

  const handleReorderRequest = async (item) => {
    const pharmacyId = selectedPharmacyId
    const medicationId = item.medicationId

    if (!pharmacyId || !medicationId) {
      return
    }

    const quantityInStock = item.quantityInStock ?? 0
    const reorderLevel = item.reorderLevel ?? 0
    const requestQuantity = Math.max(reorderLevel + 5 - quantityInStock, 1)

    setActionLoadingId(medicationId)
    setInventoryError('')
    setReorderStatus('')

    try {
      const response = await fetch(
        `${API_BASE}/api/pharmacies/${pharmacyId}/medications/${medicationId}/stock?quantity=${requestQuantity}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
        },
      )

      const data = await response.json().catch(() => ({}))

      if (!response.ok) {
        throw new Error(data.message || 'Unable to create reorder request.')
      }

      setReorderStatus(`Reorder request sent for ${item.medicationName || 'medication'} (${requestQuantity} units).`)

      const refreshed = await readJson(`${API_BASE}/api/pharmacies/${pharmacyId}/medications`)
      setMedications(normalizeList(refreshed))
    } catch (err) {
      const message = err.message || 'Unable to create reorder request.'
      setInventoryError(message)
      showError(message)
    } finally {
      setActionLoadingId('')
    }
  }

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Pharmacy operations</div>
          <h1 className="mt-1 text-2xl font-bold text-slate-900 sm:text-3xl">Pharmacies</h1>
        </div>
        <Link to="/pharmacies/new" className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-700">
          Create pharmacy
        </Link>
      </div>

      {loading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading pharmacies...</div> : null}
      {error ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div> : null}

      {!loading && !error ? (
        <>
          <div className="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {pharmacies.length > 0 ? pharmacies.map((pharmacy, index) => {
              const pharmacyId = pharmacy.id || pharmacy.pharmacyId
              const name = pharmacy.name || `Pharmacy ${index + 1}`
              const branch = pharmacy.department || pharmacy.pharmacyType || 'Department pharmacy'
              const location = pharmacy.location || pharmacy.address || 'Location not set'
              const isSelected = selectedPharmacyId === pharmacyId

              return (
                <button
                  key={pharmacyId || `${name}-${index}`}
                  type="button"
                  className={`rounded-2xl border p-4 text-left transition ${isSelected ? 'border-blue-500 bg-blue-50' : 'border-slate-200 bg-slate-50 hover:border-blue-300'}`}
                  onClick={() => setSelectedPharmacyId(pharmacyId)}
                >
                  <strong className="block text-base font-semibold text-slate-900">{name}</strong>
                  <span className="mt-1 block text-sm text-slate-600">{branch}</span>
                  <small className="mt-3 block text-sm text-slate-500">{location}</small>
                </button>
              )
            }) : <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No pharmacy records available.</div>}
          </div>

          {selectedPharmacy ? (
            <div className="mt-6 rounded-3xl border border-slate-200 bg-slate-50 p-4 sm:p-5">
              <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-end sm:justify-between">
                <div>
                  <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">Inventory</div>
                  <h2 className="mt-1 text-xl font-bold text-slate-900 sm:text-2xl">{selectedPharmacy.name || 'Selected pharmacy'}</h2>
                </div>
                <small className="text-sm text-slate-600">{selectedPharmacy.department || 'Department pharmacy'}</small>
              </div>

              <div className="mt-4 grid gap-4 lg:grid-cols-[1fr_auto] lg:items-end">
                <label className="grid gap-2 text-sm font-medium text-slate-700">
                  Search medication
                  <input
                    className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-blue-500"
                    type="text"
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                    placeholder="Type a medication name"
                  />
                </label>

                <label className="flex items-center gap-3 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-medium text-slate-700">
                  <input
                    type="checkbox"
                    checked={lowStockOnly}
                    onChange={(event) => setLowStockOnly(event.target.checked)}
                  />
                  Low stock only
                </label>
              </div>

              {inventoryLoading ? <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading medication inventory...</div> : null}
              {inventoryError ? <div className="mt-4 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-800">{inventoryError}</div> : null}
              {reorderStatus ? <div className="mt-4 rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-900">{reorderStatus}</div> : null}

              {!inventoryLoading && !inventoryError ? (
                <div className="mt-5 overflow-x-auto rounded-2xl border border-slate-200 bg-white">
                  <table className="min-w-full text-left text-sm">
                    <thead className="bg-slate-50 text-slate-600">
                      <tr>
                        <th className="px-4 py-3 font-semibold">Medication</th>
                        <th className="px-4 py-3 font-semibold">Available</th>
                        <th className="px-4 py-3 font-semibold">Reorder</th>
                        <th className="px-4 py-3 font-semibold">Status</th>
                        <th className="px-4 py-3 font-semibold">Warning</th>
                        <th className="px-4 py-3 font-semibold">Action</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200">
                      {filteredMedications.length > 0 ? filteredMedications.map((item) => {
                        const isLowStock = (item.quantityInStock ?? 0) <= (item.reorderLevel ?? 0)
                        const isLoading = actionLoadingId === (item.medicationId || item.id)

                        return (
                          <tr key={item.medicationId || item.id || item.medicationName} className={isLowStock ? 'bg-amber-50/60' : ''}>
                            <td className="px-4 py-3 font-medium text-slate-900">{item.medicationName || 'Unknown medication'}</td>
                            <td className="px-4 py-3">{item.quantityInStock ?? 0}</td>
                            <td className="px-4 py-3">{item.reorderLevel ?? 0}</td>
                            <td className="px-4 py-3">
                              <span className={getWarningClass(item.warningLevel)}>{item.stockLevel || 'AVAILABLE'}</span>
                            </td>
                            <td className="px-4 py-3">{item.warningMessage || 'Sufficient stock available.'}</td>
                            <td className="px-4 py-3">
                              <button
                                type="button"
                                className="rounded-xl bg-blue-600 px-3 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
                                onClick={() => handleReorderRequest(item)}
                                disabled={isLoading || !isLowStock}
                              >
                                {isLoading ? 'Requesting...' : 'Request reorder'}
                              </button>
                            </td>
                          </tr>
                        )
                      }) : (
                        <tr>
                          <td colSpan="6" className="px-4 py-6 text-center text-slate-600">No medication records available for this pharmacy.</td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              ) : null}
            </div>
          ) : null}
        </>
      ) : null}
    </div>
  )
}

export default PharmacyPage
