import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import client from '../api/client'

export default function SymptomChecker() {
  const [allSymptoms, setAllSymptoms] = useState([])
  const [selected, setSelected] = useState([])
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    client.get('/predict/symptoms').then((res) => setAllSymptoms(res.data))
  }, [])

  const toggle = (name) => {
    setSelected((prev) => prev.includes(name) ? prev.filter((s) => s !== name) : [...prev, name])
  }

  const grouped = allSymptoms.reduce((acc, s) => {
    acc[s.category] = acc[s.category] || []
    acc[s.category].push(s)
    return acc
  }, {})

  const check = async () => {
    setError('')
    setLoading(true)
    setResults(null)
    try {
      const res = await client.post('/predict', { symptoms: selected })
      setResults(res.data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container page">
      <h2>Symptom checker</h2>
      <p className="muted" style={{ maxWidth: 640 }}>
        Select everything you're currently experiencing. This gives you a starting point
        for which specialist to see - it isn't a diagnosis, and severe or emergency symptoms
        always warrant seeing a doctor right away regardless of what shows up here.
      </p>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="card" style={{ marginBottom: 20 }}>
        {Object.entries(grouped).map(([category, syms]) => (
          <div key={category} style={{ marginBottom: 16 }}>
            <p style={{ fontWeight: 600, fontSize: '0.85rem', color: 'var(--navy)', marginBottom: 8 }}>{category}</p>
            <div className="chip-wrap">
              {syms.map((s) => (
                <button
                  key={s.id}
                  type="button"
                  className={`symptom-chip ${selected.includes(s.name) ? 'symptom-chip-selected' : ''}`}
                  onClick={() => toggle(s.name)}
                >
                  {s.displayName}
                </button>
              ))}
            </div>
          </div>
        ))}
        <button className="btn btn-primary" onClick={check} disabled={loading || selected.length === 0} style={{ marginTop: 10 }}>
          {loading ? 'Analyzing…' : `Check ${selected.length ? `(${selected.length} selected)` : ''}`}
        </button>
      </div>

      {results && (
        <div>
          <h3>Possible conditions to discuss with a doctor</h3>
          {results.some((r) => r.severity === 'EMERGENCY') && (
            <div className="alert alert-error">
              One or more matched conditions can be a medical emergency. If you're experiencing
              severe chest pain, difficulty breathing, or loss of consciousness, seek emergency
              care immediately rather than waiting on this tool.
            </div>
          )}
          <div className="stack">
            {results.map((r, i) => (
              <div className="card" key={i}>
                <div className="flex-between">
                  <h3 style={{ margin: 0 }}>{r.disease}</h3>
                  <span className={`pill ${r.severity === 'EMERGENCY' ? 'pill-coral' : r.severity === 'HIGH' ? 'pill-amber' : 'pill-sage'}`}>
                    {r.severity}
                  </span>
                </div>
                <p className="muted" style={{ marginTop: 8 }}>{r.description}</p>
                {r.matchedSymptoms?.length > 0 && (
                  <p style={{ fontSize: '0.85rem' }}>
                    <strong>Matched:</strong> {r.matchedSymptoms.join(', ')}
                  </p>
                )}
                <div className="match-score-bar">
                  <div className="match-score-fill" style={{ width: `${r.score}%` }} />
                </div>
                <div className="flex-between" style={{ marginTop: 10 }}>
                  <span className="muted" style={{ fontSize: '0.8rem' }}>{r.score}% symptom match</span>
                  <Link to={`/doctors?specialty=${encodeURIComponent(r.specialty || '')}`} className="btn btn-secondary btn-sm">
                    See {r.specialty || 'specialists'}
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
