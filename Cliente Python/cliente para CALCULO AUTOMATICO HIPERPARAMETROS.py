import requests
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error,mean_absolute_error

entrypoint = "http://localhost:8080/api/"
endpoint_codigopostal = "demografico/codigopostal"
endpoint_coordendas = "demografico/coordenadas"

def obtainDataFromPostalCode(codigo_postal = None):
    if codigo_postal is None:
        codigo_postal = 28290
    params = {"codigo_postal" : codigo_postal}
    call = requests.get(entrypoint + endpoint_codigopostal, params=params)
    if(call.status_code == 400):
        return None, None
    respuesta = call.json()
    df_mediciones = pd.DataFrame(respuesta["medicionesMediaEstacionesCercanas"])
    df_mediaSecciones = pd.DataFrame(respuesta["mediaSecciones"], index = [0])
    return df_mediciones,df_mediaSecciones


def obtainDataFromCoordinates(latitude = None, longitude = None):
    params = {"latitud" : latitude,
              "longitud" : longitude}
    call = requests.get(entrypoint + endpoint_coordendas, params=params)
    if(call.status_code == 400):
        return None, None
    respuesta = call.json()
    df_mediciones = pd.DataFrame(respuesta["medicionesMediaEstacionesCercanas"])
    df_mediaSecciones = pd.DataFrame(respuesta["mediaSecciones"], index = [0])
    return df_mediciones,df_mediaSecciones

codigo_postal_requerido = 28100
coordenadas_requeridas = ["42","-3"] #latitud y longitud

df_mediciones,df_mediaSecciones = obtainDataFromPostalCode(codigo_postal_requerido)
df_mediciones2,df_mediaSecciones2 = obtainDataFromCoordinates(coordenadas_requeridas[0],coordenadas_requeridas[1])
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
df_mediciones['semana'] = df_mediciones['fecha'].dt.to_period('W').apply(lambda r: r.start_time)

#En caso de querer utilizar los datos divididos en quincenas
#df_mediciones['semana'] = df_mediciones['fecha'].apply(lambda x: pd.to_datetime(x.replace(day=1)) if x.day <= 15 else pd.to_datetime(x.replace(day=16)))

df_mediciones = df_mediciones.groupby('semana').mean(numeric_only=True).reset_index()



df_mediaSecciones_replicated = pd.concat([df_mediaSecciones] * len(df_mediciones), ignore_index=True)
df_mediaSecciones_replicated['semana'] = df_mediciones['semana']
df_datos_cp = pd.merge(df_mediciones, df_mediaSecciones_replicated, on='semana', how='inner')
df_datos_cp = df_datos_cp.drop(columns = ["idSeccion","idDistrito","nombreSeccion","codigoPostal","latitud_centroide_seccion","longitud_centroide_seccion"])

df_datos_cp = df_datos_cp.rename(columns = {"semana" : "fecha"})

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
df["fecha"] = pd.to_datetime(df["date_add"])
#Agrupamos por semana
df["semana"] = df["fecha"].dt.to_period('W').apply(lambda r: r.start_time)

#En caso de querer agrupar por quincenas
#df['semana'] = df['fecha'].apply(lambda x: pd.to_datetime(x.replace(day=1)) if x.day <= 15 else pd.to_datetime(x.replace(day=16)))

#Agrupamos por codigo postal y por semana para obtener el numero total de ventas
df_ventas_agrupadas = df.groupby(["postcode","semana"]).agg({"total_paid": "sum"}).reset_index()
df_ventas_agrupadas.rename(columns={'total_paid': 'ventas_totales', "semana" : "fecha"}, inplace=True)
df_ventas_agrupadas["fecha"] = pd.to_datetime(df_ventas_agrupadas["fecha"])
# Generar un índice de fechas completo para todos los códigos postales
# Obtener el rango de fechas y completar fechas faltantes
all_postcodes = df_ventas_agrupadas['postcode'].unique()

#Para crear todas las fechas para la semana especificamos la frecuencia 'W-MON' en vez de 'D'
all_dates = pd.date_range(start=df_ventas_agrupadas['fecha'].min(),end=df_ventas_agrupadas['fecha'].max(), freq='W-MON')

#####################################
# En caso de querer hacer quincenas #
#####################################
"""all_dates = pd.date_range(start=df_ventas_agrupadas['fecha'].min(),
                        end=df_ventas_agrupadas['fecha'].max(), freq='D')
all_dates = all_dates.to_series().apply(lambda x: x.replace(day=1) if x.day <= 15 else x.replace(day=16))
all_dates = all_dates.drop_duplicates().reset_index(drop=True)"""


