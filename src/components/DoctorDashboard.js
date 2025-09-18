// src/components/DoctorDashboard.jsx
import { useEffect, useState } from "react";
import axios from "axios";

export default function DoctorDashboard({ user }) {
  const [appointments, setAppointments] = useState([]);

  useEffect(() => {
    if (!user) return;
    const fetch = async () => {
      try {
        // doctor id should be doctors.id (not users.id) — backend must map from user
        // for simplicity assume user.id maps to doctors.user_id and backend /api/doctors will return doctor.id
        const resDoc = await axios.get(`http://localhost:8080/api/doctors/byUser/${user.id}`);
        const doctorId = resDoc.data.id;
        const res = await axios.get(`http://localhost:8080/api/appointments/doctor/${doctorId}`);
        setAppointments(res.data);
      } catch (err) {
        alert("Error fetching appointments: " + (err.response?.data?.message || err.message));
      }
    };
    fetch();
  }, [user]);

  const action = async (id, verb) => {
    try {
      const url = `http://localhost:8080/api/appointments/${id}/${verb}`;
      const res = await axios.put(url);
      // refresh
      setAppointments(prev => prev.map(a => a.id === res.data.id ? res.data : a));
      alert(`Appointment ${verb}ed`);
    } catch (err) {
      alert("Action failed: " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Doctor Dashboard</h2>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead><tr><th>Id</th><th>Patient</th><th>Slot</th><th>Mode</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
          {appointments.map(a => (
            <tr key={a.id}>
              <td>{a.id}</td>
              <td>{a.patientId}</td>
              <td>{new Date(a.slotId ? a.slotTime || "" : "").toLocaleString ? new Date(a.slotTime).toLocaleString() : a.slotId}</td>
              <td>{a.mode}</td>
              <td>{a.status}</td>
              <td>
                {a.status === "PENDING_APPROVAL" && (
                  <>
                    <button onClick={() => action(a.id, "approve")}>Approve</button>
                    <button onClick={() => action(a.id, "reject")}>Reject</button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
