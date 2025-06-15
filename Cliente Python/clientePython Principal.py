import requests
import pandas as pd
import numpy as np
import holidays
from sklearn.preprocessing import MinMaxScaler, StandardScaler
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error,mean_absolute_error,r2_score
import itertools
import seaborn as sns

entrypoint = "http://localhost:8080/api/"
endpoint_codigopostal = "demografico/codigopostal"
endpoint_coordenadas = "demografico/coordenadas"
endpoint_comunidad = "demografico/comunidad"

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
    call = requests.get(entrypoint + endpoint_coordenadas, params=params)
    if(call.status_code == 400):
        return None, None
    respuesta = call.json()
    df_mediciones = pd.DataFrame(respuesta["medicionesMediaEstacionesCercanas"])
    df_mediaSecciones = pd.DataFrame(respuesta["mediaSecciones"], index = [0])
    return df_mediciones,df_mediaSecciones

def obtainDataFromComunidad(id_comunidad): 
    params = {"id_comunidad": id_comunidad}
    call = requests.get(entrypoint + endpoint_comunidad, params=params)
    
    if call.status_code == 400:
        return None, None
    respuesta = call.json()
    df_comunidad = pd.DataFrame(respuesta["medicionesMediaEstacionesCercanas"])
    df_secciones_comunidad = pd.DataFrame(respuesta["mediaSecciones"], index = [0])
    return df_comunidad, df_secciones_comunidad


codigo_postal_requerido = 28100
coordenadas_requeridas = ["42","-3"] #latitud y longitud


"""
Necesario para obtener la lista de ids de provincia de una comunidad autónoma.
"""
id_comunidad = "13"
path_codigos_municipios_españa = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/AEMET/Códigos de municipio ESPAÑA.xlsx"
df_cod_mun_esp = pd.read_excel(path_codigos_municipios_españa)
df_cod_mun_esp.columns = df_cod_mun_esp.iloc[0]
df_cod_mun_esp = df_cod_mun_esp.drop(0).reset_index(drop=True)

list_provincias_comunidad = df_cod_mun_esp[df_cod_mun_esp["CODAUTO"] == id_comunidad]["CPRO"].unique().tolist()


#Todas las posibles llamadas al servicio. 3 tipos de endpoints
"""
df_mediciones_postal_code,df_mediaSecciones_postal_code = obtainDataFromPostalCode(codigo_postal_requerido)
df_mediciones_coordinates,df_mediaSecciones_coordinates = obtainDataFromCoordinates(coordenadas_requeridas[0],coordenadas_requeridas[1])
"""
df_mediciones_comunidad, df_mediaSecciones_comunidad = obtainDataFromComunidad(id_comunidad)


#nombre general de los datos. Esto nos permite modificar el origen de los datos 
#sin tener que cambiar el nombre de las variables
df_mediciones,df_mediaSecciones = df_mediciones_comunidad,df_mediaSecciones_comunidad

#Almacena el codigo de las secciones y distritos y los nombres de las secciones
secciones = df_mediaSecciones["idSeccion"].tolist()[0].split(" - ")[1:]
distrito = df_mediaSecciones["idDistrito"].tolist()[0].split(" - ")[1]
nombre_secciones = df_mediaSecciones["nombreSeccion"].tolist()[0].split(" - ")[1:]




################################################################
# TRATAMIENTO DE LOS DATOS OBTENIDOS EN LA LLAMADA AL SERVICIO #
################################################################

df_mediciones = df_mediciones.fillna(0.0)  # Para evitar los null con el nuevo servicio Spring JPA.

# Convertimos la fecha en datetime y añadimos una columna mes con el mes al que pertenece dicha fecha
df_mediciones["fecha"] = pd.to_datetime(df_mediciones["fecha"])

df_mediciones["fecha"] = df_mediciones["fecha"].dt.floor("D")
df_mediciones["fecha"] = df_mediciones["fecha"].dt.strftime('%Y-%m-%d')

df_mediciones = df_mediciones.groupby("fecha").mean(numeric_only=True).reset_index()

df_mediaSecciones_replicated = pd.concat([df_mediaSecciones] * len(df_mediciones), ignore_index=True)
df_mediaSecciones_replicated["fecha"] = df_mediciones["fecha"]

