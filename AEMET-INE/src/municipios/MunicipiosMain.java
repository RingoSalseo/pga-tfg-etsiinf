package municipios;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bbdd.DatabaseConnection;
import objetosAuxiliares.CotaNieveProv;
import objetosAuxiliares.Dia;
import objetosAuxiliares.EstadoCielo;
import objetosAuxiliares.Prediccion;
import objetosAuxiliares.ProbPrecipitacion;
import objetosAuxiliares.RachaMax;
import objetosAuxiliares.Viento;
import objetosPrincipales.PrimeraRespuesta;
import objetosPrincipales.SegundaRespuesta;

public class MunicipiosMain {

	private static String municipio = "28127";
	private static final String API_URL = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/diaria/";
	private static final String API_KEY = "";
	private static final String excel_path = "";

	private static boolean pruebasPrincipales = false;
	private static boolean pruebasAux = true;

	public static void main(String[] args) {

		if(pruebasPrincipales) {
			Connection conn = DatabaseConnection.getConnection();
			String resultado = obtenerYAlmacenarPrediccionMunicipio(conn,municipio);
		}

		if (pruebasAux) {
		    PrimeraRespuesta res = realizarPrimeraPeticion(municipio);

		    if (res != null) {
		        String datosUrl = res.getDatos();
		        if (datosUrl != null) {
		            System.out.println("URL de los datos: " + datosUrl);

		            SegundaRespuesta[] prediccionArray = realizarSegundaPeticion(datosUrl);
		            if (prediccionArray != null) {
		                System.out.println(prediccionArray[0].getOrigen());
		                System.out.println(prediccionArray[0].getPrediccion());
		                System.out.println("_________________________________________________");
		                System.out.println(prediccionArray[0].getNombre());
		                System.out.println(prediccionArray[0].getId());
		                System.out.println(prediccionArray[0].getElaborado());
		            } else {
		                System.out.println("Error al obtener los datos JSON.");
		            }
		        } else {
		            System.out.println("No se pudo extraer la URL de los datos.");
		        }
		    } else {
		        System.out.println("Error al obtener la respuesta de la primera petición.");
		    }
		}


	}

