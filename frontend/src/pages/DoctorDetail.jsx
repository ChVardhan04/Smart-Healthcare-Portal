import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import client from '../api/client'
import { useAuth } from '../context/AuthContext'

export default function DoctorDetail() {
  const { id } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [doctor, setDoctor] = useState(null)
  const [slots, setSlots] = useState([])
  const [selectedSlot, setSelectedSlot] = useState(null)
  const [mode, setMode] = useState('OFFLINE')
  const [notes, setNotes] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [booking, setBooking] = useState(false)

  const load = async () => {
    const [dRes, sRes] = await Promise.all([
      client.get(`/doctors/${id}`),
      client.get(`/doctors/${id}/slots`),
    ])
    setDoctor(dRes.data)
    setSlots(sRes.data)
  }

  useEffect(() => { load() }, [id]) // eslint-disable-line

  const book = async () => {
    if (!user) { navigate('/login'); return }
    if (!selectedSlot) { setError('Please select a time slot first'); return }
    setError('')
    setSuccess('')
    setBooking(true)
    try {
      await client.post('/patients/me/appointments', {
        doctorId: Number(id),
        slotId: selectedSlot.id,
        mode,
        patientNotes: notes,
      })
      setSuccess('Appointment requested. The doctor will approve or reject it shortly - you\'ll get an email either way.')
      setSelectedSlot(null)
      setNotes('')
      // Refresh slots immediately so the seat count reflects the booking we just made
      const sRes = await client.get(`/doctors/${id}/slots`)
      setSlots(sRes.data)
    } catch (err) {
      setError(err.message)
      // If another patient took the last seat, refresh so the UI matches reality
      const sRes = await client.get(`/doctors/${id}/slots`)
      setSlots(sRes.data)
      setSelectedSlot(null)
    } finally {
      setBooking(false)
    }
  }

  if (!doctor) return <div className="container page"><p className="muted">Loading…</p></div>

  const grouped = slots.reduce((acc, s) => {
    acc[s.date] = acc[s.date] || []
    acc[s.date].push(s)
    return acc
  }, {})

  return (
    <div className="container page">
      <div className="grid-2">
        <div>
          <span className="pill pill-sage">{doctor.specialty}</span>
          <h1 style={{ marginTop: 10 }}>Dr. {doctor.name}</h1>
          <p className="muted">{doctor.qualifications} {doctor.yearsExperience ? `· ${doctor.yearsExperience} years experience` : ''}</p>
          <p className="muted">{doctor.clinicAddress || doctor.location}</p>
          {doctor.bio && <p>{doctor.bio}</p>}
          <div className="row" style={{ marginTop: 10 }}>
            {doctor.consultationFee && <span className="pill pill-muted">₹{doctor.consultationFee} in-person</span>}
            {doctor.onlineFee && <span className="pill pill-muted">₹{doctor.onlineFee} online</span>}
          </div>
        </div>

        <div className="card">
          <h3>Book an appointment</h3>
          {error && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}

          <div className="field">
            <label>Consultation type</label>
            <div className="row">
              <button type="button" className={mode === 'OFFLINE' ? 'btn btn-primary btn-sm' : 'btn btn-secondary btn-sm'} onClick={() => setMode('OFFLINE')}>In-person</button>
              <button type="button" className={mode === 'ONLINE' ? 'btn btn-primary btn-sm' : 'btn btn-secondary btn-sm'} onClick={() => setMode('ONLINE')}>Online</button>
            </div>
          </div>

          <div className="field">
            <label>Available slots</label>
            {Object.keys(grouped).length === 0 && <p className="muted">No upcoming slots. Check back soon.</p>}
            {Object.entries(grouped).map(([date, daySlots]) => (
              <div key={date} style={{ marginBottom: 14 }}>
                <p style={{ fontWeight: 600, fontSize: '0.85rem', marginBottom: 6 }}>{date}</p>
                <div className="slot-grid">
                  {daySlots.filter((s) => s.mode === mode).map((s) => (
                    <button
                      key={s.id}
                      type="button"
                      disabled={s.full}
                      className={`slot-chip ${s.full ? 'slot-chip-full' : ''} ${selectedSlot?.id === s.id ? 'slot-chip-selected' : ''}`}
                      onClick={() => setSelectedSlot(s)}
                    >
                      <div className="slot-chip-time">{s.startTime.slice(0, 5)}–{s.endTime.slice(0, 5)}</div>
                      <div className="slot-chip-meta">
                        {s.full ? 'Fully booked' : `${s.remainingSeats}/${s.capacity} seats left`}
                      </div>
                    </button>
                  ))}
                  {daySlots.filter((s) => s.mode === mode).length === 0 && (
                    <p className="muted" style={{ fontSize: '0.82rem' }}>No {mode.toLowerCase()} slots this day.</p>
                  )}
                </div>
              </div>
            ))}
          </div>

          <div className="field">
            <label>Notes for the doctor (optional)</label>
            <textarea rows={3} value={notes} onChange={(e) => setNotes(e.target.value)} />
          </div>

          <button className="btn btn-primary btn-block" onClick={book} disabled={booking}>
            {booking ? 'Requesting…' : user ? 'Request appointment' : 'Log in to book'}
          </button>
        </div>
      </div>
    </div>
  )
}