# Convertir 'fecha' en ambos DataFrames a datetime64[ns] antes de hacer el merge
df_mediciones["fecha"] = pd.to_datetime(df_mediciones["fecha"], errors='coerce').dt.strftime('%Y-%m-%d')
df_mediaSecciones_replicated["fecha"] = pd.to_datetime(df_mediaSecciones_replicated["fecha"], errors='coerce').dt.strftime('%Y-%m-%d')

df_datos_cp = pd.merge(df_mediciones, df_mediaSecciones_replicated, on="fecha", how="inner")
df_datos_cp = df_datos_cp.drop(columns=["idSeccion", "idDistrito", "nombreSeccion", "codigoPostal", "latitud_centroide_seccion", "longitud_centroide_seccion"])

df_datos_cp = df_datos_cp.rename(columns={"fecha": "fecha"})

###################################################################################################
#                                       LECTURA DATOS VENTA                                       #
###################################################################################################

path_datos_venta = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/Python LSTM/Datos de Ventas/ventas_date 1.xlsx"

df = pd.read_excel(path_datos_venta)

tipos_de_orden_aceptados = ["Servido", "Entregado", "Enviado", "Preparación en curso", "Enviado directo proveedor", "Enviado parcialmente",
                            "Pedido Recibido", "CETELEM - Crédito Preaprobado", "Pedido listo para recoger en tienda", "Pago recibido"]

# Es necesario puesto que el precio pagado está en eur * 100k
df = df[df["order_state"].isin(tipos_de_orden_aceptados)]  # Nos quedamos con los pedidos que contabilizan para el nº de ventas
df = df.drop_duplicates(subset="id_order", keep="first")
df["total_paid"] = df["total_paid"] / 1000000
df = df[["address1", "postcode", "city", "state", "id_order", "order_state", "total_paid", "date_add"]]
df["fecha"] = pd.to_datetime(df["date_add"])

# Agrupamos por semana
df["semana"] = df["fecha"].dt.to_period("W").apply(lambda r: r.start_time)

# Agrupamos por código postal y por semana para obtener el número total de ventas
df_ventas_agrupadas = df.groupby(["postcode", "fecha"]).agg({"total_paid": "sum"}).reset_index()
df_ventas_agrupadas.rename(columns={"total_paid": "ventas_totales"}, inplace=True)

df_ventas_agrupadas["fecha"] = pd.to_datetime(df_ventas_agrupadas["fecha"]).dt.strftime('%Y-%m-%d')

# Generar un índice de fechas completo para todos los códigos postales
# Obtener el rango de fechas y completar fechas faltantes
all_postcodes = df_ventas_agrupadas["postcode"].unique()

# Para crear todas las fechas para la semana especificamos la frecuencia "W-MON" en vez de "D"
all_dates = pd.date_range(start=df_ventas_agrupadas["fecha"].min(), end=df_ventas_agrupadas["fecha"].max(), freq="D")
all_dates = all_dates.strftime('%Y-%m-%d')

####################################################################################################
#                                   CREACION DATAFRAMES PARA LSTM                                  #
#                                   INTERPOLACION, MEDIANA Y CERO                                  #
####################################################################################################

# Crear un DataFrame con todas las combinaciones posibles de fechas y códigos postales
full_index = pd.MultiIndex.from_product([all_postcodes, all_dates], names=["postcode", "fecha"])
df_completo = pd.DataFrame(index=full_index).reset_index()

df_completo = df_completo.merge(df_ventas_agrupadas, on=["postcode", "fecha"], how="left")

# Método 1 - Mediana
df_mediana = df_completo.copy()
df_mediana["ventas_totales"] = (
    df_mediana.groupby("postcode")["ventas_totales"]
    .apply(lambda x: x.fillna(x.median()))
    .reset_index(level=0, drop=True)
)

# Método 2 - Rellenar con 0
df_cero = df_completo.copy()
df_cero["ventas_totales"] = df_cero["ventas_totales"].fillna(0)


#############################################################################################
#                                   PREPARACION PARA LSTM                                   #
#############################################################################################
# A continuación unimos los datos de la comunidad del servidor junto con los datos de ventas

df_mediana_cp = df_mediana[df_mediana["postcode"] == codigo_postal_requerido].reset_index(drop=True)

