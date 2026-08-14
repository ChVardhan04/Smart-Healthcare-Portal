import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const SPECIALTIES = [
  'General Physician', 'Cardiologist', 'Pulmonologist', 'Neurologist',
  'Gastroenterologist', 'Endocrinologist', 'Dermatologist', 'Orthopedist',
  'Psychiatrist', 'ENT Specialist',
]

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [role, setRole] = useState('PATIENT')
  const [form, setForm] = useState({
    name: '', email: '', password: '', phone: '',
    specialty: '', qualifications: '', clinicAddress: '', location: '',
    consultationFee: '', onlineFee: '', yearsExperience: '',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const update = (key) => (e) => setForm({ ...form, [key]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const payload = { ...form, role }
      if (role === 'DOCTOR') {
        payload.consultationFee = form.consultationFee ? Number(form.consultationFee) : null
        payload.onlineFee = form.onlineFee ? Number(form.onlineFee) : null
        payload.yearsExperience = form.yearsExperience ? Number(form.yearsExperience) : null
      }
      const data = await register(payload)
      navigate(data.role === 'DOCTOR' ? '/doctor-dashboard' : '/dashboard')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container page" style={{ maxWidth: 480 }}>
      <h2>Create an account</h2>

      <div className="row" style={{ marginBottom: 20 }}>
        <button type="button"
          className={role === 'PATIENT' ? 'btn btn-primary' : 'btn btn-secondary'}
          onClick={() => setRole('PATIENT')}>I'm a Patient</button>
        <button type="button"
          className={role === 'DOCTOR' ? 'btn btn-primary' : 'btn btn-secondary'}
          onClick={() => setRole('DOCTOR')}>I'm a Doctor</button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <form onSubmit={submit} className="card">
        <div className="field">
          <label>Full name</label>
          <input required value={form.name} onChange={update('name')} />
        </div>
        <div className="field">
          <label>Email</label>
          <input type="email" required value={form.email} onChange={update('email')} />
        </div>
        <div className="field">
          <label>Password</label>
          <input type="password" required minLength={6} value={form.password} onChange={update('password')} />
        </div>
        <div className="field">
          <label>Phone</label>
          <input value={form.phone} onChange={update('phone')} />
        </div>

        {role === 'DOCTOR' && (
          <>
            <div className="field">
              <label>Specialty</label>
              <select required value={form.specialty} onChange={update('specialty')}>
                <option value="">Select specialty</option>
                {SPECIALTIES.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
            <div className="field">
              <label>Qualifications</label>
              <input placeholder="e.g. MBBS, MD" value={form.qualifications} onChange={update('qualifications')} />
            </div>
            <div className="grid-2">
              <div className="field">
                <label>Clinic address</label>
                <input value={form.clinicAddress} onChange={update('clinicAddress')} />
              </div>
              <div className="field">
                <label>Location / City</label>
                <input value={form.location} onChange={update('location')} />
              </div>
            </div>
            <div className="grid-2">
              <div className="field">
                <label>Offline consultation fee (₹)</label>
                <input type="number" value={form.consultationFee} onChange={update('consultationFee')} />
              </div>
              <div className="field">
                <label>Online consultation fee (₹)</label>
                <input type="number" value={form.onlineFee} onChange={update('onlineFee')} />
              </div>
            </div>
            <div className="field">
              <label>Years of experience</label>
              <input type="number" value={form.yearsExperience} onChange={update('yearsExperience')} />
            </div>
          </>
        )}

        <button className="btn btn-primary btn-block" disabled={loading}>
          {loading ? 'Creating account…' : 'Create account'}
        </button>
      </form>
      <p className="muted" style={{ marginTop: 12 }}>
        Already have an account? <Link to="/login">Log in</Link>
      </p>
    </div>
  )
}
