// src/components/Appointment.jsx
import { useState, useEffect } from "react";
import axios from "axios";

export default function Appointment({ user }) {
  const [doctors, setDoctors] = useState([]);
  const [slots, setSlots] = useState([]);
  const [form, setForm] = useState({
    patientId: user?.id || null,
    doctorId: "",
    slotId: "",
    mode: "OFFLINE",
    reason: ""
  });

  useEffect(() => {
    // fetch doctors: we expose /api/doctors that returns doctors with id,userId,specialization,name
    const fetchDoctors = async () => {
      try {
        const res = await axios.get("http://localhost:8080/api/doctors");
        setDoctors(res.data);
      } catch (err) {
        alert("Error fetching doctors: " + (err.response?.data?.message || err.message));
      }
    };
    fetchDoctors();
  }, []);

  const fetchSlots = async (doctorId) => {
    if (!doctorId) return setSlots([]);
    try {
      const res = await axios.get(`http://localhost:8080/api/appointments/slots/${doctorId}`);

      setSlots(res.data);
    } catch (err) {
      alert("Error fetching slots: " + (err.response?.data?.message || err.message));
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    const next = { ...form, [name]: value };
    setForm(next);
    if (name === "doctorId") {
      fetchSlots(value);
    }
  };

  const submit = async (e) => {
    e.preventDefault();
    if (!form.patientId) {
      return alert("Please login as patient first");
    }
    try {
      const res = await axios.post("http://localhost:8080/api/appointments/book", form);
      alert("Appointment request sent, waiting for approval.");
      // optionally redirect to patient dashboard
    } catch (err) {
      alert("Booking failed: " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div style={{ padding: 20, maxWidth: 700 }}>
      <h2>Book Appointment</h2>

      <label>Doctor</label><br />
      <select name="doctorId" value={form.doctorId} onChange={handleChange} required>
        <option value="">-- Select doctor --</option>
        {doctors.map(doc => (
          <option key={doc.id} value={doc.id}>{doc.name} ({doc.specialization})</option>
        ))}
      </select>

      <br /><br />
      <label>Available Slots</label><br />
      <select name="slotId" value={form.slotId} onChange={handleChange} required>
        <option value="">-- Select slot --</option>
        {slots.map(s => (
          <option key={s.id} value={s.id}>{new Date(s.slotTime).toLocaleString()}</option>
        ))}
      </select>

      <br /><br />
      <label>Mode</label><br />
      <select name="mode" value={form.mode} onChange={handleChange}>
        <option value="OFFLINE">Offline</option>
        <option value="ONLINE">Online</option>
      </select>

      <br /><br />
      <label>Reason (optional)</label><br />
      <textarea name="reason" value={form.reason} onChange={e => setForm({...form, reason: e.target.value})} />

      <br /><br />
      <button onClick={submit}>Book</button>
    </div>
  );
}
