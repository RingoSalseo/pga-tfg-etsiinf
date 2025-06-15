CREATE DATABASE BBDD_TFG;

-- Tabla Municipios
CREATE TABLE Municipios (
    id_municipio VARCHAR(10) PRIMARY KEY,
    nombre_municipio VARCHAR(100),
    renta_neta_media_persona INT,
    renta_neta_media_hogar INT,
    renta_unidad_consumo INT,
    mediana_renta_consumo INT,
    renta_bruta_media_persona INT,
    renta_bruta_media_hogar INT,
    edad_media_poblacion DECIMAL(12,2),
    porcentaje_menor_18 DECIMAL(12,2),
    porcentaje_mayor_65 DECIMAL(12,2),
    tamaño_medio_hogar DECIMAL(12,2),
    porcentaje_hogares_unipersonales DECIMAL(12,2),
	poblacion INT,
    porcentaje_poblacion_española DECIMAL(12,2),
    fuente_ingresos_salario INT,
    fuente_ingresos_pensiones INT,
    fuente_ingresos_pdesempleado INT,
    fuente_ingresos_otrprestaciones INT,
    fuente_ingresos_otringresos INT
);

-- Tabla Distritos
CREATE TABLE Distritos (
    id_distrito VARCHAR(12) PRIMARY KEY,
    id_municipio VARCHAR(10),
    nombre_distrito VARCHAR(100),
    renta_neta_media_persona INT,
    renta_neta_media_hogar INT,
    renta_unidad_consumo INT,
    mediana_renta_consumo INT,
    renta_bruta_media_persona INT,
    renta_bruta_media_hogar INT,
    edad_media_poblacion DECIMAL(12,2),
    porcentaje_menor_18 DECIMAL(12,2),
    porcentaje_mayor_65 DECIMAL(12,2),
    tamaño_medio_hogar DECIMAL(12,2),
    porcentaje_hogares_unipersonales DECIMAL(12,2),
    poblacion INT,
    porcentaje_poblacion_española DECIMAL(12,2),
    fuente_ingresos_salario INT,
    fuente_ingresos_pensiones INT,
    fuente_ingresos_pdesempleado INT,
    fuente_ingresos_otrprestaciones INT,
    fuente_ingresos_otringresos INT,
    FOREIGN KEY (id_municipio) REFERENCES Municipios(id_municipio)
        ON DELETE CASCADE
);

-- Tabla Secciones
CREATE TABLE Secciones (
    id_seccion VARCHAR(15) PRIMARY KEY,
    id_distrito VARCHAR(12),
    nombre_seccion VARCHAR(100),
    codigo_postal VARCHAR(5),
    renta_neta_media_persona INT,
    renta_neta_media_hogar INT,
    renta_unidad_consumo INT,
    mediana_renta_consumo INT,
    renta_bruta_media_persona INT,
    renta_bruta_media_hogar INT,
    edad_media_poblacion DECIMAL(12,2),
    porcentaje_menor_18 DECIMAL(12,2),
    porcentaje_mayor_65 DECIMAL(12,2),
    tamaño_medio_hogar DECIMAL(12,2),
    porcentaje_hogares_unipersonales DECIMAL(12,2),
	poblacion INT,
    porcentaje_poblacion_española DECIMAL(12,2),
    fuente_ingresos_salario INT,
    fuente_ingresos_pensiones INT,
    fuente_ingresos_pdesempleado INT,
    fuente_ingresos_otrprestaciones INT,
    fuente_ingresos_otringresos INT,
    latitud_centroide_seccion DOUBLE NULL, #anadidas despues
    longitud_centroide_seccion DOUBLE NULL, # anadidas despues
    FOREIGN KEY (id_distrito) REFERENCES Distritos(id_distrito)
        ON DELETE CASCADE
);

