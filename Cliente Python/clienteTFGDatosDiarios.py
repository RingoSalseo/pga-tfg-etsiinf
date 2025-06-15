import requests
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout
import matplotlib.pyplot as plt

entrypoint = "http://localhost:8080/api/"
endpoint_codigopostal = "demografico/codigopostal"
endpoint_coordendas = "demografico/coordenadas"
def obtainDataFromPostalCode(codigo_postal = None):
    if codigo_postal is None:
        codigo_postal = 28290
    params = {"codigo_postal" : codigo_postal}
    respuesta = requests.get(entrypoint + endpoint_codigopostal, params=params).json()
    df_mediciones = pd.DataFrame(respuesta["medicionesMediaEstacionesCercanas"])
    df_mediaSecciones = pd.DataFrame(respuesta["mediaSecciones"], index = [0])
    return df_mediciones,df_mediaSecciones


def obtainDataFromCoordinates(latitude = None, longitude = None):
    params = {"latitud" : latitude,
              "longitud" : longitude}
    respuesta = requests.get(entrypoint + endpoint_coordendas, params=params).json()
    df_mediciones = pd.DataFrame(respuesta["medicionesMediaEstacionesCercanas"])
    df_mediaSecciones = pd.DataFrame(respuesta["mediaSecciones"], index = [0])
    return df_mediciones,df_mediaSecciones
    pass

codigo_postal_requerido = 43201
coordenadas_requeridas = [43.5,-3.2] #latitud y longitud

df_mediciones,df_mediaSecciones = obtainDataFromPostalCode(codigo_postal_requerido)

#######################################################################
# ALMACENAR NOMBRE DE LAS SECCIONES, DEL DISTRITO Y DEL CODIGO POSTAL #
#######################################################################
secciones = df_mediaSecciones["idSeccion"].tolist()[0].split(" - ")[1:]
distrito = df_mediaSecciones["idDistrito"].tolist()[0].split(" - ")[1]
nombre_secciones = df_mediaSecciones["nombreSeccion"].tolist()[0].split(" - ")[1:]


################################################################
# TRATAMIENTO DE LOS DATOS OBTENIDOS EN LA LLAMADA AL SERVICIO #
################################################################


#Convertimos la fecha en datetime y añadimos una columna mes con el mes al que pertenece dicha fecha
df_mediciones['fecha'] = pd.to_datetime(df_mediciones['fecha'])
df_mediciones["mes"] = df_mediciones["fecha"].dt.to_period("M").dt.to_timestamp()
df_mediciones_mensuales = df_mediciones.groupby("mes").mean().reset_index()

df_mediaSecciones_replicated = pd.concat([df_mediaSecciones] * len(df_mediciones_mensuales), ignore_index=True)
df_mediaSecciones_replicated['fecha'] = df_mediciones_mensuales['fecha']
df_datos_cp = pd.merge(df_mediciones_mensuales, df_mediaSecciones_replicated, on='fecha', how='inner')
df_datos_cp = df_datos_cp.drop(columns = ["idSeccion","idDistrito","nombreSeccion","codigoPostal","latitud_centroide_seccion","longitud_centroide_seccion"])
df_datos_cp["mes"] = df_datos_cp["mes"].dt.strftime('%Y-%m')
###################################################################################################
#                                       LECTURA DATOS VENTA                                       #
###################################################################################################

path_datos_venta = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/Python LSTM/Datos de Ventas/ventas_date 1.xlsx"

df = pd.read_excel(path_datos_venta)

"""#Evitamos tratar pedidos cuyo estado sea...#
Estos estados de pedido no significan una venta en el negocio, que es lo que nos interesa estudiar en este problema. Esto no quiere decir que por ejemplo,
el estado  "CETELEM - Solicitud Pendiente" o "CETELEM - CrÃ©dito denegado" o "En espera de pago por transferencia bancaria" no sean futuras ventas. 

"Reembolsado","CETELEM - CrÃ©dito denegado","Cancelado","Reembolso parcial",
"Ãšltima unidad vendida (pedido NO pagado)","CETELEM - Solicitud Pendiente",
"Sequra: Cancelado","En espera de pago por transferencia bancaria"

Estos casos representan 4345 filas de las 35565 filas totales del Excel de ventas. 27/12/2024

"""
tipos_de_orden_aceptados = ["Servido","Entregado","Enviado","PreparaciÃ³n en curso","Enviado directo proveedor","Enviado parcialmente",
                          "Pedido Recibido","CETELEM - CrÃ©dito Preaprobado","Pedido listo para recoger en tienda",
                          "Pago recibido"]

#Es necesario puesto que el precio pagado esta en eur * 100k
df = df[df["order_state"].isin(tipos_de_orden_aceptados)] #Nos quedamos con los pedidos que contabilizan para el nº de ventas
df = df.drop_duplicates(subset = "id_order", keep = "first")
df["total_paid"] = df["total_paid"] / 1000000
df = df[["address1","postcode","city","state","id_order","order_state","total_paid","date_add"]]
df["mes_año"] = df["date_add"].dt.strftime('%Y-%m')

#Agrupamos por codigo postal y por mes para obtener el numero total de ventas
df_ventas_agrupadas = df.groupby(['postcode', 'mes_año']).agg({'total_paid': 'sum'}).reset_index()
df_ventas_agrupadas.rename(columns={'total_paid': 'ventas_totales'}, inplace=True)

