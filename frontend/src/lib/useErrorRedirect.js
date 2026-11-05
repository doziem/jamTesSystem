import { createContext, useContext } from 'react'

export const ErrorModalContext = createContext(null)

export function useErrorRedirect() {
  const showError = useContext(ErrorModalContext)
  if (!showError) {
    throw new Error('useErrorRedirect must be used within the application error modal provider.')
  }

  return showError
}
