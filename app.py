from fastapi import FastAPI
import joblib
import numpy as np
from pydantic import BaseModel

app = FastAPI()

# load model
model = joblib.load("diabetes_model.pkl")

class PatientData(BaseModel):
    features: list[float]

@app.post("/predict/diabetes")
def predict_diabetes(data: PatientData):
    X = np.array(data.features).reshape(1, -1)
    prediction = model.predict(X)[0]
    return {"prediction": int(prediction)}
