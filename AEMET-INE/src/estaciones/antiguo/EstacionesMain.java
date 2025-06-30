package estaciones.antiguo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import objetosPrincipales.PrimeraRespuesta;

public class EstacionesMain {

	private static final String API_URL = "https://opendata.aemet.es/opendata/api/observacion/convencional/todas";
	private static final String API_KEY = "";

	private static boolean pruebas = true;

	public static void main(String[] args) {


		PrimeraRespuesta aux = null;
		Estacion[] respuesta  = null;

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
		Gson gson  = gsonBuilder.create();

		String jsonResponse = realizarPrimeraPeticion();
		String url = null;
		if (jsonResponse != null) {
			aux = gson.fromJson(jsonResponse, PrimeraRespuesta.class);
			url = aux.getDatos();
		}

		respuesta = gson.fromJson(realizarSegundaPeticion(url), Estacion[].class);


		//////////////////////////// PRUEBAS PARA ESTACIONES ////////////////////////////
		if(pruebas) {
			/*
			 * PRUEBA CALCULAR ESTACION MAS CERCANA A UNA UBICACION ALEATORIA
			 * 
			 * Dada una longitud y latitud aproximadas, calcular la estacion mas cercana a dichas coordenadas
			 * Para ello recorremos el array de estaciones
			 */
			double lt = 40.4929200;
			double lg = -3.8737100;
			System.out.println(estacionMasCercana(respuesta, lt, lg));

			// MODO 0 = Guarda los nombres y el id de la estacion en cuestion en un archivo de texto
			// MODO 1 = Guarda un objeto completo en un archivo de texto
			// MODO 2 = Guarda todas las estaciones en un archivo de texto
			int MODO = 2;
			String NOMBRE_ARCHIVO_TXT = "Salida.txt"; //Salida.txt //Estacion.txt //PruebaEstacion0016A.txt
			String idEstacionABuscar = "0016A";
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO_TXT))) {
				switch(MODO) {
				case 0:
					for (int i = 0; i < respuesta.length; i++) {
						// NOMBRE E ID_ESTACION
						writer.write(respuesta[i].getNOMBRE() + "\t");
						writer.write(respuesta[i].getID_ESTACION() +"\t");
						writer.newLine();
					}
					System.out.println("Los nombres de las estaciones y sus IDs se han guardado correctamente en: " + NOMBRE_ARCHIVO_TXT);
					break;
				case 1:
					writer.write(respuesta[0].toString());
					System.out.println("Los nombres de las estaciones se han guardado correctamente en: " + NOMBRE_ARCHIVO_TXT);
					break;
				case 2: 
					for(int i = 0 ; i < respuesta.length; i++) {
						//					if(respuesta[i].getID_ESTACION().equals(idEstacionABuscar)) {
						//						writer.write(respuesta[i].toString());
						//						writer.newLine();
						//					}
						writer.write(respuesta[i].toString());
						writer.newLine();
					}
				}
			} catch (IOException e) {
				System.err.println("Error al escribir en el archivo: " + e.getMessage());
			}
		}
	}

	private static String realizarPrimeraPeticion() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(API_URL);

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
			System.out.println("Error en la primera petici�n, c�digo de respuesta: " + status);
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
			System.out.println("Error en la segunda petici�n, c�digo de respuesta: " + status);
			response.close();
			return null;
		}
	}

	/*
	 * Devuelve la distancia en kilometros entre dos coordenadas. Se utiliza la formula de Haversine
	 * https://www.viafirma.com/es/implementacion-de-la-formula-haversine-en-java/
	 */
	public static double calcularDistancia(double latitud1, double longitud1, double latitud2, double longitud2) {
		double dLat = Math.toRadians(latitud2 - latitud1);
		double dLon = Math.toRadians(longitud2 - longitud1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(Math.toRadians(latitud1)) * Math.cos(Math.toRadians(latitud2)) *
				Math.sin(dLon / 2) * Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return 6371 * c; // distancia en kilometros
	}

	/*
	 * Devuelve la estacion mas cercana de todas, dadas unas coordenadas aproximadas
	 */
	public static Estacion estacionMasCercana(Estacion[] estaciones, double latitud, double longitud) {
		Estacion resultado = null;
		double distancia = 6000; // distancia en kilometros

		for(int i = 0; i < estaciones.length; i++) {
			double aux = calcularDistancia(latitud, longitud, estaciones[i].getLATITUD(), estaciones[i].getLONGITUD());
			if(aux < distancia) {
				resultado = estaciones[i];
				distancia = aux;
			}
		}

		return resultado;
	}

}