df_cero_cp = df_cero[df_cero["postcode"].astype(str).str.startswith(tuple(list_provincias_comunidad))]
df_cero_cp = df_cero_cp.groupby("fecha").agg({
    "ventas_totales": "sum",
    "postcode": "first"
}).reset_index()


df_mediana_cp = pd.merge(df_datos_cp, df_mediana_cp, left_on="fecha", right_on="fecha", how="inner")
df_mediana_cp = df_mediana_cp.drop(columns=["fecha", "postcode"])

df_cero_cp = pd.merge(df_datos_cp, df_cero_cp, left_on="fecha", right_on="fecha", how="inner")

"""Día de la semana (true-false) -> Se convierte en 7 variables true/false (dia_Lunes, dia_Martes...)
"""
# Eliminar las columnas 'fecha' y 'postcode' en df_cero_cp después del merge
df_cero_cp['fecha'] = pd.to_datetime(df_cero_cp['fecha'], errors='coerce')
df_cero_cp['dia_semana'] = df_cero_cp['fecha'].dt.day_name(locale='es_ES')
df_cero_cp = pd.get_dummies(df_cero_cp, columns=['dia_semana'], prefix='dia')

"""Estacion del año. -> Se convierte en 4 variables true/false  (est_Invierno, est_Primavera, est_Otoño, est_Invierno)
"""
def obtener_estacion(fecha):
    año = fecha.year
    primavera = (pd.Timestamp(f'{año}-03-21'), pd.Timestamp(f'{año}-06-20'))
    verano    = (pd.Timestamp(f'{año}-06-21'), pd.Timestamp(f'{año}-09-22'))
    otoño     = (pd.Timestamp(f'{año}-09-23'), pd.Timestamp(f'{año}-12-20'))
    # invierno: resto del año
    if primavera[0] <= fecha <= primavera[1]:
        return 'primavera'
    elif verano[0] <= fecha <= verano[1]:
        return 'verano'
    elif otoño[0] <= fecha <= otoño[1]:
        return 'otoño'
    else:
        return 'invierno'

df_cero_cp['estacion'] = df_cero_cp['fecha'].apply(obtener_estacion)
df_cero_cp = pd.get_dummies(df_cero_cp, columns=['estacion'], prefix='est')

"""Vacaciones. Se convierte en una variable true/false
"""

diccionario_idcomunidad_acronimo = {"1" : "AN","2" : "AR",
                                    "3" : "AS","4" : "IB",
                                    "5" : "CN","6" : "CB",
                                    "7" : "CL","8" : "CM",
                                    "9" : "CT","10" : "VC",
                                    "11" : "EX","12" : "GA",
                                    "13" : "MD","14" : "MC",
                                    "15" : "NC","16" : "PV",
                                    "17" : "RI","18" : "CE",
                                    "19" : "ML"}
#Obtener el acrónimo de la comunidad autónoma
acronimo_comunidad = diccionario_idcomunidad_acronimo[id_comunidad]

#Crear conjunto de festivos para los años 2022, 2023, 2024
festivos = holidays.ES(prov=acronimo_comunidad, years=[2022, 2023, 2024])

#Crear columna booleana 'vacaciones' (True si la fecha es festivo)
df_cero_cp['vacaciones'] = df_cero_cp['fecha'].isin(festivos)

df_cero_cp = df_cero_cp.drop(columns=["fecha", "postcode"])

#Eliminar columnas que no varian con el tiempo
df_cero_cp = df_cero_cp.loc[:, df_cero_cp.nunique() > 1]

correlation = df_cero_cp.corr()
ventas_corr = correlation["ventas_totales"].sort_values(ascending=False)
print(ventas_corr)
show_figure = False
if show_figure:
    sns.set(style="whitegrid")
    plt.figure(figsize=(10, 8))
    palette = ['orange' if i == 'ventas_totales' else 'steelblue' for i in ventas_corr.index]
    sns.barplot(x=ventas_corr.values, y=ventas_corr.index, palette=palette)
    plt.title('Correlación con ventas_totales')
    plt.xlabel('Coeficiente de correlación')
    plt.ylabel('Variables')
    plt.tight_layout()
    plt.show()

data = df_cero_cp

