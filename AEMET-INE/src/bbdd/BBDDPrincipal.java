package bbdd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Point;

import cartociudad.ConsultasCartoCiudad;
import cartociudad.ObjetoCartoCiudad;
import shapefile.ShapefileUtils;


/*
 * Esta es la clase principal de la BBDD
 * Desde aquí se realizarán todas las operaciones relacionadas con la base de datos.
 */
public class BBDDPrincipal {

	private static Connection conn;


	public BBDDPrincipal() {
		try {
			// Iniciar la conexión a la base de datos
			conn = DatabaseConnection.getConnection();
		} catch (Exception e) {
			System.err.println("Error al crear la conexion: " + e.getMessage());
		}
	}

	public Connection getConnection() {
		return conn;
	}


	/*
	 * El objetivo es guardar los códigos postales de todas las secciones censales.
	 * Es necesario que exista la seccion censal previamente en la base de datos (Haber cargado los datos del INE).
	 * Se realizará consultando a CartoCiudad (API)
	 * 
	 * ACTUALIZACION. Existe un servicio de consulta masivo que calcula, dado un csv con todos los datos, sus respectivos codigos postales
	 * https://www.cartociudad.es/web/portal/herramientas-calculos/conversor
	 * Se realizará de forma manual subiendo un csv, creado con codigo, de todas las coordenadas geográficas.
	 * 
	 */
	public static void almacenarCodigosPostalesEnCSV(Connection conn) throws Exception {
		// Primero obtenemos los IDs de los municipios.
		ArrayList<String> listaIdMunicipios = obtenerTodosLosIdMunicipio(conn);

		// Lista para almacenar las líneas del CSV temporalmente.
		ArrayList<String> lineasCSV = new ArrayList<>();

		// Si el archivo no existe, añadimos la fila de encabezado al inicio de la lista.
		File file = new File("coordenadas_secciones_censales.csv");
		boolean isNewFile = !file.exists();
		if (isNewFile) {
			lineasCSV.add("Id|TIPO_VIA|NOMBRE_VIA|PORTAL1|PORTAL2|CODPOSTAL|COD_INE_MUNICIPIO|LATITUD_WGS84_4326|LONGITUD_WGS84_4326|PROVINCIA|MUNICIPIO|POBLACION");
		}
		// Procesamos los municipios y sus secciones censales.
		for (String id_municipio : listaIdMunicipios) {
			ArrayList<SimpleFeature> seccionesCensales = ShapefileUtils.obtenerSeccionesCensalesPorMunicipio(id_municipio);

			// Para cada sección censal, obtenemos su centroide (coordenadas).
			for (SimpleFeature seccion : seccionesCensales) {
				String cusec = seccion.getAttribute("CUSEC").toString();
				Point punto = ShapefileUtils.obtenerCentroSeccion(cusec);

				// Formateamos la línea y la añadimos a la lista.
				String linea = String.format("|||||||%s|%s|||", punto.getY(), punto.getX());
				System.out.println(linea);
				lineasCSV.add(linea);
			}
			System.out.println("Terminado municipio: " + id_municipio);
		}

		// Escribimos todas las líneas en el archivo de una sola vez.
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
			for (String linea : lineasCSV) {
				writer.write(linea);
				writer.newLine();
			}
		} catch (IOException e) {
			System.err.println("Error al escribir el archivo CSV: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void almacenarCP(Connection conn) throws Exception {
		// Primero obtenemos los IDs de los municipios. Será necesario para obtener todas las secciones censales de cada municipio.
		ArrayList<String> listaIdMunicipios = obtenerTodosLosIdMunicipio(conn);

		// Crear un libro de trabajo (workbook) de Excel
		Workbook workbook = new XSSFWorkbook();
		// Crear una hoja (sheet) dentro del workbook
		Sheet sheet = workbook.createSheet("Codigos Postales");

		// Crear la primera fila con los encabezados
		Row headerRow = sheet.createRow(0);
		Cell headerCell1 = headerRow.createCell(0);
		headerCell1.setCellValue("CUSEC");
		Cell headerCell2 = headerRow.createCell(1);
		headerCell2.setCellValue("CODIGO POSTAL");

		// Inicializamos una fila para insertar datos
		int rowNum = 1;

		// Ahora, para cada id_municipio en listaIdMunicipios, obtenemos sus secciones censales.
		for (int i = 0; i < listaIdMunicipios.size(); i++) {
			String id_municipio = listaIdMunicipios.get(i);
			ArrayList<SimpleFeature> seccionesCensales = ShapefileUtils.obtenerSeccionesCensalesPorMunicipio(id_municipio);

			// Inicializamos el código postal por defecto.
			String codigoPostalPorDefecto = "0";

			// Para cada sección censal, obtenemos su código postal asociado mediante la API CartoCiudad.
			for (int j = 0; j < seccionesCensales.size(); j++) {
				String cusec = seccionesCensales.get(j).getAttribute("CUSEC").toString();
				Point punto = ShapefileUtils.obtenerCentroSeccion(cusec);

				ObjetoCartoCiudad cartociudad = ConsultasCartoCiudad.consultarCartoCiudad(punto.getY(), punto.getX());

				String postal_code_carto_ciudad = null;
				if (cartociudad == null) {
					postal_code_carto_ciudad = codigoPostalPorDefecto;
				} else {
					postal_code_carto_ciudad = cartociudad.getPostalCode();
					codigoPostalPorDefecto = postal_code_carto_ciudad;
				}

				// Crear una nueva fila en el archivo Excel y escribir el CUSEC y el código postal
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(cusec);
				row.createCell(1).setCellValue(postal_code_carto_ciudad);
			}
			System.out.println("Terminado municipio: " + id_municipio);
		}

		// Escribir el contenido en un archivo Excel
		try (FileOutputStream fileOut = new FileOutputStream("codigos_postales_consulta_api.xlsx")) {
			workbook.write(fileOut);
		} catch (IOException e) {
			System.err.println("Error al escribir el archivo Excel: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Cerrar el workbook
			workbook.close();
		}
	}

	/*
	 * Obtiene el id_municipio de todos los municipios almacenados en la bbdd
	 */
	public static ArrayList<String> obtenerTodosLosIdMunicipio(Connection conn){

		ArrayList<String> resultado = new ArrayList<String>();
		String consulta = "select id_municipio from municipios;";
		Statement st = null;
		ResultSet rs = null;

		try {
			st = conn.createStatement();
			rs = st.executeQuery(consulta);

			while(rs.next()) {
				String id_municipio = rs.getString(1);
				resultado.add(id_municipio);
			}

		} catch (SQLException e) {
			System.err.println("Error al crear un Statement o al ejecutar la query : " + consulta);
			e.printStackTrace();
		} finally {
			//Para cerrar Statement y ResultSet
			try {
				if(st != null) {
					st.close();
				}
				if(rs != null) {
					rs.close();
				}
			} catch (SQLException e1) {
				System.err.println("Error al cerrar el Statement o el ResultSet");
				e1.printStackTrace();
			}
		}

		return resultado;
	}

	/*
	 * Dada la necesidad de obtener el centroide de una seccion censal, se almacenaran en la bbdd
	 * Primero obtendremos todos los id_seccion de la bbdd, despues utilizaremos el metodo de la clase consultarCoordenadas
	 * llamado obtenerCentroSeccion().
	 * Este metodo consulta el shapefile y devuelve latitud y longitud del centroide de la
	 * seccion censal.Despues almacenaremos esos datos en los campos latitud_centroide_seccion y longitud_centroide_seccion
	 * de la tabla Secciones
	 */
	public static void almacenarCentroidesSecciones(Connection conn) {
		Statement st = null;
		ResultSet rs = null;
		HashMap<String,double[]> mapa = new HashMap<String,double[]>();
		String consulta = "SELECT id_seccion from secciones;";

		try {
			st = conn.createStatement();
			rs = st.executeQuery(consulta);

			while(rs.next()) {
				String id_seccion = rs.getString(1);
				Point centroide = ShapefileUtils.obtenerCentroSeccion(id_seccion);
				if(centroide != null) {
					System.out.println(id_seccion);
					double[] coordenadas = {centroide.getY(),centroide.getX()};
					mapa.put(id_seccion,coordenadas);
				}

			}
			System.out.println("Mapa con los id_seccion creado.");
			
			String updateQuery = "UPDATE secciones SET latitud_centroide_seccion = ?, longitud_centroide_seccion = ? WHERE id_seccion = ?";
	        try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
	            for (Map.Entry<String, double[]> entry : mapa.entrySet()) {
	                String idSeccion = entry.getKey();
	                double[] coordenadas = entry.getValue();

	                ps.setDouble(1, coordenadas[0]);
	                ps.setDouble(2, coordenadas[1]);
	                ps.setString(3, idSeccion);
	                ps.addBatch();	                
	            }
	            ps.executeBatch();
	        }
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
	            if (rs != null) 
	            	rs.close();
	            if (st != null) 
	            	st.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		}

	}

	public static void main (String[] args) throws Exception {

		// hace que se cree una conexion con la bbdd
		BBDDPrincipal ini = new BBDDPrincipal();

		almacenarCentroidesSecciones(ini.getConnection());
		
//		Point punto = consultarCoordenadas.obtenerCentroSeccion("0105901039");
//		ArrayList<SimpleFeature> lista = consultarCoordenadas.obtenerSeccionesCensalesPorMunicipio("01059");
//		for(int i = 0; i < lista.size(); i++) {
//			String cusec = lista.get(i).getAttribute("CUSEC").toString();
//			System.out.println(cusec);
//		}


//		ArrayList<String> listaMunicipios = obtenerTodosLosIdMunicipio(ini.getConnection());

//		for(String municipio : listaMunicipios) {
//			ArrayList<SimpleFeature> listaSecciones = consultarCoordenadas.obtenerSeccionesCensalesPorMunicipio(municipio);
//			for (SimpleFeature seccion : listaSecciones) {
//	            String cusec = seccion.getAttribute("CUSEC").toString();
//	            System.out.println(cusec);
//	        }
//		}

//		ArrayList<SimpleFeature> listaSecciones = consultarCoordenadas.obtenerSeccionesCensalesPorMunicipio("28127");
//		for (SimpleFeature seccion : listaSecciones) {
//            String cusec = seccion.getAttribute("CUSEC").toString();
//            System.out.println(cusec);
//       }
//		
//almacenarCP(ini.getConnection());
//almacenarCodigosPostalesEnCSV(ini.getConnection());

//		ArrayList<String> listaIdMunicipios = obtenerTodosLosIdMunicipio(ini.getConnection());
//		System.out.println(listaIdMunicipios.size());
//
//		
//		ArrayList<SimpleFeature> res =consultarCoordenadas.obtenerSeccionesCensalesPorMunicipio("28127");
//		System.out.println(res.size());

	}
}
