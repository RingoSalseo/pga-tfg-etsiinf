package cartociudad;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConsultasCartoCiudad {

	private static final String API_URL = "https://www.cartociudad.es/geocoder/api/geocoder/reverseGeocode?";
	private static final String API_URL_LOCATIONUS = "https://us1.locationiq.com/v1/reverse";
	private static final String API_KEY_LOCATIONUS = "";

	// Método que devuelve los datos de un punto geográfico dadas unas coordenadas geográficas
	// Objeto de tipo ObjetoCartoCiudad
	public static ObjetoCartoCiudad consultarCartoCiudad(Double latitud, Double longitud) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
		Gson gson = gsonBuilder.create();

		try {
			// Construir la URL con los parámetros de latitud y longitud
			String urlStr = API_URL + "lat=" + latitud + "&lon=" + longitud;
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			int status = conn.getResponseCode();
			if (status == 200) {
				// Leer la respuesta de la API
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuilder content = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}
				in.close();
				conn.disconnect();

				// Convertir JSON a objeto Java
				return gson.fromJson(content.toString(), ObjetoCartoCiudad.class);
			} else if (status == 204) {
				System.out.println("No se ha encontrado respuesta para las coordenadas: (" + longitud + ", " + latitud + ")");
			} else {
				System.out.println("Error en petición de consulta a la API de CartoCiudad, código de respuesta: " + status);
			}
			conn.disconnect();
		} catch (IOException e) {
			System.err.println("Error al conectar con la API de CartoCiudad: " + e.getMessage());
		}
		return null;
	}

	/*
	 * Método que consulta la API de LocationUS. Pruebas para ver la precisión. Parece más precisa que CartoCiudad
	 */
	public static ObjetoLocationUS consultarLocationUS(Double latitud, Double longitud) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
		Gson gson = gsonBuilder.create();

		try {
			// Construir la URL con los parámetros necesarios
			String urlStr = API_URL_LOCATIONUS +
					"?key=" + API_KEY_LOCATIONUS +
					"&lat=" + latitud +
					"&lon=" + longitud +
					"&format=json";
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			int status = conn.getResponseCode();
			System.out.println("CODIGO LOCATION US COORDENADAS BUSCADAS : " + latitud + ", " + longitud);
			if (status == 200) {
				// Leer la respuesta de la API
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuilder content = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}
				in.close();
				conn.disconnect();

				// Convertir JSON a objeto Java
				return gson.fromJson(content.toString(), ObjetoLocationUS.class);
			} else if (status == 204) {
				System.out.println("No se ha encontrado respuesta para las coordenadas: (" + longitud + ", " + latitud + ")");
			} else {
				System.out.println("Error en la petición a la API, código de respuesta: " + status);
			}
			conn.disconnect();
		} catch (IOException e) {
			System.err.println("Error al conectar con la API de LocationIQ: " + e.getMessage());
		}
		return null;
	}
}