####################################################################################################
#                                   CREACION DATAFRAMES PARA LSTM                                  #
#                                   INTERPOLACION, MEDIANA Y CERO                                  #
####################################################################################################

""" DATAFRAMES CON INTERPOLACION Y MEDIANA

Tenemos el siguiente problema. En la mayoria de codigos postales, no habra ventas para determinados meses,
lo que en definitiva afectara negativamente a la prediccion de nuestra red neuronal

"""

# Crear un DataFrame con todas las combinaciones posibles de fechas y códigos postales
full_index = pd.MultiIndex.from_product([all_postcodes, all_dates], names=['postcode', 'fecha'])
df_completo = pd.DataFrame(index=full_index).reset_index()


df_completo = df_completo.merge(df_ventas_agrupadas, on=['postcode', 'fecha'], how='left')

# Método 1 - Mediana
df_mediana = df_completo.copy()
df_mediana['ventas_totales'] = (
    df_mediana.groupby('postcode')['ventas_totales']
    .apply(lambda x: x.fillna(x.median()))
    .reset_index(level=0, drop=True)
)

# Método 2 - Rellenar con 0
df_cero = df_completo.copy()
df_cero['ventas_totales'] = df_cero['ventas_totales'].fillna(0)


#############################################################################################
#                                   PREPARACION PARA LSTM                                   #
#############################################################################################
#A continuacion unimos los datos del codigo postal del servidor junto con los datos de ventas

df_mediana_cp = df_mediana[df_mediana["postcode"] == codigo_postal_requerido].reset_index(drop=True)
df_cero_cp = df_cero[df_cero["postcode"] == codigo_postal_requerido].reset_index(drop=True)

df_mediana_cp = pd.merge(df_datos_cp, df_mediana_cp, left_on = "fecha", right_on = "fecha", how = "inner")
df_mediana_cp = df_mediana_cp.drop(columns = ["fecha","postcode"])

df_cero_cp = pd.merge(df_datos_cp, df_cero_cp, left_on = "fecha", right_on = "fecha", how = "inner")
df_cero_cp = df_cero_cp.drop(columns = ["fecha","postcode"])

df_cero_cp = df_cero_cp.loc[:, df_cero_cp.nunique() > 1]
data = df_cero_cp

hyperparameter_search = True # <<<<<<<< CAMBIA A True PARA BUSQUEDA

# ===========================
# Función auxiliar
# ===========================
def create_sequences_multivariable(data, target_index, window_size):
    X, y = [], []
    for i in range(len(data) - window_size):
        X.append(data[i:i + window_size])
        y.append(data[i + window_size, target_index])  # Target: columna de ventas_totales
    return np.array(X), np.array(y)

# ===========================
# Hiperparámetros por defecto
# ===========================
default_window_size = 10
default_dropout = 0.2
default_epochs = 350
default_batch_size = 32
default_neurons = 64

# ===========================
# Escalar los datos
# ===========================
split = int(0.8 * len(data))  # 80% entrenamiento, 20% prueba

train_data = data[:split]
test_data = data[split:]

scaler = MinMaxScaler(feature_range=(0, 1))

# Ajustar y transformar los datos de entrenamiento
scaled_train_data = scaler.fit_transform(train_data)

# Transformar los datos de prueba (sin ajustar para evitar fuga de información)
scaled_test_data = scaler.transform(test_data)

# Identificar la posición de la columna 'ventas_totales'
ventas_column_index = train_data.columns.get_loc('ventas_totales')

