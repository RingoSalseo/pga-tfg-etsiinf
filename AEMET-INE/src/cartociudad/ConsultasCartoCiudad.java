package cartociudad;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConsultasCartoCiudad {

	private static final String API_URL = "https://www.cartociudad.es/geocoder/api/geocoder/reverseGeocode?";
	
	private static final String API_URL_LOCATIONUS = "https://us1.locationiq.com/v1/reverse";
	private static final String API_KEY_LOCATIONUS = "";
	

	// Metodo que devuelve los datos de un punto geografico dadas unas coordenadas geograficas
	// Objeto de tipo ObjetoCartoCiudad
	public static ObjetoCartoCiudad consultarCartoCiudad(Double latitud, Double longitud) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(API_URL).queryParam("lat", latitud).queryParam("lon", longitud);

		ObjetoCartoCiudad resultado;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
		Gson gson  = gsonBuilder.create();

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.get();


		int status = response.getStatus();
		if (status == 200) {
			resultado = gson.fromJson(response.readEntity(String.class), ObjetoCartoCiudad.class);
			response.close();
			return resultado;
		} else {
			if(status == 204) {
				System.out.println("No se ha encontrado respuesta para las coordenadas: (" + longitud + ", " + latitud + ")");
				response.close();
				return null;
			}
			else {
				System.out.println("Error en petici�n de consulta a la API de CartoCiudad, c�digo de respuesta: " + status);
				response.close();
				return null;
			}
		}
	}
	
	/*
	 * M�todo que consulta la api de LocationUS. Pruebas para ver la precisi�n. Parece m�s precisa que CartoCiudad
	 */
	public static ObjetoLocationUS consultarLocationUS(Double latitud, Double longitud) {
	    Client client = ClientBuilder.newClient();
	    
	    // Agregar par�metros de la consulta (clave, latitud y longitud)
	    WebTarget target = client.target(API_URL_LOCATIONUS)
	            .queryParam("key", API_KEY_LOCATIONUS)
	            .queryParam("lat", latitud)
	            .queryParam("lon", longitud)
	            .queryParam("format", "json"); // Aseguramos el formato JSON en la respuesta

	    GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
	    Gson gson = gsonBuilder.create();

	    Response response = target
	            .request(MediaType.APPLICATION_JSON)
	            .get();

	    int status = response.getStatus();
	    System.out.println("CODIGO LOCATION US COORDENADAS BUSCADAS : " +  latitud + ", " + longitud);
	    if (status == 200) {
	        String jsonResponse = response.readEntity(String.class);
	        response.close();
	        return gson.fromJson(jsonResponse, ObjetoLocationUS.class);
	    } else {
	        if (status == 204) {
	            System.out.println("No se ha encontrado respuesta para las coordenadas: (" + longitud + ", " + latitud + ")");
	        } else {
	            System.out.println("Error en la petici�n a la API, c�digo de respuesta: " + status);
	        }
	        response.close();
	        return null;
	    }
	}

	
	
}
