package estaciones.nuevo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import bbdd.DatabaseConnection;
import estaciones.antiguo.Estacion;
import objetosPrincipales.PrimeraRespuesta;

public class HistoricoEstaciones {

	private static final String BASE_API_URL = "https://opendata.aemet.es/opendata/api/valores/climatologicos/diarios/datos/fechaini/";
	private static final String API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYWJsby5ndWVycmVyby5hbHZhcmV6QGFsdW1ub3MudXBtLmVzIiwianRpIjoiYjM5NTE0MmMtNzM3ZC00YjhjLTg2OTEtNTNmOWI1MTJmOWI4IiwiaXNzIjoiQUVNRVQiLCJpYXQiOjE3MjUwMDU3NjUsInVzZXJJZCI6ImIzOTUxNDJjLTczN2QtNGI4Yy04NjkxLTUzZjliNTEyZjliOCIsInJvbGUiOiIifQ.bXj_Lrbtnr2ooS-2v9Lxmf9P_uoCWdPwvP9XToSmoIk";



	private static String realizarPrimeraPeticionHistorico(String fechaIniStr, String fechaFinStr) {
		Client client = ClientBuilder.newClient();
		String url = BASE_API_URL + fechaIniStr + "/fechafin/" + fechaFinStr + "/todasestaciones";
		WebTarget target = client.target(url);

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.header("api_key", API_KEY)
				.get();

		int status = response.getStatus();
		if (status == 200) {
			String jsonResponse = response.readEntity(String.class);
			response.close();
			return jsonResponse;
		} else {
			System.out.println("Error en la primera petición, código de respuesta: " + status);
			response.close();
			return null;
		}
	}
	
	private static String realizarPrimeraPeticionCoordenadas() {
		Client client = ClientBuilder.newClient();
		String url = "https://opendata.aemet.es/opendata/api/observacion/convencional/todas";
		WebTarget target = client.target(url);

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.header("api_key", API_KEY)
				.get();

		int status = response.getStatus();
		if (status == 200) {
			String jsonResponse = response.readEntity(String.class);
			response.close();
			return jsonResponse;
		} else {
			System.out.println("Error en la primera petición, código de respuesta: " + status);
			response.close();
			return null;
		}
	}

	private static String realizarSegundaPeticion(String url) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(url);

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.get();

