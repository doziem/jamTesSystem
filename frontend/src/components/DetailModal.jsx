function DetailModal({ isOpen, title, fields, onClose, loading }) {
  if (!isOpen) {
    return null
  }

  return (
    <div className="modal-overlay" onClick={onClose} role="dialog" aria-modal="true">
      <div className="modal-card" onClick={(event) => event.stopPropagation()}>
        <div className="modal-header">
          <h3>{title}</h3>
          <button type="button" className="close-button" onClick={onClose} aria-label="Close details">
            ×
          </button>
        </div>

        {loading ? (
          <div className="status info">Loading details...</div>
        ) : (
          <dl className="detail-list">
            {fields.length > 0 ? fields.map(([label, value]) => (
              <div key={label} className="detail-row">
                <dt>{label}</dt>
                <dd>{value || 'N/A'}</dd>
              </div>
            )) : <div className="status info">No details available.</div>}
          </dl>
        )}
      </div>
    </div>
  )
}

export default DetailModal
