import { useEffect, useState } from 'react'
import client from '../api/client'

export default function DoctorDashboard() {
  const [tab, setTab] = useState('pending')
  const [appointments, setAppointments] = useState([])
  const [slots, setSlots] = useState([])
  const [error, setError] = useState('')
  const [slotForm, setSlotForm] = useState({ date: '', startTime: '', endTime: '', mode: 'OFFLINE', capacity: 1 })

  const loadAppointments = async (status) => {
    const res = await client.get('/doctor-dashboard/appointments', { params: status ? { status } : {} })
    setAppointments(res.data)
  }
  const loadSlots = async () => {
    const res = await client.get('/doctor-dashboard/slots')
    setSlots(res.data)
  }

  useEffect(() => {
    if (tab === 'slots') loadSlots()
    else loadAppointments(tab === 'pending' ? 'PENDING' : tab === 'all' ? null : tab.toUpperCase())
  }, [tab]) // eslint-disable-line

  const decide = async (id, status) => {
    setError('')
    try {
      await client.put(`/doctor-dashboard/appointments/${id}/status`, { status })
      loadAppointments(tab === 'pending' ? 'PENDING' : null)
    } catch (err) {
      setError(err.message)
    }
  }

  const createSlot = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await client.post('/doctor-dashboard/slots', {
        ...slotForm,
        capacity: Number(slotForm.capacity),
      })
      setSlotForm({ date: '', startTime: '', endTime: '', mode: 'OFFLINE', capacity: 1 })
      loadSlots()
    } catch (err) {
      setError(err.message)
    }
  }

  const removeSlot = async (id) => {
    try {
      await client.delete(`/doctor-dashboard/slots/${id}`)
      loadSlots()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="container page">
      <h2>Doctor dashboard</h2>
      {error && <div className="alert alert-error">{error}</div>}

      <div className="row" style={{ marginBottom: 20 }}>
        {['pending', 'approved', 'rejected', 'all', 'slots'].map((t) => (
          <button key={t}
            className={tab === t ? 'btn btn-primary btn-sm' : 'btn btn-secondary btn-sm'}
            onClick={() => setTab(t)}>
            {t[0].toUpperCase() + t.slice(1)}
          </button>
        ))}
      </div>

      {tab !== 'slots' && (
        <div className="stack">
          {appointments.length === 0 && <div className="empty-state">Nothing here right now.</div>}
          {appointments.map((a) => (
            <div className="card" key={a.id}>
              <div className="flex-between">
                <div>
                  <h3 style={{ marginBottom: 4 }}>{a.patient.user.name}</h3>
                  <p className="muted" style={{ margin: 0 }}>
                    {a.slot.date} · {a.slot.startTime.slice(0, 5)}–{a.slot.endTime.slice(0, 5)} · {a.mode}
                  </p>
                </div>
                <span className="pill pill-muted">{a.status}</span>
              </div>
              {a.patientNotes && <p style={{ marginTop: 10 }}><strong>Patient notes:</strong> {a.patientNotes}</p>}
              {a.status === 'PENDING' && (
                <div className="row" style={{ marginTop: 12 }}>
                  <button className="btn btn-sage btn-sm" onClick={() => decide(a.id, 'APPROVED')}>Approve</button>
                  <button className="btn btn-danger btn-sm" onClick={() => decide(a.id, 'REJECTED')}>Reject</button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {tab === 'slots' && (
        <div className="grid-2">
          <form onSubmit={createSlot} className="card">
            <h3>Add a slot</h3>
            <div className="field">
              <label>Date</label>
              <input type="date" required value={slotForm.date} onChange={(e) => setSlotForm({ ...slotForm, date: e.target.value })} />
            </div>
            <div className="grid-2">
              <div className="field">
                <label>Start time</label>
                <input type="time" required value={slotForm.startTime} onChange={(e) => setSlotForm({ ...slotForm, startTime: e.target.value })} />
              </div>
              <div className="field">
                <label>End time</label>
                <input type="time" required value={slotForm.endTime} onChange={(e) => setSlotForm({ ...slotForm, endTime: e.target.value })} />
              </div>
            </div>
            <div className="grid-2">
              <div className="field">
                <label>Mode</label>
                <select value={slotForm.mode} onChange={(e) => setSlotForm({ ...slotForm, mode: e.target.value })}>
                  <option value="OFFLINE">In-person</option>
                  <option value="ONLINE">Online</option>
                </select>
              </div>
              <div className="field">
                <label>Capacity (seats)</label>
                <input type="number" min={1} required value={slotForm.capacity} onChange={(e) => setSlotForm({ ...slotForm, capacity: e.target.value })} />
              </div>
            </div>
            <button className="btn btn-primary btn-block">Create slot</button>
          </form>

          <div>
            <h3>Upcoming slots</h3>
            <div className="stack">
              {slots.length === 0 && <p className="muted">No upcoming slots yet.</p>}
              {slots.map((s) => (
                <div className="card" key={s.id}>
                  <div className="flex-between">
                    <div>
                      <p style={{ fontWeight: 600, margin: 0 }}>{s.date} · {s.startTime.slice(0, 5)}–{s.endTime.slice(0, 5)}</p>
                      <p className="muted" style={{ margin: 0, fontSize: '0.85rem' }}>
                        {s.mode} · {s.remainingSeats}/{s.capacity} seats remaining {s.full && '· FULL'}
                      </p>
                    </div>
                    <button className="btn btn-secondary btn-sm" onClick={() => removeSlot(s.id)}>Remove</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
