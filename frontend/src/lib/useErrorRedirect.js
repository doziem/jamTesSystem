import { useNavigate } from 'react-router-dom'

export function useErrorRedirect() {
  const navigate = useNavigate()

  return (message) => {
    navigate('/error', { replace: true, state: { message } })
  }
}
