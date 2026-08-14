import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Nav() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  return (
    <div className="topnav">
      <div className="topnav-inner">
        <Link to="/" className="brand">
          <span className="brand-mark" />
          Smart Healthcare
        </Link>
        <div className="nav-links">
          <Link to="/doctors">Find a Doctor</Link>
          <Link to="/symptom-checker">Symptom Checker</Link>
          {user?.role === 'PATIENT' && <Link to="/dashboard">My Appointments</Link>}
          {user?.role === 'DOCTOR' && <Link to="/doctor-dashboard">Dashboard</Link>}
          {!user && <Link to="/login">Log in</Link>}
          {!user && (
            <Link to="/register" className="btn btn-primary btn-sm">Get started</Link>
          )}
          {user && (
            <button
              className="btn btn-secondary btn-sm"
              onClick={() => { logout(); navigate('/') }}
            >
              Log out
            </button>
          )}
        </div>
      </div>
    </div>
  )
}
