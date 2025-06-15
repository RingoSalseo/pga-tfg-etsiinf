import matplotlib.pyplot as plt
import pandas as pd
from sklearn.metrics import mean_absolute_error


path_data_modelo_con_variables = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/Python LSTM/Fotos/Comunidad Diario/MODELOS NOTABLES/8 (MEJOR)/"
path_data_modelo_sin_variables = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/Python LSTM/Fotos/Comunidad Diario/MODELOS NOTABLES/8 (MEJOR)/MISMO MODELO PERO SOLO VENTAS/"

data_modelo_con_variables = pd.read_excel(f"{path_data_modelo_con_variables}resultados.xlsx")
data_modelo_sin_variables = pd.read_excel(f"{path_data_modelo_sin_variables}resultados.xlsx")

###
data_modelo_con_variables.drop(columns=["Unnamed: 0"], inplace=True)
data_modelo_sin_variables.drop(columns=["Unnamed: 0"], inplace=True)

resultados = pd.merge(data_modelo_con_variables, data_modelo_sin_variables, left_index=True, right_index=True, suffixes=("_con_variables", "_sin_variables"))
resultados.drop(columns=["Real_sin_variables"],inplace=True)
resultados = resultados.rename(columns={"Real_con_variables" : "Real"})

plt.figure(figsize=(12, 6))
plt.plot(resultados["Real"], label="Real", color="blue", linewidth=0.5)
plt.plot(resultados["Predicción_con_variables"], label="Pred. Modelo con variables externas", color="green")
plt.plot(resultados["Predicción_sin_variables"], label="Pred. Solo ventas",  color="red") #linestyle=":",

plt.xlabel("Índice")
plt.ylabel("Ventas Totales")
plt.title("Comparación de Ventas Reales vs Predicciones de Ambos Modelos")
plt.legend()
plt.grid(alpha=0.3)

# Métricas de evaluación
plt.text(0.01, 0.95, f"MAE Modelo 1: 915.45\nMAE Modelo 2: 991.48", 
        transform=plt.gca().transAxes, fontsize=10, color="gray", ha="left", va="top")

plt.show()


"""
plt.figure(figsize=(12, 6))

# Curvas
plt.plot(resultados["Real"], label="Real", color="blue", linewidth=0.5)
plt.plot(resultados["Predicción_con_variables"], label="Pred. Modelo con variables externas", color="green")
plt.plot(resultados["Predicción_sin_variables"], label="Pred. Solo ventas", color="red")

# Área entre la curva roja y la verde
plt.fill_between(
    resultados.index,
    resultados["Predicción_con_variables"],
    resultados["Predicción_sin_variables"],
    where=(resultados["Predicción_con_variables"] > resultados["Predicción_sin_variables"]),
    interpolate=True,
    color="orange",
    alpha=0.3,
    label="Área entre modelos"
)

plt.fill_between(
    resultados.index,
    resultados["Predicción_sin_variables"],
    resultados["Predicción_con_variables"],
    where=(resultados["Predicción_sin_variables"] > resultados["Predicción_con_variables"]),
    interpolate=True,
    color="purple",
    alpha=0.3,
    label="Área entre modelos"
)

plt.xlabel("Índice")
plt.ylabel("Ventas Totales")
plt.title("Comparación de Ventas Reales vs Predicciones de Ambos Modelos")
plt.legend()
plt.grid(alpha=0.3)

plt.text(0.01, 0.95, f"MAE Modelo 1: 915.45\nMAE Modelo 2: 1021.38", 
         transform=plt.gca().transAxes, fontsize=10, color="gray", ha="left", va="top")

plt.show()
"""