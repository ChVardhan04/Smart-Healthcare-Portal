import { useState } from "react";
import axios from "axios";

export default function Prediction() {
  const [features, setFeatures] = useState("");
  const [result, setResult] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const numbers = features.split(",").map(Number);
    try {
      const res = await axios.post("http://localhost:8080/api/predict/diabetes", { features: numbers });
      setResult(res.data.prediction);
    } catch (err) {
      alert("Error: " + err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ padding: "20px" }}>
      <h2>Disease Prediction</h2>
      <input
        value={features}
        onChange={(e) => setFeatures(e.target.value)}
        placeholder="Enter 10 numbers separated by commas"
      /> <br />
      <button type="submit">Predict</button>
      {result !== null && <h3>Prediction: {result === 1 ? "At Risk" : "Healthy"}</h3>}
    </form>
  );
}
