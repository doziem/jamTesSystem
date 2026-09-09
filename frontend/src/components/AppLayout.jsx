import { NavLink } from 'react-router-dom'

function AppLayout({ user, onLogout, children }) {
  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand-wrap">
          <div className="brand">JamTes System</div>
          <div className="user-pill">{user?.role || 'USER'}</div>
        </div>

        <nav className="nav-menu" aria-label="Main navigation">
          <NavLink to="/" end className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Overview
          </NavLink>
          <NavLink to="/patients" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Patients
          </NavLink>
          <NavLink to="/doctors" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Doctors
          </NavLink>
          <NavLink to="/pharmacies" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Pharmacies
          </NavLink>
          <NavLink to="/billing" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Billing
          </NavLink>
        </nav>

        <button type="button" className="logout-button" onClick={onLogout}>
          Log out
        </button>
      </header>

      <main className="content">
        <div className="user-bar">
          <div>
            <div className="eyebrow">Authenticated</div>
            <h2>{user?.name || 'Hospital staff'}</h2>
          </div>
          <small>{user?.email || 'No email available'}</small>
        </div>

        {children}
      </main>
    </div>
  )
}

export default AppLayout