# ===========================
# Búsqueda o entrenamiento simple
# ===========================
if hyperparameter_search:

    # Puedes definir los rangos de parámetros que quieras probar
    window_sizes = [10] #[8, 9, 10, 11, 12]
    dropouts = [0.1, 0.2, 0.3] #[0.1,0.2, 0.3, 0.5]
    epocas_list = [200, 300] #[100, 200, 300]
    batch_sizes = [32, 48, 64, 72]
    neurons = [32, 64, 128] #Iterable para el nº de neuronas
    resultados_gridsearch = []

    for neuron in neurons:
        for window_size in window_sizes:
            for dropout in dropouts:
                for epocas in epocas_list:
                    for batch_size in batch_sizes:
                        print(f"\nEntrenando con: Neuronas {neuron} | Window {window_size} | Dropout {dropout} | Épocas {epocas} | Batch {batch_size}")

                        # Crear secuencias
                        X_train, y_train = create_sequences_multivariable(scaled_train_data, ventas_column_index, window_size)
                        X_test, y_test = create_sequences_multivariable(scaled_test_data, ventas_column_index, window_size)

                        # Definir el modelo
                        model = Sequential([
                            LSTM(neuron, return_sequences=False, input_shape=(X_train.shape[1], X_train.shape[2])),
                            Dropout(dropout),
                            Dense(1)
                        ])

                        model.compile(optimizer='adam', loss='mean_squared_error')

                        # Entrenar
                        history = model.fit(X_train, y_train, epochs=epocas, batch_size=batch_size, verbose=0, validation_data=(X_test, y_test))

                        # Predicciones
                        predicted = model.predict(X_test)

                        # Invertir el escalado
                        predicted_ventas = scaler.inverse_transform(
                            np.hstack([np.zeros((predicted.shape[0], scaled_test_data.shape[1] - 1)), predicted])
                        )[:, ventas_column_index]

                        real_ventas = scaler.inverse_transform(
                            np.hstack([np.zeros((y_test.shape[0], scaled_test_data.shape[1] - 1)), y_test.reshape(-1, 1)])
                        )[:, ventas_column_index]

                        mse = mean_squared_error(real_ventas, predicted_ventas)
                        mae = mean_absolute_error(real_ventas, predicted_ventas)

                        resultados_gridsearch.append({
                            'window_size': window_size,
                            'dropout': dropout,
                            'epocas': epocas,
                            'batch_size': batch_size,
                            'neuron' : neuron,
                            'mse': mse,
                            'mae': mae
                        })

    # Mostrar resultados ordenados por MSE
    resultados_gridsearch = sorted(resultados_gridsearch, key=lambda x: x['mse'])
    print("\nTop combinaciones:")
    for resultado in resultados_gridsearch[:8]:
        print(resultado)

else:
    # Entrenamiento simple

    window_size = default_window_size
    dropout = default_dropout
    epocas = default_epochs
    batch_size = default_batch_size
    neurons = default_neurons

    # Crear secuencias
    X_train, y_train = create_sequences_multivariable(scaled_train_data, ventas_column_index, window_size)
    X_test, y_test = create_sequences_multivariable(scaled_test_data, ventas_column_index, window_size)

    # Definir el modelo
    """model = Sequential([
        LSTM(128, return_sequences=True, input_shape=(X_train.shape[1], X_train.shape[2])),
        Dropout(dropout),
        LSTM(64, return_sequences=False),
        Dropout(dropout),
        Dense(32, activation='relu'),
        Dense(1)
    ])"""
    model = Sequential([
        LSTM(neurons, return_sequences=False, input_shape=(X_train.shape[1], X_train.shape[2])),
        Dropout(dropout),
        Dense(1)  # Salida final: ventas_totales
    ])

    model.compile(optimizer='adam', loss='mean_squared_error')

    # Entrenar
    history = model.fit(X_train, y_train, epochs=epocas, batch_size=batch_size, validation_data=(X_test, y_test))

    # Evaluar
    loss = model.evaluate(X_test, y_test)
    print(f"Loss en el conjunto de prueba: {loss}")

    # Predicciones
    predicted = model.predict(X_test)

    # Invertir el escalado
    predicted_ventas = scaler.inverse_transform(
        np.hstack([np.zeros((predicted.shape[0], scaled_test_data.shape[1] - 1)), predicted])
    )[:, ventas_column_index]

    real_ventas = scaler.inverse_transform(
        np.hstack([np.zeros((y_test.shape[0], scaled_test_data.shape[1] - 1)), y_test.reshape(-1, 1)])
    )[:, ventas_column_index]

    resultados = pd.DataFrame({
        'Real': real_ventas,
        'Predicción': predicted_ventas
    })

    mse = mean_squared_error(real_ventas, predicted_ventas)
    mae = mean_absolute_error(real_ventas, predicted_ventas)
    print(resultados.head())

    # Visualización
    plt.figure(figsize=(12, 6))
    plt.plot(resultados['Real'], label='Real')
    plt.plot(resultados['Predicción'], label='Predicción')
    plt.xlabel('Índice')
    plt.ylabel('Ventas Totales')
    plt.legend()
    plt.text(0.5, 0.95, f'MSE: {mse:.2f}', ha='center', va='center', transform=plt.gca().transAxes, fontsize=12, color='red')
    plt.text(0.5, 0.90, f'MAE: {mae:.2f}', ha='center', va='center', transform=plt.gca().transAxes, fontsize=12, color='red')
    plt.title('Comparación entre Ventas Reales y Predichas')
    plt.show()

    plt.figure(figsize=(12, 6))
    plt.plot(history.history['loss'], label='Pérdida Entrenamiento')
    plt.plot(history.history['val_loss'], label='Pérdida Validación')
    plt.xlabel('Épocas')
    plt.ylabel('Pérdida')
    plt.legend()
    plt.title('Pérdida de Entrenamiento y Validación')
    plt.show()

pass