## En caso de querer limitar las variables con las que se entrenan a las "mejores"
#data = data[["ventas_totales","humedadMedia","est_otoño","humedadMinima","humedadMinima","temperaturaMinima","est_verano","dia_Domingo","temperaturaMedia","temperaturaMaxima","insolacion"]]

# Dividir en train y test
split = int(0.7 * len(data))
train_data = data[:split]
test_data = data[split:]

# Escalar TODAS las columnas (para alimentar al modelo LSTM)
scaler = MinMaxScaler()
scaled_train_data = scaler.fit_transform(train_data)
scaled_test_data = scaler.transform(test_data)

# Escalar SOLO la columna 'ventas_totales' con un scaler separado
ventas_column = "ventas_totales"
ventas_scaler = MinMaxScaler()

# Entrenar el scaler solo con la columna de ventas del set de entrenamiento
ventas_scaler.fit(train_data[[ventas_column]])

# Obtener la posición de la columna ventas_totales (útil si usas arrays)
ventas_column_index = train_data.columns.get_loc(ventas_column)

# Crear secuencias de tiempo
def create_sequences_multivariable(data, target_index, window_size):
    X, y = [], []
    for i in range(len(data) - window_size):
        X.append(data[i:i + window_size])
        y.append(data[i + window_size, target_index])
    return np.array(X), np.array(y)

hyperparameter_search = False #Para decidir si ejecutamos la búsqueda de hiperparametros iterativa (Grid Search manual) o no
#AQUI ES DONDE SE DECIDE SI HACEMOS LA BUSQUEDA O ENTRENAMOS UN MODELO EN CONCRETO
if not hyperparameter_search:
    # Hiperparámetros
    default_window_size = 30
    default_dropout = 0.3
    default_epocas = 250
    default_batch_size = 183
    default_neurons = 64
    X_train, y_train = create_sequences_multivariable(scaled_train_data, ventas_column_index, default_window_size)
    X_test, y_test = create_sequences_multivariable(scaled_test_data, ventas_column_index, default_window_size)

    # Definición del modelo LSTM mejorado
    model = Sequential([
        LSTM(default_neurons, return_sequences=False, input_shape=(X_train.shape[1], X_train.shape[2])),
        Dropout(default_dropout),
        Dense(1)
    ])

    # Compilar el modelo
    model.compile(optimizer="adam", loss="mean_squared_error") #mae

    # Entrenar el modelo
    history = model.fit(X_train, y_train, epochs=default_epocas, batch_size=default_batch_size, validation_data=(X_test, y_test))

    # Evaluar el modelo
    loss = model.evaluate(X_test, y_test)
    print(f"Loss en el conjunto de prueba: {loss}")

    # Predicciones
    predicted = model.predict(X_test)

    # Invertir el escalado
    predicted_ventas = ventas_scaler.inverse_transform(predicted)
    real_ventas = ventas_scaler.inverse_transform(y_test.reshape(-1, 1))
    """predicted_ventas = scaler.inverse_transform(
        np.hstack([np.zeros((predicted.shape[0], scaled_test_data.shape[1] - 1)), predicted])
    )[:, ventas_column_index]

    real_ventas = scaler.inverse_transform(
        np.hstack([np.zeros((y_test.shape[0], scaled_test_data.shape[1] - 1)), y_test.reshape(-1, 1)])
    )[:, ventas_column_index]"""

    resultados = pd.DataFrame({
        "Real": real_ventas.flatten(),
        "Predicción": predicted_ventas.flatten()
    })

    # Métricas de evaluación
    mse = mean_squared_error(real_ventas, predicted_ventas)
    mae = mean_absolute_error(real_ventas, predicted_ventas)
    r2 = r2_score(real_ventas, predicted_ventas)
    print(resultados.head())
    # Visualización
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


    # Calcular las medias móviles
    #
    ventana = 6
    media_movil_exponencial_real = resultados["Real"].ewm(span=ventana, adjust=False).mean()
    media_movil_exponencial_pred = resultados["Predicción"].ewm(span=ventana, adjust=False).mean()
    mae_medias_moviles = mean_absolute_error(media_movil_exponencial_real, media_movil_exponencial_pred)

    # Plot de las medias móviles
    plt.figure(figsize=(12, 6))
    plt.plot(media_movil_exponencial_real, label="Media Móvil Real", linestyle="--", color="blue")
    plt.plot(media_movil_exponencial_pred, label="Media Móvil Predicción", linestyle="--", color="orange")
    plt.xlabel("Índice")
    plt.ylabel("Ventas Totales (Media Móvil)")
    plt.title("Comparación de Tendencias: Media Móvil de Ventas Reales vs Predichas")
    plt.text(0.5, 0.9, f"MAE: {mae_medias_moviles:.2f}",transform=plt.gca().transAxes, fontsize=12, color="red", ha="center")
    plt.legend()
    plt.grid(True)
    plt.show()