#Dada la necesidad de consultar el centroide de cada seccion censal,
#en vez de abrir el shapefile cada vez que se haga una peticion al servidor,
#haremos una sencilla query a la bbdd 2912
ALTER TABLE Secciones
ADD COLUMN latitud_centroide_seccion DOUBLE NULL,
ADD COLUMN longitud_centroide_seccion DOUBLE NULL;


-- MEDICIONES ESTACIONES

CREATE TABLE Estaciones (
    id_estacion VARCHAR(10) PRIMARY KEY,
    nombre VARCHAR(100),
    provincia VARCHAR(50),
    altitud FLOAT,
    latitud FLOAT,
    longitud FLOAT
);

CREATE TABLE Mediciones (
    id_estacion VARCHAR(10) NOT NULL,
    fecha DATE NOT NULL,
    temperatura_media FLOAT,
    precipitacion FLOAT,
    temperatura_minima FLOAT,
    hora_temperatura_minima VARCHAR(10),
    temperatura_maxima FLOAT,
    hora_temperatura_maxima VARCHAR(10),
    direccion_racha_maxima FLOAT,
    velocidad_media_viento FLOAT,
    racha_maxima FLOAT,
    horaracha VARCHAR(10),
    insolacion FLOAT,
    presion_maxima FLOAT,
    hora_presion_maxima VARCHAR(10),
    presion_minima FLOAT,
    hora_presion_minima VARCHAR(10),
    humedad_media FLOAT,
    humedad_maxima FLOAT,
    hora_humedad_maxima VARCHAR(10),
    humedad_minima FLOAT,
    hora_humedad_minima VARCHAR(10),
    PRIMARY KEY(id_estacion,fecha),
    FOREIGN KEY (id_estacion) REFERENCES Estaciones(id_estacion) ON DELETE CASCADE
);

-- PREDICCIONES MUNICIPIOS

CREATE TABLE Prediccion (
    id_prediccion INT PRIMARY KEY,
    id_municipio VARCHAR(10) UNIQUE,
    FOREIGN KEY (id_municipio) REFERENCES Municipios(id_municipio)
);

CREATE TABLE Dia (
    id INT PRIMARY KEY,
    id_prediccion INT,
    fecha DATE UNIQUE,
    uvMax INT,
    temperatura_maxima INT,
    temperatura_minima INT,
    senstermica_maxima INT,
    senstermica_minima INT,
    humedadrelativa_maxima INT,
    humedadrelativa_minima INT,
    FOREIGN KEY (id_prediccion) REFERENCES Prediccion(id_prediccion) ON DELETE CASCADE
);

CREATE TABLE ProbPrecipitacion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_dia INT,
    periodo VARCHAR(10),
    porcentaje INT,
    FOREIGN KEY (id_dia) REFERENCES Dia(id) ON DELETE CASCADE
);

CREATE TABLE CotaNieveProv (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_dia INT,
    periodo VARCHAR(10),
    cota VARCHAR(20),
    FOREIGN KEY (id_dia) REFERENCES Dia(id) ON DELETE CASCADE
);

CREATE TABLE EstadoCielo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_dia INT,
    valor VARCHAR(10),
    periodo VARCHAR(10),
    descripcion VARCHAR(50),
    FOREIGN KEY (id_dia) REFERENCES Dia(id) ON DELETE CASCADE
);

CREATE TABLE Viento (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_dia INT,
    periodo VARCHAR(10),
    velocidad INT,
    direccion VARCHAR(10),
    FOREIGN KEY (id_dia) REFERENCES Dia(id) ON DELETE CASCADE
);

CREATE TABLE RachaMax (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_dia INT,
    periodo VARCHAR(10),
    velocidad VARCHAR(10),
    FOREIGN KEY (id_dia) REFERENCES Dia(id) ON DELETE CASCADE
);


select COUNT(*) from secciones;
-- select nombre_municipio, nombre_distrito from municipios left join distritos on municipios.id_municipio = distritos.id_municipio;



