import { useEffect, useState } from 'react'
import client from '../api/client'

const STATUS_PILL = {
  PENDING: 'pill-amber',
  APPROVED: 'pill-sage',
  REJECTED: 'pill-coral',
  CANCELLED: 'pill-muted',
  COMPLETED: 'pill-muted',
}

export default function PatientDashboard() {
  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = async () => {
    setLoading(true)
    try {
      const res = await client.get('/patients/me/appointments')
      setAppointments(res.data)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const cancel = async (id) => {
    setError('')
    try {
      await client.post(`/patients/me/appointments/${id}/cancel`)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  const payNow = async (appt) => {
    setError('')
    try {
      const intent = await client.post(`/patients/me/appointments/${appt.id}/pay/create-intent`)
      // Mock provider: confirm immediately (a real integration would redirect to a hosted checkout here)
      await client.post(`/patients/me/payments/${intent.data.id}/confirm`)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="container page">
      <h2>My appointments</h2>
      {error && <div className="alert alert-error">{error}</div>}
      {loading && <p className="muted">Loading…</p>}
      {!loading && appointments.length === 0 && (
        <div className="empty-state">
          No appointments yet. <a href="/doctors">Find a doctor</a> to get started.
        </div>
      )}
      <div className="stack">
        {appointments.map((a) => (
          <div className="card" key={a.id}>
            <div className="flex-between">
              <div>
                <h3 style={{ marginBottom: 4 }}>Dr. {a.doctor.user.name} · {a.doctor.specialty}</h3>
                <p className="muted" style={{ margin: 0 }}>
                  {a.slot.date} · {a.slot.startTime.slice(0, 5)}–{a.slot.endTime.slice(0, 5)} · {a.mode}
                </p>
              </div>
              <span className={`pill ${STATUS_PILL[a.status]}`}>{a.status}</span>
            </div>
            {a.patientNotes && <p style={{ marginTop: 10 }}><strong>Your notes:</strong> {a.patientNotes}</p>}
            {a.doctorComment && <p><strong>Doctor's note:</strong> {a.doctorComment}</p>}

            <div className="row" style={{ marginTop: 12 }}>
              {(a.status === 'PENDING' || a.status === 'APPROVED') && (
                <button className="btn btn-danger btn-sm" onClick={() => cancel(a.id)}>Cancel</button>
              )}
              {a.paymentStatus === 'PENDING_PAYMENT' && (
                <button className="btn btn-sage btn-sm" onClick={() => payNow(a)}>Pay now</button>
              )}
              {a.paymentStatus === 'PAID' && <span className="pill pill-sage">Paid</span>}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
