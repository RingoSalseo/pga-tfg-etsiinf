package estaciones.nuevo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bbdd.DatabaseConnection;
import objetosPrincipales.PrimeraRespuesta;

public class ActualizarCoordenadasEstaciones {

	
	private static final String BASE_API_URL = "https://opendata.aemet.es/opendata/api/valores/climatologicos/inventarioestaciones/todasestaciones";
	private static final String API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYWJsby5ndWVycmVyby5hbHZhcmV6QGFsdW1ub3MudXBtLmVzIiwianRpIjoiYjM5NTE0MmMtNzM3ZC00YjhjLTg2OTEtNTNmOWI1MTJmOWI4IiwiaXNzIjoiQUVNRVQiLCJpYXQiOjE3MjUwMDU3NjUsInVzZXJJZCI6ImIzOTUxNDJjLTczN2QtNGI4Yy04NjkxLTUzZjliNTEyZjliOCIsInJvbGUiOiIifQ.bXj_Lrbtnr2ooS-2v9Lxmf9P_uoCWdPwvP9XToSmoIk";

	private static String realizarPrimeraPeticion() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(BASE_API_URL);

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
	 * Función que convierte coordenadas geográficas en formato "X" grados "Y" minutos "Z" segundos a "G" grados
	 */
	public static double convertirAGradosDecimales(String coordenada) {
	    // Extraer los grados, minutos y segundos
	    int grados = Integer.parseInt(coordenada.substring(0, 2));
	    int minutos = Integer.parseInt(coordenada.substring(2, 4));
	    int segundos = Integer.parseInt(coordenada.substring(4, 6));
	    char direccion = coordenada.charAt(6);

	    // Convertir a grados decimales
	    double decimal = grados + (minutos / 60.0) + (segundos / 3600.0);

	    // Ajustar el signo según la dirección (N/S, E/W)
	    if (direccion == 'S' || direccion == 'W') {
	        decimal *= -1;
	    }

	    return decimal;
	}
	
	public static void main (String[] args) {
		
		//El objetivo es actualizar 86 estaciones que tras la primera insercion de coordenadas, seguían sin tener coordenadas
		
		Connection conn = DatabaseConnection.getConnection();
		
		PrimeraRespuesta aux = null;
		ObjetoActualizarCoordenadas[] respuesta = null;

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
		Gson gson  = gsonBuilder.create();

		String response = realizarPrimeraPeticion();
		String urlDatosEstaciones = null;

		if (response != null) {
			aux = gson.fromJson(response, PrimeraRespuesta.class);
			urlDatosEstaciones = aux.getDatos();
		}
		String res = realizarSegundaPeticion(urlDatosEstaciones);

		respuesta = gson.fromJson(res, ObjetoActualizarCoordenadas[].class);
		
		String updateQuery = "UPDATE estaciones SET latitud = ?, longitud = ? WHERE id_estacion = ? AND latitud IS NULL";

		try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
			System.out.println("Actualizando Estaciones...");
		    for (ObjetoActualizarCoordenadas estacion : respuesta) {
		        String latitudOriginal = estacion.getLatitud();
		        String longitudOriginal = estacion.getLongitud();

		        double latitudDecimal = convertirAGradosDecimales(latitudOriginal);
		        double longitudDecimal = convertirAGradosDecimales(longitudOriginal);

		        estacion.setLatitud(latitudDecimal + "");
		        estacion.setLongitud(longitudDecimal + "");

		        stmt.setDouble(1, latitudDecimal);
		        stmt.setDouble(2, longitudDecimal);
		        stmt.setString(3, estacion.getIdEstacion());

		        int rowsAffected = stmt.executeUpdate();
		        if (rowsAffected > 0) {
		            System.out.println("Actualizada estación con indicativo: " + estacion.getIdEstacion());
		        }
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		}
		System.out.println("Actualizacion Estaciones Terminado.");
	}
}
