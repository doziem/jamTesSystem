export const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'
export const STORAGE_KEY = 'jamtes-auth-token'

export function getStoredToken() {
  return localStorage.getItem(STORAGE_KEY) || ''
}

export function getAuthHeaders(token = getStoredToken()) {
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export async function readJson(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      ...(options.headers || {}),
      ...getAuthHeaders(),
    },
  })

  const contentType = response.headers.get('content-type') || ''
  const payload = contentType.includes('application/json') ? await response.json() : await response.text()

  if (!response.ok) {
    const message = typeof payload === 'string' ? payload : payload?.message || 'Request failed'
    throw new Error(message)
  }

  return payload
}

export function normalizeList(payload) {
  if (Array.isArray(payload)) {
    if (payload.length === 1 && payload[0] && typeof payload[0] === 'object') {
      const item = payload[0]
      for (const key of ['data', 'content', 'items', 'results', 'records']) {
        if (Array.isArray(item[key])) {
          return item[key]
        }
      }
    }

    return payload
  }

  if (!payload || typeof payload !== 'object') {
    return []
  }

  for (const key of ['data', 'content', 'items', 'results', 'records']) {
    const value = payload[key]
    if (Array.isArray(value)) {
      return value
    }
  }

  const values = Object.values(payload)
  const firstArray = values.find(Array.isArray)
  return firstArray || []
}
