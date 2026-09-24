import { API_BASE, readJson } from './api'

export async function loadUsersByRole(role) {
  const payload = await readJson(`${API_BASE}/api/users/all`)
  return (Array.isArray(payload) ? payload : [])
    .filter((user) => user?.role === role)
    .map((user) => ({
      value: user.id || '',
      label: formatUserOptionLabel(user),
        fullName: user.name || '',
    }))
}

function formatUserOptionLabel(user) {
  const primary = user?.name || user?.email || user?.phone || 'Unnamed user'
  const secondary = [user?.email, user?.phone].filter(Boolean).join(' • ')
  return secondary && secondary !== primary ? `${primary} (${secondary})` : primary
}
