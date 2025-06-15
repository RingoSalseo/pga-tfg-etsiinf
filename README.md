Este repositorio está organizado en varias partes clave que cubren desde la adquisición de datos abiertos, su almacenamiento, la exposición de un servicio REST, hasta el consumo de estos datos para realizar predicciones con un modelo LSTM en Python.
AEMET-INE/
Proyecto en Java encargado de la adquisición y procesamiento de datos abiertos de fuentes como AEMET e INE. Incluye técnicas variadas para obtener los datos y su inserción en la base de datos.

bbdd/
Contiene un script principal, el diagrama E/R y el dump de la bbdd con los datos cargados.

servicio/
Servicio REST desarrollado con Spring que expone una API con 3 endpoints principales para acceder a los datos almacenados en la base de datos. Este servicio es consumido por el cliente Python.

Cliente Python/
Cliente en Python que se conecta al servicio REST para obtener datos, ejecuta un modelo LSTM que realiza predicciones de ventas y obtiene la gráfica de predicción . Ademas contiene scripts auxiliares
y todas las fotos de entrenamientos de los diferentes modelos

Diagrama arquitectura/
Diagrama de arquitectura.

Diagramas uml/
Son los diagramas UML correspondientes al proyecto AEMET-INE y al servicio
