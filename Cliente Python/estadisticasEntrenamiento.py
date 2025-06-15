import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

# Configurar estilo de gráficos
sns.set(style="whitegrid")

# Cargar datos
ruta_excel = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/Python LSTM/Fotos/Comunidad Diario/Datos Entrenamiento.xlsx"

data = pd.read_excel(ruta_excel)
data = data.dropna(subset=["MAE GRAFICA"])
variables = ["MAE GRAFICA", "NEURONAS", "BATCH", "DROPOUT", "EPOCAS", "VENTANA"]

output_dir = os.path.join(os.path.dirname(ruta_excel), "ANALISIS CORRELACIONES")
os.makedirs(output_dir, exist_ok=True)


# Tendencia con MAE
for var in variables[1:]:
    plt.figure(figsize=(6, 4))
    sns.scatterplot(data=data, x=var, y="MAE GRAFICA", alpha=0.6)
    sns.regplot(data=data, x=var, y="MAE GRAFICA", scatter=False, color='red', label="Tendencia")
    plt.title(f"Relación entre {var} y MAE GRAFICA")
    plt.tight_layout()
    plt.savefig(os.path.join(output_dir, f"Relacion {var} vs MAE GRAFICA.png"))
    plt.close()


#Correlación de los hiperparametros con el MAE

mae_corr = data[variables].corr()["MAE GRAFICA"].drop("MAE GRAFICA").sort_values()
colors = ["green" if val < 0 else "red" for val in mae_corr.values]

plt.figure(figsize=(7, 4))
sns.barplot(x=mae_corr.values, y=mae_corr.index, palette=colors)
plt.axvline(0, color="black", linestyle="--", linewidth=1)
plt.title("Correlación de hiperparámetros con MAE")
plt.xlabel("Correlación")
plt.ylabel("Hiperparámetro")
plt.tight_layout()
plt.savefig(os.path.join(output_dir, "Correlacion hiperparametros con MAE.png"))
plt.close()
