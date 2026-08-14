import { Link } from 'react-router-dom'

export default function Home() {
  return (
    <div>
      <div className="hero">
        <div className="container">
          <h1>Book a doctor, or figure out what's wrong first.</h1>
          <p>
            Search doctors by specialty, book a slot in real time - no double-booking,
            ever - or run a quick symptom check to see which specialist to see.
          </p>
          <div className="row" style={{ marginTop: 24 }}>
            <Link to="/doctors" className="btn btn-primary">Find a doctor</Link>
            <Link to="/symptom-checker" className="btn btn-secondary" style={{ background: 'rgba(255,255,255,0.1)', color: '#fff', borderColor: 'rgba(255,255,255,0.3)' }}>
              Check my symptoms
            </Link>
          </div>
        </div>
      </div>

      <div className="container page">
        <div className="grid-3">
          <div className="card">
            <h3>Live slot availability</h3>
            <p className="muted">Every slot shows exactly how many seats are left, and updates instantly the moment one fills up - no overbooking.</p>
          </div>
          <div className="card">
            <h3>Symptom-based triage</h3>
            <p className="muted">Select what you're feeling and get a ranked list of likely conditions with the right specialist to see, before you book.</p>
          </div>
          <div className="card">
            <h3>Approval workflow</h3>
            <p className="muted">Doctors review and approve or reject each request, with instant email notifications to patients either way.</p>
          </div>
        </div>
      </div>
    </div>
  )
}
