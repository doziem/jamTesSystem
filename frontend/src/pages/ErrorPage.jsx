import { Link, useLocation } from 'react-router-dom'

function ErrorPage() {
  const location = useLocation()
  const message = location.state?.message || 'Something went wrong. Please try again.'

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-6 text-slate-900 sm:px-6 lg:px-8">
      <main className="mx-auto flex min-h-[calc(100vh-3rem)] w-full max-w-lg items-center justify-center">
        <section className="w-full rounded-2xl border border-slate-200 bg-white p-5 text-center shadow-sm">
          <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-red-50 text-xl text-red-600">!</div>
          <h1 className="mt-4 text-2xl font-bold tracking-tight">We hit a problem</h1>
          <p className="mt-2 text-sm text-slate-600">{message}</p>
          <div className="mt-4 flex flex-wrap justify-center gap-2">
            <Link to="/" className="rounded-lg btn-brand px-3.5 py-2 text-sm font-semibold">
              Go to dashboard
            </Link>
            <Link to="/login" className="rounded-lg border border-slate-300 px-3.5 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100">
              Back to login
            </Link>
          </div>
        </section>
      </main>
    </div>
  )
}

export default ErrorPage
