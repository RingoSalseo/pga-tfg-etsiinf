import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import os

# Ruta de entrada y salida
ruta_datos = r"C:\Users\Pablo Guerrero\OneDrive - Universidad Politécnica de Madrid\Escritorio\TFG PABLO GUERRERO ALVAREZ\Python LSTM\Fotos\Comunidad Diario\Datos Entrenamiento.xlsx"
carpeta_base = r"C:\Users\Pablo Guerrero\OneDrive - Universidad Politécnica de Madrid\Escritorio\TFG PABLO GUERRERO ALVAREZ\Python LSTM\Fotos\Comunidad Diario\MAPAS DE CALOR"

# Cargar datos
data = pd.read_excel(ruta_datos)
data["MAE MEDIAS MOVILES"] = data["MAE MEDIAS MOVILES"].fillna(0)

# Todas las variables, pero VENTANA no se fija
todas = ["NEURONAS", "BATCH", "EPOCAS", "DROPOUT", "VENTANA"]
fijas = ["NEURONAS", "BATCH", "EPOCAS", "DROPOUT"]

# Generar mapas
for fija in fijas:
    carpeta_salida = os.path.join(carpeta_base, f"{fija} FIJO")
    os.makedirs(carpeta_salida, exist_ok=True)

    otras = [v for v in todas if v != fija]
    for y_var in otras:
        # Crear tabla pivote para el heatmap
        heatmap_data = data.groupby([y_var, fija])["MAE GRAFICA"].mean().unstack()
        if heatmap_data.empty or heatmap_data.isnull().all().all():
            continue

        # Graficar
        plt.figure(figsize=(10, 6))
        sns.heatmap(heatmap_data, annot=True, fmt=".1f", cmap="YlGnBu")
        plt.title(f"Mapa de calor MAE: {y_var} vs {fija}")
        plt.ylabel(y_var)
        plt.xlabel(fija)
        plt.tight_layout()

        # Guardar
        nombre_archivo = f"Mapa de calor {y_var} {fija}.png".replace(" ", "_")
        plt.savefig(os.path.join(carpeta_salida, nombre_archivo))
        plt.close()