SELECT
    p.id_prediccion,
    p.id_municipio,
    d.fecha,
    d.uvMax,
    d.temperatura_maxima,
    d.temperatura_minima,
    d.senstermica_maxima,
    d.senstermica_minima,
    d.humedadrelativa_maxima,
    d.humedadrelativa_minima,
    pp.periodo AS prob_periodo,
    pp.porcentaje AS prob_porcentaje,
    cn.periodo AS cota_periodo,
    cn.cota AS cota_valor,
    ec.valor AS estado_valor,
    ec.periodo AS estado_periodo,
    ec.descripcion AS estado_valor,
    v.periodo AS viento_periodo,
    v.velocidad AS viento_velocidad,
    v.direccion AS viento_direccion,
    rm.periodo AS racha_periodo,
    rm.velocidad AS racha_velocidad
FROM 
    Prediccion p
JOIN 
    Dia d ON p.id_prediccion = d.id_prediccion
LEFT JOIN 
    ProbPrecipitacion pp ON d.id = pp.id_dia
LEFT JOIN 
    CotaNieveProv cn ON d.id = cn.id_dia
LEFT JOIN 
    EstadoCielo ec ON d.id = ec.id_dia
LEFT JOIN 
    Viento v ON d.id = v.id_dia
LEFT JOIN 
    RachaMax rm ON d.id = rm.id_dia
WHERE 
    p.id_prediccion = 1;


delete from prediccion where id_prediccion = 1;


-- para borrar
DROP TABLE IF EXISTS ProbPrecipitacion;
DROP TABLE IF EXISTS CotaNieveProv;
DROP TABLE IF EXISTS EstadoCielo;
DROP TABLE IF EXISTS Viento;
DROP TABLE IF EXISTS RachaMax;

-- Luego eliminar las tablas dependientes
DROP TABLE IF EXISTS Dia;

-- Finalmente, eliminar la tabla Prediccion
DROP TABLE IF EXISTS Prediccion;

############################################
# para el manejo de municipios y secciones #
############################################

select * from secciones;

-- buscar las seccciones que tengan codigo postal vacio
SELECT * FROM Secciones WHERE codigo_postal IS NULL AND secciones.mediana_renta_consumo != 0;

-- query para obtener la seccion con id_seccion = id_buscado
select * from secciones where id_distrito = "2812701";

select id_municipio from municipios;
select distinct count(nombre_seccion) from secciones;

select * from secciones where codigo_postal = "28290";

SELECT * 
FROM secciones 
WHERE codigo_postal LIKE '28%';

select * from secciones where latitud_centroide_seccion IS NULL;
#############################################
# para el manejo de estaciones y mediciones #
#############################################

DROP TABLE IF EXISTS estaciones;
DROP TABLE IF EXISTS mediciones;

select id_estacion, latitud, longitud
from estaciones where latitud IS NOT NULL;


select *
from estaciones est
inner join mediciones med
on est.id_estacion = med.id_estacion
where est.id_estacion = "1347T" AND fecha > "2024-11-15";


SELECT
    fecha,
    AVG(temperatura_media) AS temperatura_media,
    AVG(precipitacion) AS precipitacion,
    AVG(temperatura_minima) AS temperatura_minima,
    AVG(temperatura_maxima) AS temperatura_maxima,
    AVG(direccion_racha_maxima) AS direccion_racha_maxima,
    AVG(velocidad_media_viento) AS velocidad_media_viento,
    AVG(racha_maxima) AS racha_maxima,
    AVG(insolacion) AS insolacion,
    AVG(presion_maxima) AS presion_maxima,
    AVG(presion_minima) AS presion_minima,
    AVG(humedad_media) AS humedad_media,
    AVG(humedad_maxima) AS humedad_maxima,
    AVG(humedad_minima) AS humedad_minima
FROM Mediciones
WHERE id_estacion IN ("3194Y", "3343Y") AND fecha < '2022-01-15'
GROUP BY fecha;
