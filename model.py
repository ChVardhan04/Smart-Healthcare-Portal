import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.datasets import load_diabetes
import joblib

# For demo, we’ll just train a simple fake model (replace later with real dataset)
X, y = load_diabetes(return_X_y=True)
y = (y > y.mean()).astype(int)  # convert regression target to binary

model = LogisticRegression(max_iter=1000)
model.fit(X, y)

joblib.dump(model, "diabetes_model.pkl")
