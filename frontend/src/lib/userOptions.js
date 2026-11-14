import { API_BASE, readJson } from './api'

export async function loadUsersByRole(role) {
  const payload = await readJson(`${API_BASE}/api/staff?role=${encodeURIComponent(role)}`)
  return (Array.isArray(payload) ? payload : [])
    .map((staff) => ({
      value: staff.userId || '',
      label: formatUserOptionLabel(staff),
      fullName: staff.fullName || '',
    }))
}

function formatUserOptionLabel(user) {
  const primary = user?.fullName || user?.name || user?.email || user?.phone || 'Unnamed user'
  const secondary = [user?.email, user?.phone].filter(Boolean).join(' • ')
  return secondary && secondary !== primary ? `${primary} (${secondary})` : primary
}
