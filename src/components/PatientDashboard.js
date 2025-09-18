// src/components/PatientDashboard.jsx
import { useEffect, useState } from "react";
import axios from "axios";

export default function PatientDashboard({ user }) {
  const [appointments, setAppointments] = useState([]);

  useEffect(() => {
    if (!user) return;
    const fetch = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/api/appointments/patient/${user.id}`);
        setAppointments(res.data);
      } catch (err) {
        alert("Error: " + (err.response?.data?.message || err.message));
      }
    };
    fetch();
  }, [user]);

  const pay = async (id) => {
    // placeholder: integrate real payment later
    try {
      await axios.put(`http://localhost:8080/api/appointments/${id}/confirm`);
      alert("Payment simulated; appointment confirmed.");
      setAppointments(prev => prev.map(a => a.id === id ? { ...a, status: "CONFIRMED" } : a));
    } catch (err) {
      alert("Payment failed: " + err.message);
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>My Appointments</h2>
      <table style={{ width: "100%" }}>
        <thead><tr><th>ID</th><th>Doctor</th><th>Slot</th><th>Mode</th><th>Status</th><th>Action</th></tr></thead>
        <tbody>
          {appointments.map(a => (
            <tr key={a.id}>
              <td>{a.id}</td>
              <td>{a.doctorId}</td>
              <td>{a.slotId}</td>
              <td>{a.mode}</td>
              <td>{a.status}</td>
              <td>
                {a.status === "APPROVED" && a.mode === "ONLINE" && (<button onClick={() => pay(a.id)}>Pay</button>)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
