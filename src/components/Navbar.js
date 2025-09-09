import React from "react";
import { Link } from "react-router-dom";

function Navbar({ user, setUser }) {
  return (
    <nav
      style={{
        background: "#1976d2",
        padding: "15px",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        color: "white",
      }}
    >
      <div>
        <Link to="/" style={{ color: "white", textDecoration: "none", marginRight: "20px" }}>
          Smart Healthcare
        </Link>
        {user && (
          <>
            <Link to="/appointment" style={{ color: "white", marginRight: "20px" }}>
              Book Appointment
            </Link>
            <Link to="/predict" style={{ color: "white" }}>
              Disease Prediction
            </Link>
          </>
        )}
      </div>
      <div>
        {user ? (
          <button
            onClick={() => setUser(null)}
            style={{
              background: "red",
              border: "none",
              color: "white",
              padding: "6px 12px",
              borderRadius: "5px",
              cursor: "pointer",
            }}
          >
            Logout
          </button>
        ) : (
          <span>Welcome Guest</span>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
