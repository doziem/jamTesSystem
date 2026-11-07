export const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'
export const STORAGE_KEY = 'jamtes-auth-token'
export const USER_STORAGE_KEY = 'jamtes-auth-user'
const EXPIRED_TOKEN_MESSAGE = 'Token expired'

function isTokenExpired(token) {
  const payload = token.split('.')[1]
  if (!payload) return false

  try {
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/')
    const claims = JSON.parse(atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, '=')))
    return typeof claims.exp === 'number' && claims.exp * 1000 <= Date.now()
  } catch {
    return false
  }
}

export function getStoredToken() {
  const token = localStorage.getItem(STORAGE_KEY) || ''
  if (token && isTokenExpired(token)) {
    redirectToLogin(EXPIRED_TOKEN_MESSAGE)
    return ''
  }

  return token
}

export function getAuthHeaders(token = getStoredToken()) {
  return token ? { Authorization: `Bearer ${token}` } : {}
}

function clearStoredAuth() {
  localStorage.removeItem(STORAGE_KEY)
  localStorage.removeItem(USER_STORAGE_KEY)
}

function redirectToLogin(message) {
  if (typeof window === 'undefined') {
    return
  }

  clearStoredAuth()
  const params = new URLSearchParams()
  if (message) {
    params.set('message', message)
  }

  const loginUrl = `/login${params.toString() ? `?${params.toString()}` : ''}`
  if (window.location.pathname !== '/login' || window.location.search !== (params.toString() ? `?${params.toString()}` : '')) {
    window.location.replace(loginUrl)
  }
}

function handleUnauthorizedResponse(response, payload) {
  const message = typeof payload === 'string' ? payload : payload?.message || 'Your session has expired. Please log in again.'
  const normalizedMessage = message.toLowerCase()
  const token = localStorage.getItem(STORAGE_KEY) || ''
  const tokenExpired = isTokenExpired(token)

  if (response.status === 401 || tokenExpired || normalizedMessage.includes('token expired') || normalizedMessage.includes('jwt expired')) {
    redirectToLogin(EXPIRED_TOKEN_MESSAGE)
    throw new Error(message)
  }

  return message
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
    const message = handleUnauthorizedResponse(response, payload)
    throw new Error(message)
  }

  return payload
}

export async function writeJson(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
      ...getAuthHeaders(),
    },
  })

  const contentType = response.headers.get('content-type') || ''
  const payload = contentType.includes('application/json') ? await response.json() : await response.text()

  if (!response.ok) {
    const message = handleUnauthorizedResponse(response, payload)
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

export async function readAllPages(url, pageSize = 100) {
  const results = []
  for (let page = 0; ; page += 1) {
    const separator = url.includes('?') ? '&' : '?'
    const items = normalizeList(await readJson(`${url}${separator}page=${page}&size=${pageSize}`))
    results.push(...items)
    if (items.length < pageSize) return results
  }
}
