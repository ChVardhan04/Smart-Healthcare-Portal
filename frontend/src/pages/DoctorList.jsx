import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import client from '../api/client'

export default function DoctorList() {
  const [params, setParams] = useSearchParams()
  const [doctors, setDoctors] = useState([])
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({
    specialty: params.get('specialty') || '',
    name: '',
    location: '',
  })

  const search = async (f = filters) => {
    setLoading(true)
    try {
      const res = await client.get('/doctors', { params: f })
      setDoctors(res.data)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { search() }, []) // eslint-disable-line

  const submit = (e) => {
    e.preventDefault()
    search()
  }

  return (
    <div className="container page">
      <h2>Find a doctor</h2>
      <form onSubmit={submit} className="card" style={{ marginBottom: 20 }}>
        <div className="grid-3">
          <div className="field" style={{ marginBottom: 0 }}>
            <label>Specialty</label>
            <input placeholder="e.g. Cardiologist" value={filters.specialty}
              onChange={(e) => setFilters({ ...filters, specialty: e.target.value })} />
          </div>
          <div className="field" style={{ marginBottom: 0 }}>
            <label>Doctor name</label>
            <input value={filters.name}
              onChange={(e) => setFilters({ ...filters, name: e.target.value })} />
          </div>
          <div className="field" style={{ marginBottom: 0 }}>
            <label>Location</label>
            <input value={filters.location}
              onChange={(e) => setFilters({ ...filters, location: e.target.value })} />
          </div>
        </div>
        <button className="btn btn-primary" style={{ marginTop: 14 }}>Search</button>
      </form>

      {loading && <p className="muted">Loading doctors…</p>}
      {!loading && doctors.length === 0 && (
        <div className="empty-state">No doctors matched your search. Try a broader filter.</div>
      )}

      <div className="grid-3">
        {doctors.map((d) => (
          <Link to={`/doctors/${d.id}`} key={d.id} className="card" style={{ textDecoration: 'none' }}>
            <span className="pill pill-sage">{d.specialty}</span>
            <h3 style={{ marginTop: 10 }}>Dr. {d.name}</h3>
            <p className="muted" style={{ fontSize: '0.85rem' }}>
              {d.qualifications || 'Qualified practitioner'} {d.yearsExperience ? `· ${d.yearsExperience} yrs experience` : ''}
            </p>
            <p className="muted" style={{ fontSize: '0.85rem' }}>{d.location || d.clinicAddress}</p>
            {d.consultationFee && <p style={{ fontWeight: 600, marginTop: 8 }}>₹{d.consultationFee} consult</p>}
          </Link>
        ))}
      </div>
    </div>
  )
}
