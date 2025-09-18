import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from "react-router-dom";
import Navbar from "./components/Navbar";
import Login from "./components/Login";
import Register from "./components/Register";
import Appointment from "./components/Appointment";
import Prediction from "./components/Prediction";

function App() {
  const [user, setUser] = useState(null); // Track logged-in user

  return (
    <Router>
      {/* Pass user + setUser so Navbar can handle logout */}
      <Navbar user={user} setUser={setUser} />

      <Routes>
        {/* Home route */}
        <Route
          path="/"
          element={
            <div style={{ padding: "20px", textAlign: "center" }}>
              {user ? (
                <div>
                  <h2>Welcome, {user.name} ({user.role})</h2>
                  <p>Select an option from the navbar to continue</p>
                </div>
              ) : (
                <div>
                  <h2>Welcome to Smart Healthcare Portal</h2>
                  <p>Please register or login to continue.</p>

                  {/* Buttons */}
                  <div style={{ marginTop: "20px" }}>
                    <Link to="/register">
                      <button style={{ marginRight: "10px", padding: "10px 20px" }}>
                        Register
                      </button>
                    </Link>
                    <Link to="/login">
                      <button style={{ padding: "10px 20px" }}>
                        Login
                      </button>
                    </Link>
                  </div>
                </div>
              )}
            </div>
          }
        />

        {/* Auth routes */}
        <Route path="/login" element={<Login setUser={setUser} />} />
        <Route path="/register" element={<Register />} />

        {/* Protected routes */}
        <Route
          path="/appointment"
          element={user ? <Appointment user={user} /> : <Navigate to="/login" />}
        />
        <Route
          path="/predict"
          element={user ? <Prediction /> : <Navigate to="/login" />}
        />
      </Routes>
    </Router>
  );
}

export default App;