else:
    window_sizes = [14,30]
    dropouts = [0.1,0.4,0.5]
    epocas_list = [100,180]
    batch_sizes = [7,14,30,64]
    neurons_list = [1024]

    combinaciones = list(itertools.product(window_sizes, dropouts, epocas_list, batch_sizes, neurons_list))
    resultados_gridsearch = []

    for window_size, dropout, epocas, batch_size, neuron in combinaciones:
        print(f"Entrenando el modelo con Ventana : {window_size} - Drop : {dropout} - Epocas : {epocas} - Batch : {batch_size} - Neuronas : {neuron}")
        X_train, y_train = create_sequences_multivariable(scaled_train_data, ventas_column_index, window_size)
        X_test, y_test = create_sequences_multivariable(scaled_test_data, ventas_column_index, window_size)

        model = Sequential([
            LSTM(neuron, return_sequences=False, input_shape=(X_train.shape[1], X_train.shape[2])),
            Dropout(dropout),
            Dense(1)
        ])

        model.compile(optimizer="adam", loss="mean_squared_error")
        history = model.fit(X_train, y_train, epochs=epocas, batch_size=batch_size, validation_data=(X_test, y_test), verbose=0)

        predicted = model.predict(X_test)
        predicted_ventas = ventas_scaler.inverse_transform(predicted)
        real_ventas = ventas_scaler.inverse_transform(y_test.reshape(-1, 1))

        mse = mean_squared_error(real_ventas, predicted_ventas)
        mae = mean_absolute_error(real_ventas, predicted_ventas)
        ventana = 6
        media_movil_exponencial_real = pd.Series(real_ventas.flatten()).ewm(span=ventana, adjust=False).mean()
        media_movil_exponencial_pred = pd.Series(predicted_ventas.flatten()).ewm(span=ventana, adjust=False).mean()
        mae_medias_moviles = mean_absolute_error(media_movil_exponencial_real, media_movil_exponencial_pred)

        # Extraer pérdidas
        train_loss = history.history['loss'][-1]
        val_loss = history.history['val_loss'][-1]
        diff_loss = val_loss - train_loss

        resultados_gridsearch.append({
            'neuron': neuron,
            'window_size': window_size,
            'epocas': epocas,
            'batch_size': batch_size,
            'dropout': dropout,
            'mse': mse,
            'mae': mae,
            'mae_medias_moviles' : mae_medias_moviles,
            'diff_loss': diff_loss
        })

    resultados_gridsearch = sorted(resultados_gridsearch, key=lambda x: x['mae'])
    print("\nTop combinaciones:")
    for resultado in resultados_gridsearch[:8]:
        print(resultado)
    df_resultado = pd.DataFrame(resultados_gridsearch)
    df_resultado.to_excel("C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/Python LSTM/Fotos/Comunidad Diario/temp.xlsx")

obtener_heat_map = False
if obtener_heat_map:

    data = pd.read_excel("C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/Python LSTM/Fotos/Comunidad Diario/Datos Entrenamiento.xlsx")
    data["MAE MEDIAS MOVILES"] = data["MAE MEDIAS MOVILES"].fillna(0)
    heatmap_data = data.groupby(['DROPOUT', 'BATCH'])['MAE GRAFICA'].mean().unstack()
    plt.figure(figsize=(10, 6))
    sns.heatmap(heatmap_data, annot=True, fmt=".1f", cmap="YlGnBu")
    plt.title('Heatmap de MAE según Dropout y Batch Size')
    plt.ylabel('Dropout')
    plt.xlabel('Batch Size')
    plt.tight_layout()
    plt.show()


pass