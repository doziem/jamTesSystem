import { useEffect, useState } from 'react'
import { API_BASE, normalizeList, readJson } from '../lib/api'

function PharmacyPage() {
  const [pharmacies, setPharmacies] = useState([])
  const [medications, setMedications] = useState([])
  const [selectedPharmacyId, setSelectedPharmacyId] = useState('')
  const [loading, setLoading] = useState(true)
  const [inventoryLoading, setInventoryLoading] = useState(false)
  const [error, setError] = useState('')
  const [inventoryError, setInventoryError] = useState('')

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
        setError(err.message || 'Unable to load pharmacy records.')
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
        setInventoryError(err.message || 'Unable to load medication inventory.')
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

  const getWarningClass = (warningLevel = '') => {
    if (warningLevel.includes('LEVEL_1')) return 'status error'
    if (warningLevel.includes('LEVEL_5')) return 'status info'
    if (warningLevel.includes('LEVEL_10')) return 'status info'
    return 'status info'
  }

  return (
    <div className="page-panel">
      <div className="page-head">
        <div>
          <div className="eyebrow">Pharmacy operations</div>
          <h1>Pharmacies</h1>
        </div>
      </div>

      {loading ? <div className="status info">Loading pharmacies...</div> : null}
      {error ? <div className="status error">{error}</div> : null}

      {!loading && !error ? (
        <>
          <div className="module-list">
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
                  className={`module-card pharmacy-select ${isSelected ? 'selected' : ''}`}
                  onClick={() => setSelectedPharmacyId(pharmacyId)}
                >
                  <div className="module-main">
                    <strong>{name}</strong>
                    <span>{branch}</span>
                  </div>
                  <small>{location}</small>
                </button>
              )
            }) : <div className="status info">No pharmacy records available.</div>}
          </div>

          {selectedPharmacy ? (
            <div className="inventory-panel">
              <div className="page-head">
                <div>
                  <div className="eyebrow">Inventory</div>
                  <h2>{selectedPharmacy.name || 'Selected pharmacy'}</h2>
                </div>
                <small>{selectedPharmacy.department || 'Department pharmacy'}</small>
              </div>

              {inventoryLoading ? <div className="status info">Loading medication inventory...</div> : null}
              {inventoryError ? <div className="status error">{inventoryError}</div> : null}

              {!inventoryLoading && !inventoryError ? (
                <div className="inventory-table-wrap">
                  <table className="inventory-table">
                    <thead>
                      <tr>
                        <th>Medication</th>
                        <th>Available</th>
                        <th>Reorder</th>
                        <th>Status</th>
                        <th>Warning</th>
                      </tr>
                    </thead>
                    <tbody>
                      {medications.length > 0 ? medications.map((item) => (
                        <tr key={item.medicationId || item.id || item.medicationName}>
                          <td>{item.medicationName || 'Unknown medication'}</td>
                          <td>{item.quantityInStock ?? 0}</td>
                          <td>{item.reorderLevel ?? 0}</td>
                          <td>
                            <span className={getWarningClass(item.warningLevel)}>{item.stockLevel || 'AVAILABLE'}</span>
                          </td>
                          <td>{item.warningMessage || 'Sufficient stock available.'}</td>
                        </tr>
                      )) : (
                        <tr>
                          <td colSpan="5" className="empty-table">No medication records available for this pharmacy.</td>
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
