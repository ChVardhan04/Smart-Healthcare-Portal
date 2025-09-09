import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Login from "./components/Login";
import Register from "./components/Register";
import Appointment from "./components/Appointment";
import Prediction from "./components/Prediction";

function App() {
  const [user, setUser] = useState(null); // Track logged-in user

  return (
    <Router>
      <Navbar />
      <Routes>
        {/* Home route */}
        <Route
          path="/"
          element={
            <div style={{ padding: "20px" }}>
              {user ? (
                <div>
                  <h2>Welcome, {user.name}</h2>
                  <p>Select an option from the navbar to continue</p>
                </div>
              ) : (
                <div>
                  <h2>Register</h2>
                  <Register />
                  <h2>Or Login</h2>
                  <Login setUser={setUser} />
                </div>
              )}
            </div>
          }
        />

        {/* Other pages */}
        <Route path="/login" element={<Login setUser={setUser} />} />
        <Route path="/register" element={<Register />} />
        <Route path="/appointment" element={user ? <Appointment /> : <Login setUser={setUser} />} />
        <Route path="/predict" element={user ? <Prediction /> : <Login setUser={setUser} />} />
      </Routes>
    </Router>
  );
}

export default App;
