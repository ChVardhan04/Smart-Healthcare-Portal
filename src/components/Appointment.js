import { useState, useEffect } from "react";
import axios from "axios";

export default function Appointment({ user }) {
  const [doctors, setDoctors] = useState([]);
  const [form, setForm] = useState({ patientId: user?.id || 1, doctorName: "", appointmentTime: "" });

  useEffect(() => {
    const fetchDoctors = async () => {
      try {
        const res = await axios.get("http://localhost:8080/api/doctors"); // Adjust endpoint
        setDoctors(res.data);
      } catch (err) {
        alert("Error fetching doctors: " + err.message);
      }
    };
    fetchDoctors();
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post("http://localhost:8080/api/appointments/book", form);
      alert("Appointment booked with " + res.data.doctorName);
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ padding: "20px" }}>
      <h2>Book Appointment</h2>

      <label>Doctor:</label> <br />
      <select name="doctorName" onChange={handleChange} required>
        <option value="">Select a doctor</option>
        {doctors.map((doc) => (
          <option key={doc.id} value={doc.name}>
            {doc.name} ({doc.specialization})
          </option>
        ))}
      </select>
      <br /><br />

      <label>Appointment Time:</label> <br />
      <input
        name="appointmentTime"
        type="datetime-local"
        onChange={handleChange}
        required
      />
      <br /><br />

      <button type="submit">Book</button>
    </form>
  );
}
