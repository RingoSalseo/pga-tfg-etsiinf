import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error, mean_absolute_error
from keras.models import Sequential
from keras.layers import LSTM, Dropout, Dense

# Leer Excel
df = pd.read_excel("C:/Users/Pablo Guerrero/Downloads/datos_de_venta_diariosMadrid.xlsx")
ventas = df[["ventas_totales"]].values

# Dividir en train/test antes de escalar
split_index = int(0.7 * len(ventas))
ventas_train = ventas[:split_index]
ventas_test = ventas[split_index:]

# Escalar solo con los datos de entrenamiento
scaler = MinMaxScaler()
ventas_train_scaled = scaler.fit_transform(ventas_train)
ventas_test_scaled = scaler.transform(ventas_test)

# Crear secuencias
def create_sequences(data, window_size):
    X, y = [], []
    for i in range(window_size, len(data)):
        X.append(data[i - window_size:i])
        y.append(data[i])
    return np.array(X), np.array(y)

window_size = 30
X_train, y_train = create_sequences(ventas_train_scaled, window_size)
X_test, y_test = create_sequences(ventas_test_scaled, window_size)

# Ajustar formato para LSTM
X_train = X_train.reshape((X_train.shape[0], X_train.shape[1], 1))
X_test = X_test.reshape((X_test.shape[0], X_test.shape[1], 1))

# Modelo LSTM
model = Sequential([
    LSTM(64, return_sequences=False, input_shape=(X_train.shape[1], X_train.shape[2])),
    Dense(32, activation='relu'),
    Dense(1)
])
model.compile(optimizer='adam', loss='mean_squared_error')      
history = model.fit(X_train, y_train, epochs=250, batch_size=183, validation_data=(X_test, y_test))

# Predicción
predicted = model.predict(X_test)
predicted_ventas = scaler.inverse_transform(predicted)[:, 0]
real_ventas = scaler.inverse_transform(y_test)[:, 0]

# Resultados
resultados = pd.DataFrame({"Real": real_ventas, "Predicción": predicted_ventas})
mse = mean_squared_error(real_ventas, predicted_ventas)
mae = mean_absolute_error(real_ventas, predicted_ventas)

# Gráficos
plt.figure(figsize=(12, 6))
plt.plot(resultados["Real"], label="Real")
plt.plot(resultados["Predicción"], label="Predicción")
plt.xlabel("Índice")
plt.ylabel("Ventas Totales")
plt.legend()
plt.text(0.5, 0.95, f"MSE: {mse:.2f}", ha="center", va="center", transform=plt.gca().transAxes, fontsize=12, color="red")
plt.text(0.5, 0.90, f"MAE: {mae:.2f}", ha="center", va="center", transform=plt.gca().transAxes, fontsize=12, color="red")
plt.title("Comparación entre Ventas Reales y Predichas")
plt.show()

plt.figure(figsize=(12, 6))
plt.plot(history.history["loss"], label="Pérdida Entrenamiento")
plt.plot(history.history["val_loss"], label="Pérdida Validación")
plt.xlabel("Épocas")
plt.ylabel("Pérdida")
plt.legend()
plt.title("Pérdida de Entrenamiento y Validación")
plt.show()

# Cálculo de la media móvil exponencial (EMA)
ema_real = pd.Series(real_ventas).ewm(span=10, adjust=False).mean()
ema_pred = pd.Series(predicted_ventas).ewm(span=10, adjust=False).mean()

# Calcular MAE entre ambas EMA
ema_mae = mean_absolute_error(ema_real, ema_pred)

# Gráfico comparativo de EMA
plt.figure(figsize=(12, 6))
plt.plot(ema_real, label="EMA Real", color="blue")
plt.plot(ema_pred, label="EMA Predicción", color="orange")
plt.xlabel("Índice")
plt.ylabel("Ventas Totales (EMA)")
plt.title("Comparación de EMA: Ventas Reales vs Predichas")
plt.legend()
plt.text(0.5, 0.9, f"MAE (EMA): {ema_mae:.2f}", ha="center", va="center", transform=plt.gca().transAxes, fontsize=12, color="green")
plt.grid(True)
plt.show()

pass
