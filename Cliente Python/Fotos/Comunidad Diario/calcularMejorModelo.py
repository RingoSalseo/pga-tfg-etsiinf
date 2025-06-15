import pandas as pd
from sklearn.preprocessing import MinMaxScaler

# Cargar el Excel (reemplaza con la ruta real)
df = pd.read_excel("C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/Python LSTM/Fotos/Comunidad Diario/Datos Entrenamiento.xlsx")

# Columnas de métricas
metricas = ['MSE GRAFICA', 'MAE GRAFICA', 'MAE MEDIAS MOVILES', 'DIFERENCIA PÉRDIDA']

# Normalizar métricas a [0,1]
scaler = MinMaxScaler()
df_norm = pd.DataFrame(scaler.fit_transform(df[metricas]), columns=metricas)

# Calcular score ponderado (mayor peso a MAE y MEDIA MOVIL)
df['SCORE_PONDERADO'] = (
    0.15 * df_norm['MSE GRAFICA'] +
    0.55 * df_norm['MAE GRAFICA'] +
    0.1 * df_norm['MAE MEDIAS MOVILES'] +
    0.2 * df_norm['DIFERENCIA PÉRDIDA']
)

# Mostrar la mejor combinación
mejor = df.sort_values('SCORE_PONDERADO').head(1)


#MEJORES POR MEDIDA
mejores_mse = df.sort_values("MSE GRAFICA")
mejores_mae = df.sort_values("MSE GRAFICA")

print("Mejor combinación de hiperparámetros basada en score ponderado:")
print(mejor[['NEURONAS', 'VENTANA', 'EPOCAS', 'BATCH', 'DROPOUT'] + metricas + ['SCORE_PONDERADO']])
