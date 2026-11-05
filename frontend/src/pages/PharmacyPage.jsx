import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { API_BASE, normalizeList, readJson, writeJson } from '../lib/api'
import { useErrorRedirect } from '../lib/useErrorRedirect'

function getStoredUserRole() {
  try {
    return JSON.parse(localStorage.getItem('jamtes-auth-user') || 'null')?.role || ''
  } catch {
    return ''
  }
}

function PharmacyPage() {
  const [pharmacies, setPharmacies] = useState([])
  const [medications, setMedications] = useState([])
  const [reorderRequests, setReorderRequests] = useState(null)
  const [requestQuantities, setRequestQuantities] = useState({})
  const [searchTerm, setSearchTerm] = useState('')
  const [lowStockOnly, setLowStockOnly] = useState(false)
  const [reorderStatus, setReorderStatus] = useState('')
  const [reorderError, setReorderError] = useState('')
  const [selectedPharmacyId, setSelectedPharmacyId] = useState('')
  const [loading, setLoading] = useState(true)
  const [inventoryLoading, setInventoryLoading] = useState(false)
  const [actionLoadingId, setActionLoadingId] = useState('')
  const [userRole] = useState(getStoredUserRole)
  const [error, setError] = useState('')
  const [inventoryError, setInventoryError] = useState('')
  const showError = useErrorRedirect()
  const canProcessReorderRequests = ['SUPERVISOR', 'ADMIN', 'PHARMACIST'].includes(userRole)
  const requestsLoading = reorderRequests === null

  const loadReorderRequests = async () => {
    const payload = await readJson(`${API_BASE}/api/pharmacies/reorder-requests`)
    setReorderRequests(normalizeList(payload))
    setReorderError('')
  }

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
    const fetchReorderRequests = async () => {
      try {
        await loadReorderRequests()
      } catch (err) {
        setReorderError(err.message || 'Unable to load reorder requests.')
      }
    }

    fetchReorderRequests()
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
    const requestQuantity = Number(requestQuantities[medicationId] ?? 1)

    if (!pharmacyId || !medicationId || !Number.isInteger(requestQuantity) || requestQuantity <= 0) {
      return
    }

    setActionLoadingId(medicationId)
    setInventoryError('')
    setReorderStatus('')
    setReorderError('')

    try {
      await writeJson(
        `${API_BASE}/api/pharmacies/${pharmacyId}/medications/${medicationId}/reorder-requests`,
        {
          method: 'POST',
          body: JSON.stringify({ quantity: requestQuantity }),
        },
      )

      setReorderStatus(`Reorder request submitted for ${item.medicationName || 'medication'} (${requestQuantity} units).`)
      await loadReorderRequests()
    } catch (err) {
      const message = err.message || 'Unable to submit reorder request.'
      setReorderError(message)
      showError(message)
    } finally {
      setActionLoadingId('')
    }
  }

  const handleProcessReorderRequest = async (request) => {
    setActionLoadingId(`request-${request.id}`)
    setReorderError('')
    setReorderStatus('')

    try {
      await writeJson(`${API_BASE}/api/pharmacies/reorder-requests/${request.id}/process`, {
        method: 'POST',
      })
      setReorderStatus(`Fulfilled ${request.quantity} units of ${request.medicationName} for ${request.pharmacyName}.`)
      await loadReorderRequests()
      if (request.pharmacyId === selectedPharmacyId) {
        const refreshed = await readJson(`${API_BASE}/api/pharmacies/${selectedPharmacyId}/medications`)
        setMedications(normalizeList(refreshed))
      }
    } catch (err) {
      const message = err.message || 'Unable to process reorder request.'
      setReorderError(message)
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
                              <div className="flex min-w-48 items-center gap-2">
                                <input
                                  aria-label={`Quantity to request for ${item.medicationName || 'medication'}`}
                                  className="w-20 rounded-lg border border-slate-300 px-2 py-2"
                                  type="number"
                                  min="1"
                                  step="1"
                                  required
                                  value={requestQuantities[item.medicationId] ?? '1'}
                                  onChange={(event) => setRequestQuantities((current) => ({
                                    ...current,
                                    [item.medicationId]: event.target.value,
                                  }))}
                                />
                              <button
                                type="button"
                                className="rounded-xl bg-blue-600 px-3 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
                                onClick={() => handleReorderRequest(item)}
                                disabled={isLoading || !isLowStock}
                              >
                                {isLoading ? 'Requesting...' : 'Request reorder'}
                              </button>
                              </div>
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

          <section className="mt-6 rounded-3xl border border-slate-200 bg-white p-4 sm:p-5">
            <div className="flex items-center justify-between gap-3 border-b border-slate-200 pb-4">
              <div>
                <div className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-500">
                  {canProcessReorderRequests ? 'Reorder processing queue' : 'My reorder requests'}
                </div>
                <h2 className="mt-1 text-xl font-bold text-slate-900">
                  {canProcessReorderRequests ? 'Pharmacy reorder requests' : 'Submitted requests'}
                </h2>
              </div>
            </div>

            {reorderStatus ? <div className="mt-4 rounded-xl bg-emerald-50 px-4 py-3 text-sm text-emerald-900">{reorderStatus}</div> : null}
            {reorderError ? <div className="mt-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-800">{reorderError}</div> : null}
            {requestsLoading ? <div className="mt-4 text-sm text-slate-600">Loading reorder requests...</div> : null}

            {!requestsLoading && !reorderError ? (
              <div className="mt-4 overflow-x-auto rounded-2xl border border-slate-200">
                <table className="min-w-full text-left text-sm">
                  <thead className="bg-slate-50 text-slate-600">
                    <tr>
                      <th className="px-4 py-3 font-semibold">Pharmacy</th>
                      <th className="px-4 py-3 font-semibold">Medication</th>
                      <th className="px-4 py-3 font-semibold">Quantity</th>
                      <th className="px-4 py-3 font-semibold">Requested by</th>
                      <th className="px-4 py-3 font-semibold">Status</th>
                      {canProcessReorderRequests ? <th className="px-4 py-3 font-semibold">Action</th> : null}
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-200">
                    {reorderRequests.length ? reorderRequests.map((request) => {
                      const isProcessing = actionLoadingId === `request-${request.id}`
                      return (
                        <tr key={request.id}>
                          <td className="px-4 py-3">{request.pharmacyName}</td>
                          <td className="px-4 py-3">{request.medicationName}</td>
                          <td className="px-4 py-3">{request.quantity}</td>
                          <td className="px-4 py-3">{request.requestedByName}</td>
                          <td className="px-4 py-3">{request.status}</td>
                          {canProcessReorderRequests ? (
                            <td className="px-4 py-3">
                              {request.status === 'PENDING' ? (
                                <button
                                  type="button"
                                  className="rounded-lg bg-emerald-600 px-3 py-2 font-semibold text-white disabled:opacity-60"
                                  onClick={() => handleProcessReorderRequest(request)}
                                  disabled={isProcessing}
                                >
                                  {isProcessing ? 'Processing...' : 'Fulfill request'}
                                </button>
                              ) : request.processedByName ? `Processed by ${request.processedByName}` : 'Fulfilled'}
                            </td>
                          ) : null}
                        </tr>
                      )
                    }) : (
                      <tr>
                        <td colSpan={canProcessReorderRequests ? 6 : 5} className="px-4 py-6 text-center text-slate-600">
                          No reorder requests yet.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            ) : null}
          </section>
        </>
      ) : null}
    </div>
  )
}

export default PharmacyPage