	private static PrimeraRespuesta realizarPrimeraPeticion(String municipio) {
	    try {
	        String urlString = API_URL + municipio;
	        URL url = new URL(urlString);

	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	        connection.setRequestProperty("api_key", API_KEY);
	        connection.setRequestProperty("Accept", "application/json");

	        int status = connection.getResponseCode();

	        if (status == HttpURLConnection.HTTP_OK) {
	            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String inputLine;
	            StringBuilder response = new StringBuilder();

	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();

	            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy hh:mm a").create();
	            return gson.fromJson(response.toString(), PrimeraRespuesta.class);
	        } else {
	            System.out.println("Error en la primera petición, código de respuesta: " + status);
	            return null;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	private static SegundaRespuesta[] realizarSegundaPeticion(String url) {
	    try {
	        URL urlObj = new URL(url);

	        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
	        connection.setRequestMethod("GET");
	        connection.setRequestProperty("Accept", "application/json");

	        int status = connection.getResponseCode();

	        if (status == HttpURLConnection.HTTP_OK) {
	            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String inputLine;
	            StringBuilder response = new StringBuilder();

	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();

	            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy hh:mm a").create();
	            return gson.fromJson(response.toString(), SegundaRespuesta[].class);
	        } else {
	            System.out.println("Error en la segunda petición, código de respuesta: " + status);
	            return null;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	/*
	 * Método que realiza la petición a la api de Aemet, obtiene los datos y los almacena en la bbdd
	 */
	private static String obtenerYAlmacenarPrediccionMunicipio(Connection conn, String id_municipio){
		String nombreMunicipio = "";

		// Realizar la primera petición para obtener la URL de los datos
		PrimeraRespuesta res = realizarPrimeraPeticion(id_municipio);
		if (res != null) {

			// Extraer "datos" del objeto
			String datosUrl = res.getDatos();

			if (datosUrl != null) {
				System.out.println("URL de los datos: " + datosUrl);

				//Ahora creamos el segundo objeto donde guardaremos las predicciones de AEMET
				SegundaRespuesta[] prediccionArray = null;

				// Realizar la segunda peticion para obtener el JSON de la URL "datos"
				prediccionArray = realizarSegundaPeticion(datosUrl);
				if (prediccionArray != null) {
					//System.out.println("Datos JSON obtenidos: " + jsonData);
					nombreMunicipio = prediccionArray[0].getNombre();
					Prediccion prediccion = prediccionArray[0].getPrediccion();

					System.out.println(prediccion);
					System.out.println("_________________________________________________");
					System.out.println("Guardando la prediccion en la base de datos");

					//Obtenemos los datos que vamos a guardar
					int id_prediccion_bbdd = 1;
					String id_municipio_bbdd = String.valueOf(prediccionArray[0].getId());

					//Limpiamos la base de datos y añadimos la nueva predicción
					String borrarBBDD = "DELETE FROM prediccion where id_prediccion = 1;";
					try(Statement borrar = conn.createStatement()){
						int filasAfectadas = borrar.executeUpdate(borrarBBDD);
						System.out.println("Se han borrado: " + filasAfectadas + " fila/s");
					}catch (SQLException e) {
						System.err.println("Error en el borrado de la base de datos");
						e.printStackTrace();
					}
					//Inserción bbdd

					String insertPrediccionSQL = "INSERT INTO Prediccion (id_prediccion, id_municipio) VALUES (?, ?)"
							+ "ON DUPLICATE KEY UPDATE id_municipio = VALUES(id_municipio)";
					String insertDiaSQL = "INSERT INTO Dia (id, id_prediccion, fecha, uvMax, temperatura_maxima, temperatura_minima, "
							+ "senstermica_maxima, senstermica_minima, humedadrelativa_maxima, humedadrelativa_minima) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
							+ "ON DUPLICATE KEY UPDATE fecha = VALUES(fecha), uvMax = VALUES(uvMax), temperatura_maxima = VALUES(temperatura_maxima),"
							+ "temperatura_minima = VALUES(temperatura_minima), senstermica_maxima = VALUES(senstermica_maxima), senstermica_minima = VALUES(senstermica_minima),"
							+ "humedadrelativa_maxima = VALUES(humedadrelativa_maxima), humedadrelativa_minima = VALUES(humedadrelativa_minima)";

					try (PreparedStatement prediccionStmt = conn.prepareStatement(insertPrediccionSQL)) {
						prediccionStmt.setInt(1, id_prediccion_bbdd);
						prediccionStmt.setString(2, id_municipio_bbdd);
						prediccionStmt.executeUpdate();
					} catch (SQLException e) {
						System.err.println("Error en la carga de los datos en la tabla Prediccion para el Municipio: " + id_municipio_bbdd);
						e.printStackTrace();
					}

					int id = 0;
					for (Dia dia : prediccion.getDia()) {

						System.out.println("Insertando dia: " + id);
						try (PreparedStatement diaStmt = conn.prepareStatement(insertDiaSQL)) {
							diaStmt.setInt(1, id);
							diaStmt.setInt(2, id_prediccion_bbdd);
							diaStmt.setString(3, dia.getFecha());
							diaStmt.setInt(4, dia.getUvMax());

							diaStmt.setInt(5, dia.getTemperatura().getMaxima());
							diaStmt.setInt(6, dia.getTemperatura().getMinima());

							diaStmt.setInt(7, dia.getSensTermica().getMaxima());
							diaStmt.setInt(8, dia.getSensTermica().getMinima());

							diaStmt.setInt(9, dia.getHumedadRelativa().getMaxima());
							diaStmt.setInt(10, dia.getHumedadRelativa().getMinima());
							diaStmt.executeUpdate();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						insertarDetallesDia(conn,dia,id);
						id = id + 1;
					}


				} else {
					System.out.println("Error al obtener los datos JSON.");
				}
			} else {
				System.out.println("No se pudo extraer la URL de los datos.");
			}
		} else {
			System.out.println("Error al obtener la respuesta de la primera petición.");
		}
		System.out.println("Se ha actualizado la prediccion para el municipio: " + nombreMunicipio + " con id_municipio: " + id_municipio);
		return nombreMunicipio;
	}

	public static void insertarDetallesDia(Connection conn, Dia dia, int id_dia) {
		// Insertar detalles de probabilidad de precipitación
		String upsertProbPrecipitacionSQL = "INSERT INTO ProbPrecipitacion (id_dia, periodo, porcentaje) VALUES (?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE porcentaje = VALUES(porcentaje)"; // Utiliza la clave primaria o un índice único

		try (PreparedStatement probStmt = conn.prepareStatement(upsertProbPrecipitacionSQL)) {
			for (ProbPrecipitacion prob : dia.getProbPrecipitacion()) {
				probStmt.setInt(1, id_dia);
				probStmt.setString(2, prob.getPeriodo());
				probStmt.setObject(3, prob.getValue());
				probStmt.addBatch();
			}
			probStmt.executeBatch();
		} catch (SQLException e) {
			System.err.println("ERROR EN ProbPrecipitacion");
			e.printStackTrace();
		}

		// Insertar detalles de cota de nieve
		String upsertCotaNieveSQL = "INSERT INTO CotaNieveProv (id_dia, periodo, cota) VALUES (?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE cota = VALUES(cota)";

		try (PreparedStatement cotaStmt = conn.prepareStatement(upsertCotaNieveSQL)) {
			for (CotaNieveProv cota : dia.getCotaNieveProv()) {
				cotaStmt.setInt(1, id_dia);
				cotaStmt.setString(2, cota.getPeriodo());
				cotaStmt.setObject(3, cota.getValue().isEmpty() ? null : Integer.parseInt(cota.getValue()));
				cotaStmt.addBatch();
			}
			cotaStmt.executeBatch();
		} catch (SQLException e) {
			System.err.println("ERROR EN CotaNieveProv");
			e.printStackTrace();
		}

		// Insertar detalles de estado del cielo
		String upsertEstadoCieloSQL = "INSERT INTO EstadoCielo (id_dia, valor, periodo, descripcion) VALUES (?, ?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE valor = VALUES(valor), periodo = VALUES(periodo), descripcion = VALUES(descripcion)";

		try (PreparedStatement estadoStmt = conn.prepareStatement(upsertEstadoCieloSQL)) {
			for (EstadoCielo estado : dia.getEstadoCielo()) {
				estadoStmt.setInt(1, id_dia);
				estadoStmt.setString(2, estado.getValue());
				estadoStmt.setString(3, estado.getPeriodo());
				estadoStmt.setString(4, estado.getDescripcion());
				estadoStmt.addBatch();
			}
			estadoStmt.executeBatch();
		} catch (SQLException e) {
			System.err.println("ERROR EN EstadoCielo");
			e.printStackTrace();
		}


		// Insertar detalles de viento
		String upsertVientoSQL = "INSERT INTO Viento (id_dia, periodo, velocidad, direccion) VALUES (?, ?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE velocidad = VALUES(velocidad), direccion = VALUES(direccion)";

		try (PreparedStatement vientoStmt = conn.prepareStatement(upsertVientoSQL)) {
			for (Viento viento : dia.getViento()) {
				vientoStmt.setInt(1, id_dia);
				vientoStmt.setString(2, viento.getPeriodo());
				vientoStmt.setInt(3, viento.getVelocidad());
				vientoStmt.setString(4, viento.getDireccion());
				vientoStmt.addBatch();
			}
			vientoStmt.executeBatch();
		} catch (SQLException e) {
			System.err.println("ERROR EN Viento");
			e.printStackTrace();
		}

		// Insertar detalles de racha máxima
		String upsertRachaMaxSQL = "INSERT INTO RachaMax (id_dia, periodo, velocidad) VALUES (?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE velocidad = VALUES(velocidad)";

		try (PreparedStatement rachaStmt = conn.prepareStatement(upsertRachaMaxSQL)) {
			for (RachaMax racha : dia.getRachaMax()) {
				rachaStmt.setInt(1, id_dia);
				rachaStmt.setString(2, racha.getPeriodo());
				rachaStmt.setString(3, racha.getValue());
				rachaStmt.addBatch();
			}
			rachaStmt.executeBatch();
		} catch (SQLException e) {
			System.err.println("ERROR EN RachaMax");
			e.printStackTrace();
		}
	}


	/**
	 * Metodo mas importante de la clase MunicipiosMain.
	 * Se encarga de realizar la peticion y obtener los datos de predicciones de tiempo para cada municipio.
	 * 
	 * 
	 * @param path : Ruta donde se ubica el Excel con la relacion entre municipios y su id_municipio (CPRO+CMUN)
	 * @return obtenidos: Nº de municipios para los que se han obtenido diferentes mediciones
	 * @throws IOException 
	 */
	private static int peticionPrediccionesMunicipios(String path) throws IOException {

	    //////////// OBTENER LISTA IDS MUNICIPIO ////////////
	    int obtenidos = 0;
	    FileInputStream file = new FileInputStream(new File(path));
	    Workbook workbook = new XSSFWorkbook(file);
	    Sheet sheet = workbook.getSheetAt(0);

	    ArrayList<String> listaIdsMunicipios = new ArrayList<>();
	    System.out.println("Lista municipios al principio: " + listaIdsMunicipios);

	    for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
	        Row row = sheet.getRow(rowIndex);

	        if (row.getCell(1).getCellType() == CellType.NUMERIC) {
	            listaIdsMunicipios.add(String.valueOf((int) row.getCell(1).getNumericCellValue()) + row.getCell(2).getStringCellValue());
	        } else if (row.getCell(2).getCellType() == CellType.NUMERIC) {
	            listaIdsMunicipios.add(row.getCell(1).getStringCellValue() + String.valueOf((int) row.getCell(2).getNumericCellValue()));
	        } else {
	            listaIdsMunicipios.add(row.getCell(1).getStringCellValue() + row.getCell(2).getStringCellValue());
	        }
	    }

	    //////////// REALIZAR PETICIONES PARA CADA UNO DE LOS MUNICIPIOS ////////////
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter("municipiosRespuesta.txt"))) {
	        for (String idMunicipio : listaIdsMunicipios) {

	            PrimeraRespuesta res = realizarPrimeraPeticion(idMunicipio);
	            if (res != null) {
	                String datosUrl = res.getDatos();

	                if (datosUrl != null) {
	                    SegundaRespuesta[] prediccionArray = realizarSegundaPeticion(datosUrl);
	                    if (prediccionArray != null && prediccionArray.length > 0) {
	                        writer.write(prediccionArray[0].getPrediccion().toString());
	                        writer.newLine();
	                        obtenidos++;
	                    }
	                }
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error al escribir en el archivo: " + e.getMessage());
	    }

	    return obtenidos;
	}

}
