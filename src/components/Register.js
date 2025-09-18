import { useState } from "react";
import axios from "axios";

export default function Register() {
  const [form, setForm] = useState({ name: "", email: "", password: "" });

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post("http://localhost:8080/api/auth/register", form);
      alert("User registered: " + res.data.name);
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ padding: "20px" }}>
      <h2>Register</h2>
      <input name="name" placeholder="Name" onChange={handleChange} /> <br />
      <input name="email" placeholder="Email" onChange={handleChange} /> <br />
      <input name="password" type="password" placeholder="Password" onChange={handleChange} /> <br />
      <button type="submit">Register</button>
    </form>
  );
}