		int status = response.getStatus();
		if (status == 200) {
			String jsonResponse = response.readEntity(String.class);
			response.close();
			return jsonResponse;
		} else {
			System.out.println("Error en la segunda petición, código de respuesta: " + status);
			response.close();
			return null;
		}
	}

	/*
	 * Tendremos que realizar peticiones para las fechas comprendidas entre 2022-01-01 y 2024-11-15
	 */
	public static String obtenerHistoricoEstaciones(Connection conn, String fecha_inicial, String fecha_final) throws ParseException {
		
		final int limite_maximo_api = 15;

		// Crear un SimpleDateFormat para trabajar con las fechas
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		// Convertir las fechas de inicio y fin a objetos Date
		Date fechaInicio = sdf.parse(fecha_inicial);
		Date fechaFin = sdf.parse(fecha_final);

		// para obtener todas las fechas
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaInicio);

		// para pasar de date a string => 2024-11-01T00:00:00UTC
		SimpleDateFormat sdfSalida = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'UTC'");
		sdfSalida.setTimeZone(TimeZone.getTimeZone("UTC"));

		// Bucle para realizar las peticiones
		while (calendar.getTime().before(fechaFin)) {

			// Avanzar el calendario por 15 días
			calendar.add(Calendar.DATE, limite_maximo_api);
			Date fechaIntervaloFin = calendar.getTime();

			// Formatear las fechas en formato string con el formato adecuado
			String fechaIniStr = sdfSalida.format(fechaInicio);
			String fechaFinStr = sdfSalida.format(fechaIntervaloFin);

			// Imprimir las fechas
			System.out.println("fechaINI: " + fechaIniStr + ", fechaFIN: " + fechaFinStr);


			PrimeraRespuesta aux = null;
			ObjetoEstacionHistorico[] respuesta = null;

			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
			Gson gson  = gsonBuilder.create();

			String response = realizarPrimeraPeticionHistorico(fechaIniStr, fechaFinStr);
			String urlDatosAemet = null;

			if (response != null) {
				aux = gson.fromJson(response, PrimeraRespuesta.class);
				urlDatosAemet = aux.getDatos();
			}
			String res = realizarSegundaPeticion(urlDatosAemet);

			respuesta = gson.fromJson(res, ObjetoEstacionHistorico[].class);

		
			try (PreparedStatement stmtEstaciones = conn.prepareStatement(
			        "INSERT INTO Estaciones (id_estacion, nombre, provincia, altitud) " +
			        "VALUES (?, ?, ?, ?) " +
			        "ON DUPLICATE KEY UPDATE " +
			        "nombre = VALUES(nombre), provincia = VALUES(provincia), altitud = VALUES(altitud)")) {

			    try (PreparedStatement stmtMediciones = conn.prepareStatement(
			            "INSERT INTO Mediciones (id_estacion, fecha, temperatura_media, precipitacion, " +
			            "temperatura_minima, hora_temperatura_minima, temperatura_maxima, hora_temperatura_maxima, " +
			            "direccion_racha_maxima, velocidad_media_viento, racha_maxima, horaracha, insolacion, " +
			            "presion_maxima, hora_presion_maxima, presion_minima, hora_presion_minima, humedad_media, " +
			            "humedad_maxima, hora_humedad_maxima, humedad_minima, hora_humedad_minima) " +
			            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
			            "ON DUPLICATE KEY UPDATE " +
			            "temperatura_media = VALUES(temperatura_media), precipitacion = VALUES(precipitacion), " +
			            "temperatura_minima = VALUES(temperatura_minima), hora_temperatura_minima = VALUES(hora_temperatura_minima), " +
			            "temperatura_maxima = VALUES(temperatura_maxima), hora_temperatura_maxima = VALUES(hora_temperatura_maxima), " +
			            "direccion_racha_maxima = VALUES(direccion_racha_maxima), velocidad_media_viento = VALUES(velocidad_media_viento), " +
			            "racha_maxima = VALUES(racha_maxima), horaracha = VALUES(horaracha), insolacion = VALUES(insolacion), " +
			            "presion_maxima = VALUES(presion_maxima), hora_presion_maxima = VALUES(hora_presion_maxima), " +
			            "presion_minima = VALUES(presion_minima), hora_presion_minima = VALUES(hora_presion_minima), " +
			            "humedad_media = VALUES(humedad_media), humedad_maxima = VALUES(humedad_maxima), " +
			            "hora_humedad_maxima = VALUES(hora_humedad_maxima), humedad_minima = VALUES(humedad_minima), " +
			            "hora_humedad_minima = VALUES(hora_humedad_minima)")) {

			        for (ObjetoEstacionHistorico estacion : respuesta) {
			            // Insertar en la tabla Estaciones
			            stmtEstaciones.setString(1, estacion.getId_estacion());
			            stmtEstaciones.setString(2, estacion.getNombre());
			            stmtEstaciones.setString(3, estacion.getProvincia());
			            stmtEstaciones.setFloat(4, Float.parseFloat(estacion.getAltitud()));
			            stmtEstaciones.addBatch();

			            // Insertar en la tabla Mediciones
			            stmtMediciones.setString(1, estacion.getId_estacion());
			            stmtMediciones.setDate(2, estacion.getFecha());
			            if(estacion.getTemperatura_media() == null) {
			            	stmtMediciones.setNull(3, Types.FLOAT);
			            }
			            else {
			            	stmtMediciones.setFloat(3, ObjetoEstacionHistorico.reemplazarComa(estacion.getTemperatura_media()));
			            }
			            
			            if (estacion.getPrecipitacion() == null) {
			                stmtMediciones.setNull(4, Types.FLOAT);
			            } else {
			            	//Significa que la precipitacion ha sido inferior a 0.1 mm
			            	//En nuestro caso será igual a 0
			            	if(estacion.getPrecipitacion().equals("Ip") | estacion.getPrecipitacion().equals("Acum")) {
			            		stmtMediciones.setFloat(4, 0);
			            	}
			            	else {
			            		stmtMediciones.setFloat(4, ObjetoEstacionHistorico.reemplazarComa(estacion.getPrecipitacion()));
			            	}
			            }

			            if (estacion.getTemperatura_minima() == null) {
			                stmtMediciones.setNull(5, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(5, ObjetoEstacionHistorico.reemplazarComa(estacion.getTemperatura_minima()));
			            }

			            stmtMediciones.setString(6, estacion.getHora_temperatura_minima());

			            if (estacion.getTemperatura_maxima() == null) {
			                stmtMediciones.setNull(7, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(7, ObjetoEstacionHistorico.reemplazarComa(estacion.getTemperatura_maxima()));
			            }

			            stmtMediciones.setString(8, estacion.getHora_temperatura_maxima());

			            if (estacion.getDireccion_racha_maxima() == null) {
			                stmtMediciones.setNull(9, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(9, ObjetoEstacionHistorico.reemplazarComa(estacion.getDireccion_racha_maxima()));
			            }

			            if (estacion.getVelocidad_media_viento() == null) {
			                stmtMediciones.setNull(10, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(10, ObjetoEstacionHistorico.reemplazarComa(estacion.getVelocidad_media_viento()));
			            }

			            if (estacion.getRacha_maxima() == null) {
			                stmtMediciones.setNull(11, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(11, ObjetoEstacionHistorico.reemplazarComa(estacion.getRacha_maxima()));
			            }

			            stmtMediciones.setString(12, estacion.getHoraracha());

			            if (estacion.getInsolacion() == null) {
			                stmtMediciones.setNull(13, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(13, ObjetoEstacionHistorico.reemplazarComa(estacion.getInsolacion()));
			            }

			            if (estacion.getPresion_maxima() == null) {
			                stmtMediciones.setNull(14, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(14, ObjetoEstacionHistorico.reemplazarComa(estacion.getPresion_maxima()));
			            }

			            stmtMediciones.setString(15, estacion.getHora_presion_maxima());

			            if (estacion.getPresion_minima() == null) {
			                stmtMediciones.setNull(16, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(16, ObjetoEstacionHistorico.reemplazarComa(estacion.getPresion_minima()));
			            }

			            stmtMediciones.setString(17, estacion.getHora_presion_minima());

			            if (estacion.getHumedad_media() == null) {
			                stmtMediciones.setNull(18, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(18, ObjetoEstacionHistorico.reemplazarComa(estacion.getHumedad_media()));
			            }

			            if (estacion.getHumedad_maxima() == null) {
			                stmtMediciones.setNull(19, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(19, ObjetoEstacionHistorico.reemplazarComa(estacion.getHumedad_maxima()));
			            }

			            stmtMediciones.setString(20, estacion.getHora_humedad_maxima());

			            if (estacion.getHumedad_minima() == null) {
			                stmtMediciones.setNull(21, Types.FLOAT);
			            } else {
			                stmtMediciones.setFloat(21, ObjetoEstacionHistorico.reemplazarComa(estacion.getHumedad_minima()));
			            }

			            stmtMediciones.setString(22, estacion.getHora_humedad_minima());
			            

			            stmtMediciones.addBatch();
			        }

			        // Ejecutar los batch para cada tabla
			        stmtEstaciones.executeBatch();
			        stmtMediciones.executeBatch();
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}
			
			// Avanzar la fecha de inicio para el siguiente intervalo
			fechaInicio = fechaIntervaloFin;
		}

		return "Los datos historicos entre las fechas : " + fecha_inicial +  " --- " + fecha_final + " han sido guardados";
	}
	
	
	public static String insertarCoordenadasEstaciones(Connection conn) {
        // Lista para almacenar los IDs de estaciones ya procesados
        ArrayList<String> estacionesProcesadas = new ArrayList<>();
        
        // Declaración de objetos para la respuesta de la API
        PrimeraRespuesta aux = null;
        Estacion[] respuesta = null;

        // Configuración de Gson para deserializar las respuestas JSON
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
        Gson gson = gsonBuilder.create();

        // Realiza la primera petición para obtener el URL de las estaciones
        String response = realizarPrimeraPeticionCoordenadas();
        String urlDatosEstaciones = null;

        if (response != null) {
            aux = gson.fromJson(response, PrimeraRespuesta.class);
            urlDatosEstaciones = aux.getDatos();
        }

        // Realiza la segunda petición para obtener los datos de las estaciones
        String res = realizarSegundaPeticion(urlDatosEstaciones);
        respuesta = gson.fromJson(res, Estacion[].class);

        // Query de inserción de latitud y longitud
        String sql = "INSERT INTO estaciones (id_estacion, latitud, longitud) "
                   + "VALUES (?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE latitud = VALUES(latitud), longitud = VALUES(longitud)";
        System.out.println(respuesta[0]);
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (Estacion estacion : respuesta) {
            	System.out.println("Actualizando coordenadas de la estacion: " + estacion.getID_ESTACION());
                // Si la estacion ya ha sido actualizada, continuamos
                if (estacionesProcesadas.contains(estacion.getID_ESTACION())) {
                    continue;
                }

                // Insertamos latitud y longitud en una estacion
                preparedStatement.setString(1, estacion.getID_ESTACION());
                preparedStatement.setFloat(2, estacion.getLATITUD());
                preparedStatement.setFloat(3, estacion.getLONGITUD());
                preparedStatement.addBatch();

                // Guardamos la estacion actualizada
                estacionesProcesadas.add(estacion.getID_ESTACION());
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error al insertar las coordenadas en la base de datos: " + e.getMessage());
        }
        return "Inserciones completadas correctamente";
    }
	
	public static void main (String[] args) throws ParseException {

		Boolean pruebas = false;
		if(pruebas) {

			String fecha_ini = "2022-01-01";
			String fecha_fin = "2022-01-15";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			Date dateInicial = sdf.parse(fecha_ini);
			Date dateFinal = sdf.parse(fecha_fin);

			SimpleDateFormat sdfSalida = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'UTC'");
			sdfSalida.setTimeZone(TimeZone.getTimeZone("UTC"));

			String fechaInicial = sdfSalida.format(dateInicial);
			String fechaFinal = sdfSalida.format(dateFinal);

			String url = BASE_API_URL + fechaInicial + "/fechafin/" + fechaFinal + "/todasestaciones";
			System.out.println(url);
			String respuesta = realizarPrimeraPeticionHistorico(fechaInicial, fechaFinal);
			System.out.println(respuesta);
		}
		
		Boolean codigoPrincipal = true;
		
		if(codigoPrincipal) {
			final String fecha_inicial_por_defecto = "2022-01-01";
			final String fecha_final_por_defecto = "2024-11-15";
			
			final String fecha_inicial = "2024-11-15";
			final String fecha_final = "2024-12-27";
			
			Connection conn = DatabaseConnection.getConnection();
			obtenerHistoricoEstaciones(conn, fecha_inicial, fecha_final);
		}
		
		Boolean insertarCoordenadas = false;
		if(insertarCoordenadas) {
			Connection conn = DatabaseConnection.getConnection();
			insertarCoordenadasEstaciones(conn);
		}
	}

}
