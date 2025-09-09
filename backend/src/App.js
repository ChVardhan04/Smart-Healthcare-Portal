import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Register from "./components/Register";
import Appointment from "./components/Appointment";
import Prediction from "./components/Prediction";

function App() {
  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/appointment" element={<Appointment />} />
        <Route path="/predict" element={<Prediction />} />
        <Route path="/" element={<h2>Welcome to Smart Healthcare Portal</h2>} />
      </Routes>
    </Router>
  );
}

export default App;