# Generar un índice de fechas completo para todos los códigos postales
# Obtener el rango de fechas y completar fechas faltantes
df_ventas_agrupadas['mes_año'] = pd.to_datetime(df_ventas_agrupadas['mes_año'])
all_postcodes = df_ventas_agrupadas['postcode'].unique()
all_dates = pd.date_range(start=df_ventas_agrupadas['mes_año'].min(),
                          end=df_ventas_agrupadas['mes_año'].max(), freq='MS')

####################################################################################################
#                                   CREACION DATAFRAMES PARA LSTM                                  #
#                                   INTERPOLACION, MEDIANA Y CERO                                  #
####################################################################################################

""" DATAFRAMES CON INTERPOLACION Y MEDIANA

Tenemos el siguiente problema. En la mayoria de codigos postales, no habra ventas para determinados meses,
lo que en definitiva afectara negativamente a la prediccion de nuestra red neuronal

"""

# Crear un DataFrame con todas las combinaciones posibles de fechas y códigos postales
full_index = pd.MultiIndex.from_product([all_postcodes, all_dates], names=['postcode', 'mes_año'])
df_completo = pd.DataFrame(index=full_index).reset_index()


df_completo = df_completo.merge(df_ventas_agrupadas, on=['postcode', 'mes_año'], how='left')

# Método 1 - Interpolación lineal
df_interpolado = df_completo.copy()
df_interpolado['ventas_totales'] = (
    df_interpolado.groupby('postcode')['ventas_totales']
    .apply(lambda x: x.interpolate(method='linear'))
    .reset_index(level=0, drop=True)
)
df_interpolado["mes_año"] = df_interpolado["mes_año"].dt.strftime('%Y-%m')

# Método 2 - Mediana
df_mediana = df_completo.copy()
df_mediana['ventas_totales'] = (
    df_mediana.groupby('postcode')['ventas_totales']
    .apply(lambda x: x.fillna(x.median()))
    .reset_index(level=0, drop=True)
)
df_mediana["mes_año"] = df_mediana["mes_año"].dt.strftime('%Y-%m')

# Método 3 - Rellenar con 0
df_cero = df_completo.copy()
df_cero['ventas_totales'] = df_cero['ventas_totales'].fillna(0)
df_cero["mes_año"] = df_cero["mes_año"].dt.strftime('%Y-%m')


#############################################################################################
#                                   PREPARACION PARA LSTM                                   #
#############################################################################################
#A continuacion unimos los datos del codigo postal del servidor junto con los datos de ventas

df_interpolado_cp = df_interpolado[df_interpolado["postcode"] == codigo_postal_requerido].reset_index(drop=True)
df_mediana_cp = df_mediana[df_mediana["postcode"] == codigo_postal_requerido].reset_index(drop=True)
df_cero_cp = df_cero[df_cero["postcode"] == codigo_postal_requerido].reset_index(drop=True)

df_interpolado_cp = pd.merge(df_datos_cp, df_interpolado_cp, left_on = "mes", right_on = "mes_año", how = "inner")
df_interpolado_cp = df_interpolado_cp.drop(columns = ["fecha","postcode","mes_año"])

df_mediana_cp = pd.merge(df_datos_cp, df_mediana_cp, left_on = "mes", right_on = "mes_año", how = "inner")
df_mediana_cp = df_mediana_cp.drop(columns = ["fecha","postcode","mes_año"])

df_cero_cp = pd.merge(df_datos_cp, df_cero_cp, left_on = "mes", right_on = "mes_año", how = "inner")
df_cero_cp = df_cero_cp.drop(columns = ["fecha","postcode","mes_año"])


data = df_cero_cp.drop(columns=["mes"])

# Escalar todos los datos entre 0 y 1
scaler = MinMaxScaler(feature_range=(0, 1))
scaled_data = scaler.fit_transform(data)

# Crear secuencias de tiempo para todas las columnas
def create_sequences_multivariable(data, window_size):
    X, y = [], []
    for i in range(len(data) - window_size):
        X.append(data[i:i + window_size])
        y.append(data[i + window_size, -1])  # Target: ventas_totales (última columna)
    return np.array(X), np.array(y)

# Ventana de tiempo (ejemplo: 6 meses)
window_size = 3
X, y = create_sequences_multivariable(scaled_data, window_size)

# Dividir en conjuntos de entrenamiento y prueba
split = int(0.8 * len(X))  # 80% entrenamiento, 20% prueba
X_train, X_test = X[:split], X[split:]
y_train, y_test = y[:split], y[split:]

# Paso 2: Definir el modelo LSTM
model = Sequential([
    LSTM(128, return_sequences=True, input_shape=(X_train.shape[1], X_train.shape[2])),
    Dropout(0.3),
    LSTM(64, return_sequences=False),
    Dropout(0.3),
    Dense(32, activation='relu'),
    Dense(1)  # Salida final: ventas_totales
])

# Compilar el modelo
model.compile(optimizer='adam', loss='mean_squared_error')

# Paso 3: Entrenar el modelo
model.fit(X_train, y_train, epochs=50, batch_size=32, validation_data=(X_test, y_test))

# Paso 4: Evaluar el modelo
loss = model.evaluate(X_test, y_test)
print(f"Loss en el conjunto de prueba: {loss}")

# Paso 5: Predicciones
predicted = model.predict(X_test)
predicted = scaler.inverse_transform(np.hstack((np.zeros((len(predicted), scaled_data.shape[1] - 1)), predicted.reshape(-1, 1))))[:, -1]

# Ver resultados
resultados = pd.DataFrame({
    'Real': scaler.inverse_transform(np.hstack((np.zeros((len(y_test), scaled_data.shape[1] - 1)), y_test.reshape(-1, 1))))[:, -1],
    'Predicción': predicted
})
print(resultados.head())
pass