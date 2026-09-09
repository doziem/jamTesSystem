function DetailModal({ isOpen, title, fields, onClose, loading }) {
  if (!isOpen) {
    return null
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4" onClick={onClose} role="dialog" aria-modal="true">
      <div className="w-full max-w-2xl rounded-3xl bg-white p-5 shadow-2xl" onClick={(event) => event.stopPropagation()}>
        <div className="flex items-start justify-between gap-4 border-b border-slate-200 pb-4">
          <h3 className="text-xl font-bold text-slate-900">{title}</h3>
          <button type="button" className="rounded-full border border-slate-300 px-3 py-1 text-lg leading-none text-slate-600" onClick={onClose} aria-label="Close details">
            ×
          </button>
        </div>

        {loading ? (
          <div className="mt-4 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-900">Loading details...</div>
        ) : (
          <dl className="mt-4 grid gap-3 sm:grid-cols-2">
            {fields.length > 0 ? fields.map(([label, value]) => (
              <div key={label} className="rounded-2xl bg-slate-50 p-4">
                <dt className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">{label}</dt>
                <dd className="mt-1 text-sm font-medium text-slate-900">{value || 'N/A'}</dd>
              </div>
            )) : <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">No details available.</div>}
          </dl>
        )}
      </div>
    </div>
  )
}

export default DetailModal
