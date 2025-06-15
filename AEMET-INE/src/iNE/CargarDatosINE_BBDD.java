package iNE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import bbdd.DatabaseConnection;

public class CargarDatosINE_BBDD {

	/*
	 * Esta será la clase principal para cargar los Excel con los datos del INE en la base de datos
	 * 
	 */
	private static final String path_directorio = "C:/Users/Pablo Guerrero/eclipse-workspace/AEMET/INE";
	private static Connection conn;
	private static ArrayList<String> subdirectorios;

	// Constructor
	public CargarDatosINE_BBDD() {
		try {
			// Iniciar la conexión a la base de datos
			conn = DatabaseConnection.getConnection();
			// Obtener la lista de subdirectorios
			subdirectorios = UtilsDirectorios.listarSubdirectorios(path_directorio);
		} catch (Exception e) {
			System.err.println("Error al listar subdirectorios: " + e.getMessage());
		}
	}

	public Connection getConnection() {
		return conn;
	}
	public ArrayList<String> getSubdirectorios() {
		return subdirectorios;
	}

	/*
	 * Función para insertar los valores de renta media y mediana en la bbdd para la hoja "sheet"
	 */
	private static void procesarIndicadoresRentaMedia(Sheet sheet) throws SQLException {
		//Para cada hoja recorremos cada fila
		for (int rowIndex = 8; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row == null) continue;

			ArrayList<String> nombreFila = new ArrayList<String>();
			int[] valoresRenta = new int[6];
			int i = -1; // Contador inicializado a -1 porque siempre habrá 1 nombre + 6 datos

			//Recorremos cada celda y asignamos el valor
			for (Cell cell : row) {
				switch (cell.getCellType()) {
				case STRING:
					if(!cell.toString().equals(".") && !(cell.getCellType() == CellType.BLANK)) {
						String[] partes = cell.getStringCellValue().split(" ", 2);

						if(!partes[0].matches("\\d+")) break; // Si la columna A no empieza por numero no nos interesa
						nombreFila = LeerExcel.separarNombreColumnaA(partes);
						System.out.print(nombreFila + "  ");
					}
					else {
						System.out.print(0);	
					}
					break;
				case NUMERIC:
					valoresRenta[i] = (int) cell.getNumericCellValue();
					System.out.print((int) cell.getNumericCellValue() + "\t");
					break;
				default:
					System.err.print("ERROR: Encontrado un tipo desconocido\t");
					break;
				}
				i++;
			}
			//Aqui tengo que almacenar los datos de la celda en función del nombre obtenido.
			//Tengo el nombre en nombreFila y los datos en valoresRenta[]

			int tipo = LeerExcel.tipoDelNombre(nombreFila);
			// Una vez obtenido el tipo del nombre, llamamos a una función para almacenar los datos
			switch(tipo) {
			case 0: //Municipio
				String id_municipio = nombreFila.get(3) + nombreFila.get(4);
				String nombre_mun = nombreFila.get(0);
				insertarOActualizarMunicipioRenta(conn, id_municipio, nombre_mun, valoresRenta[0], valoresRenta[1], valoresRenta[2], valoresRenta[3], valoresRenta[4], valoresRenta[5]);
				break;
			case 1: //Distrito
				String id_distrito = nombreFila.get(3) + nombreFila.get(4) + nombreFila.get(5);
				String id_municipio_dist = nombreFila.get(3) + nombreFila.get(4);
				String nombre_dist = nombreFila.get(0) + " " + nombreFila.get(1);
				insertarOActualizarDistritoRenta(conn, id_distrito, id_municipio_dist, nombre_dist, valoresRenta[0], valoresRenta[1], valoresRenta[2], valoresRenta[3], valoresRenta[4], valoresRenta[5]);
				break;
			case 2: //Sección
				String id_seccion = nombreFila.get(3) + nombreFila.get(4) + nombreFila.get(5) + nombreFila.get(6);
				String id_distrito_secc = nombreFila.get(3) + nombreFila.get(4) + nombreFila.get(5);
				String nombre_secc = nombreFila.get(0) + " " + nombreFila.get(2);
				insertarOActualizarSeccionRenta(conn, id_seccion, id_distrito_secc, nombre_secc, valoresRenta[0], valoresRenta[1], valoresRenta[2], valoresRenta[3], valoresRenta[4], valoresRenta[5]);
				break;
			default:
				System.err.println("ERROR, NO SE HA CONSEGUIDO IDENTIFICAR EL TIPO DEL NOMBRE DE LONGITUD:" + tipo);
			}

			System.out.println();
		}
	}

	private static void insertarOActualizarMunicipioRenta(Connection connection, String idMunicipio, String nombreMunicipio, int rentaNetaMediaPersona, int rentaNetaMediaHogar, int rentaUnidadConsumo, int medianaRentaConsumo, int rentaBrutaMediaPersona, int rentaBrutaMediaHogar) throws SQLException {
		String query = "INSERT INTO Municipios (id_municipio, nombre_municipio, renta_neta_media_persona, renta_neta_media_hogar, renta_unidad_consumo, mediana_renta_consumo, renta_bruta_media_persona, renta_bruta_media_hogar) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"nombre_municipio = VALUES(nombre_municipio), " +
				"renta_neta_media_persona = VALUES(renta_neta_media_persona), " +
				"renta_neta_media_hogar = VALUES(renta_neta_media_hogar), " +
				"renta_unidad_consumo = VALUES(renta_unidad_consumo), " +
				"mediana_renta_consumo = VALUES(mediana_renta_consumo), " +
				"renta_bruta_media_persona = VALUES(renta_bruta_media_persona), " +
				"renta_bruta_media_hogar = VALUES(renta_bruta_media_hogar)";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, idMunicipio);
			statement.setString(2, nombreMunicipio);
			statement.setInt(3, rentaNetaMediaPersona);
			statement.setInt(4, rentaNetaMediaHogar);
			statement.setInt(5, rentaUnidadConsumo);
			statement.setInt(6, medianaRentaConsumo);
			statement.setInt(7, rentaBrutaMediaPersona);
			statement.setInt(8, rentaBrutaMediaHogar);

			statement.executeUpdate();
		}
	}

	private static void insertarOActualizarDistritoRenta(Connection connection, String idDistrito, String idMunicipio, String nombreDistrito, int rentaNetaMediaPersona, int rentaNetaMediaHogar, int rentaUnidadConsumo, int medianaRentaConsumo, int rentaBrutaMediaPersona, int rentaBrutaMediaHogar) throws SQLException {
		String query = "INSERT INTO Distritos (id_distrito, id_municipio, nombre_distrito, renta_neta_media_persona, renta_neta_media_hogar, renta_unidad_consumo, mediana_renta_consumo, renta_bruta_media_persona, renta_bruta_media_hogar) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"id_municipio = VALUES(id_municipio), " +
				"nombre_distrito = VALUES(nombre_distrito), " +
				"renta_neta_media_persona = VALUES(renta_neta_media_persona), " +
				"renta_neta_media_hogar = VALUES(renta_neta_media_hogar), " +
				"renta_unidad_consumo = VALUES(renta_unidad_consumo), " +
				"mediana_renta_consumo = VALUES(mediana_renta_consumo), " +
				"renta_bruta_media_persona = VALUES(renta_bruta_media_persona), " +
				"renta_bruta_media_hogar = VALUES(renta_bruta_media_hogar)";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, idDistrito);
			statement.setString(2, idMunicipio);
			statement.setString(3, nombreDistrito);
			statement.setInt(4, rentaNetaMediaPersona);
			statement.setInt(5, rentaNetaMediaHogar);
			statement.setInt(6, rentaUnidadConsumo);
			statement.setInt(7, medianaRentaConsumo);
			statement.setInt(8, rentaBrutaMediaPersona);
			statement.setInt(9, rentaBrutaMediaHogar);

			statement.executeUpdate();
		}
	}

	private static void insertarOActualizarSeccionRenta(Connection connection, String idSeccion, String idDistrito, String nombreSeccion, int rentaNetaMediaPersona, int rentaNetaMediaHogar, int rentaUnidadConsumo, int medianaRentaConsumo, int rentaBrutaMediaPersona, int rentaBrutaMediaHogar) throws SQLException {
		String query = "INSERT INTO Secciones (id_seccion, id_distrito, nombre_seccion, renta_neta_media_persona, renta_neta_media_hogar, renta_unidad_consumo, mediana_renta_consumo, renta_bruta_media_persona, renta_bruta_media_hogar) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"id_distrito = VALUES(id_distrito), " +
				"nombre_seccion = VALUES(nombre_seccion), " +
				"renta_neta_media_persona = VALUES(renta_neta_media_persona), " +
				"renta_neta_media_hogar = VALUES(renta_neta_media_hogar), " +
				"renta_unidad_consumo = VALUES(renta_unidad_consumo), " +
				"mediana_renta_consumo = VALUES(mediana_renta_consumo), " +
				"renta_bruta_media_persona = VALUES(renta_bruta_media_persona), " +
				"renta_bruta_media_hogar = VALUES(renta_bruta_media_hogar)";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, idSeccion);
			statement.setString(2, idDistrito);
			statement.setString(3, nombreSeccion);
			statement.setInt(4, rentaNetaMediaPersona);
			statement.setInt(5, rentaNetaMediaHogar);
			statement.setInt(6, rentaUnidadConsumo);
			statement.setInt(7, medianaRentaConsumo);
			statement.setInt(8, rentaBrutaMediaPersona);
			statement.setInt(9, rentaBrutaMediaHogar);

			statement.executeUpdate();
		}
	}



	/*
	 * Función para insertar la distribución por fuente de ingresos en la bbdd para la hoja "sheet"
	 */
	private static void procesarDistribucionPorFuenteIngresos(Sheet sheet) throws SQLException {
		//Para cada hoja recorremos cada fila
		for (int rowIndex = 8; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row == null) continue;

			ArrayList<String> nombreFila = new ArrayList<String>();
			int[] valoresRenta = new int[6];
			int i = -1; // Contador inicializado a -1 porque siempre habrá 1 nombre + 6 datos

			//Recorremos cada celda y asignamos el valor
			for (Cell cell : row) {
				switch (cell.getCellType()) {
				case STRING:
					if(!cell.toString().equals(".") && !(cell.getCellType() == CellType.BLANK)) {
						String[] partes = cell.getStringCellValue().split(" ", 2);

						if(!partes[0].matches("\\d+")) break; // Si la columna A no empieza por numero no nos interesa
						nombreFila = LeerExcel.separarNombreColumnaA(partes);
						System.out.print(nombreFila + "  ");
					}
					else {
						System.out.print(0);	
					}
					break;
				case NUMERIC:
					valoresRenta[i] = (int) cell.getNumericCellValue();
					System.out.print((int) cell.getNumericCellValue() + "\t");
					break;
				default:
					System.err.print("ERROR: Encontrado un tipo desconocido\t");
					break;
				}
				i++;
			}
			//Aqui tengo que almacenar los datos de la celda en función del nombre obtenido.
			//Tengo el nombre en nombreFila y los datos en valoresRenta[]

			int tipo = LeerExcel.tipoDelNombre(nombreFila);
			// Una vez obtenido el tipo del nombre, llamamos a una función para almacenar los datos
			switch(tipo) {
			case 0: //Municipio
				String id_municipio = nombreFila.get(3) + nombreFila.get(4);
				String nombre_mun = nombreFila.get(0);
				insertarOActualizarMunicipioFuenteIngresos(conn, id_municipio, nombre_mun, valoresRenta[1], valoresRenta[2], valoresRenta[3], valoresRenta[4], valoresRenta[5]);
				break;
			case 1: //Distrito
				String id_distrito = nombreFila.get(3) + nombreFila.get(4) + nombreFila.get(5);
				String id_municipio_dist = nombreFila.get(3) + nombreFila.get(4);
				String nombre_dist = nombreFila.get(0) + " " + nombreFila.get(1);
				insertarOActualizarDistritoFuenteIngresos(conn, id_distrito, id_municipio_dist, nombre_dist, valoresRenta[1], valoresRenta[2], valoresRenta[3], valoresRenta[4], valoresRenta[5]);
				break;
			case 2: //Sección
				String id_seccion = nombreFila.get(3) + nombreFila.get(4) + nombreFila.get(5) + nombreFila.get(6);
				String id_distrito_secc = nombreFila.get(3) + nombreFila.get(4) + nombreFila.get(5);
				String nombre_secc = nombreFila.get(0) + " " + nombreFila.get(2);
				insertarOActualizarSeccionFuenteIngresos(conn, id_seccion, id_distrito_secc, nombre_secc, valoresRenta[1], valoresRenta[2], valoresRenta[3], valoresRenta[4], valoresRenta[5]);
				break;
			default:
				System.err.println("ERROR, NO SE HA CONSEGUIDO IDENTIFICAR EL TIPO DEL NOMBRE DE LONGITUD:" + tipo);
			}

			System.out.println();
		}
	}

	private static void insertarOActualizarMunicipioFuenteIngresos(Connection connection, String idMunicipio, String nombreMunicipio, int fuenteIngresosSalario, int fuenteIngresosPensiones, int fuenteIngresosPDesempleado, int fuenteIngresosOtrasPrestaciones, int fuenteIngresosOtrosIngresos) throws SQLException {
		String query = "INSERT INTO Municipios (id_municipio, nombre_municipio, fuente_ingresos_salario, fuente_ingresos_pensiones, fuente_ingresos_pdesempleado, fuente_ingresos_otrprestaciones, fuente_ingresos_otringresos) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"nombre_municipio = VALUES(nombre_municipio), " +
				"fuente_ingresos_salario = VALUES(fuente_ingresos_salario), " +
				"fuente_ingresos_pensiones = VALUES(fuente_ingresos_pensiones), " +
				"fuente_ingresos_pdesempleado = VALUES(fuente_ingresos_pdesempleado), " +
				"fuente_ingresos_otrprestaciones = VALUES(fuente_ingresos_otrprestaciones), " +
				"fuente_ingresos_otringresos = VALUES(fuente_ingresos_otringresos)";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, idMunicipio);
			statement.setString(2, nombreMunicipio);
			statement.setInt(3, fuenteIngresosSalario);
			statement.setInt(4, fuenteIngresosPensiones);
			statement.setInt(5, fuenteIngresosPDesempleado);
			statement.setInt(6, fuenteIngresosOtrasPrestaciones);
			statement.setInt(7, fuenteIngresosOtrosIngresos);
			statement.executeUpdate();
		}
	}

	private static void insertarOActualizarDistritoFuenteIngresos(Connection connection, String idDistrito, String idMunicipio, String nombreDistrito, int fuenteIngresosSalario, int fuenteIngresosPensiones, int fuenteIngresosPDesempleado, int fuenteIngresosOtrasPrestaciones, int fuenteIngresosOtrosIngresos) throws SQLException {
		String query = "INSERT INTO Distritos (id_distrito, id_municipio, nombre_distrito, fuente_ingresos_salario, fuente_ingresos_pensiones, fuente_ingresos_pdesempleado, fuente_ingresos_otrprestaciones, fuente_ingresos_otringresos) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"id_municipio = VALUES(id_municipio), " +
				"nombre_distrito = VALUES(nombre_distrito), " +
				"fuente_ingresos_salario = VALUES(fuente_ingresos_salario), " +
				"fuente_ingresos_pensiones = VALUES(fuente_ingresos_pensiones), " +
				"fuente_ingresos_pdesempleado = VALUES(fuente_ingresos_pdesempleado), " +
				"fuente_ingresos_otrprestaciones = VALUES(fuente_ingresos_otrprestaciones), " +
				"fuente_ingresos_otringresos = VALUES(fuente_ingresos_otringresos)";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, idDistrito);
			statement.setString(2, idMunicipio);
			statement.setString(3, nombreDistrito);
			statement.setInt(4, fuenteIngresosSalario);
			statement.setInt(5, fuenteIngresosPensiones);
			statement.setInt(6, fuenteIngresosPDesempleado);
			statement.setInt(7, fuenteIngresosOtrasPrestaciones);
			statement.setInt(8, fuenteIngresosOtrosIngresos);
			statement.executeUpdate();
		}
	}

	private static void insertarOActualizarSeccionFuenteIngresos(Connection connection, String idSeccion, String idDistrito, String nombreSeccion, int fuenteIngresosSalario, int fuenteIngresosPensiones, int fuenteIngresosPDesempleado, int fuenteIngresosOtrasPrestaciones, int fuenteIngresosOtrosIngresos) throws SQLException {
		String query = "INSERT INTO Secciones (id_seccion, id_distrito, nombre_seccion, fuente_ingresos_salario, fuente_ingresos_pensiones, fuente_ingresos_pdesempleado, fuente_ingresos_otrprestaciones, fuente_ingresos_otringresos) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"id_distrito = VALUES(id_distrito), " +
				"nombre_seccion = VALUES(nombre_seccion), " +
				"fuente_ingresos_salario = VALUES(fuente_ingresos_salario), " +
				"fuente_ingresos_pensiones = VALUES(fuente_ingresos_pensiones), " +
				"fuente_ingresos_pdesempleado = VALUES(fuente_ingresos_pdesempleado), " +
				"fuente_ingresos_otrprestaciones = VALUES(fuente_ingresos_otrprestaciones), " +
				"fuente_ingresos_otringresos = VALUES(fuente_ingresos_otringresos)";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, idSeccion);
			statement.setString(2, idDistrito);
			statement.setString(3, nombreSeccion);
			statement.setInt(4, fuenteIngresosSalario);
			statement.setInt(5, fuenteIngresosPensiones);
			statement.setInt(6, fuenteIngresosPDesempleado);
			statement.setInt(7, fuenteIngresosOtrasPrestaciones);
			statement.setInt(8, fuenteIngresosOtrosIngresos);
			statement.executeUpdate();
		}
	}


	/*
	 * Función para insertar los indicadores demográficos en la bbdd para la hoja "sheet"
	 */
	private static void procesarIndicadoresDemograficos(Sheet sheet) throws SQLException {
		//Para cada hoja recorremos cada fila
		for (int rowIndex = 8; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row == null) continue;

			ArrayList<String> nombreFila = new ArrayList<String>();
			double[] valoresRenta = new double[7];
			int i = -1; // Contador inicializado a -1 porque siempre habrá 1 nombre + 6 datos

			//Recorremos cada celda y asignamos el valor
			for (Cell cell : row) {
				switch (cell.getCellType()) {
				case STRING:
					if(!cell.toString().equals(".") && !(cell.getCellType() == CellType.BLANK)) {
						String[] partes = cell.getStringCellValue().split(" ", 2);

						if(!partes[0].matches("\\d+")) break; // Si la columna A no empieza por numero no nos interesa
						nombreFila = LeerExcel.separarNombreColumnaA(partes);
						System.out.print(nombreFila + "  ");
					}
					else {
						System.out.print(0);	
					}
					break;
				case NUMERIC:
					valoresRenta[i] =  cell.getNumericCellValue();
					System.out.print((double)cell.getNumericCellValue() + "\t");
					break;
				default:
					System.err.print("ERROR: Encontrado un tipo desconocido\t");
					break;
				}
				i++;
			}
			//Aqui tengo que almacenar los datos de la celda en función del nombre obtenido.
			//Tengo el nombre en nombreFila y los datos en valoresRenta[]

			int tipo = LeerExcel.tipoDelNombre(nombreFila);
			// Una vez obtenido el tipo del nombre, llamamos a una función para almacenar los datos
			switch(tipo) {
			case 0: //Municipio
				String id_municipio = nombreFila.get(3) + nombreFila.get(4);
				String nombre_mun = nombreFila.get(0);
				insertarOActualizarMunicipioIndicadores(conn, id_municipio, nombre_mun,valoresRenta[0], valoresRenta[1], valoresRenta[2], valoresRenta[3], valoresRenta[4],(int)valoresRenta[5],valoresRenta[6]);
				break;
			case 1: //Distrito
				String id_distrito = nombreFila.get(3) + nombreFila.get(4) + nombreFila.get(5);
				String id_municipio_dist = nombreFila.get(3) + nombreFila.get(4);
				String nombre_dist = nombreFila.get(0) + " " + nombreFila.get(1);
				insertarOActualizarDistritoIndicadores(conn,id_distrito, id_municipio_dist, nombre_dist, valoresRenta[0], valoresRenta[1], valoresRenta[2], valoresRenta[3], valoresRenta[4],(int)valoresRenta[5],valoresRenta[6]);
				break;
			case 2: //Sección
				String id_seccion = nombreFila.get(3) + nombreFila.get(4) + nombreFila.get(5) + nombreFila.get(6);
				String id_distrito_secc = nombreFila.get(3) + nombreFila.get(4) + nombreFila.get(5);
				String nombre_secc = nombreFila.get(0) + " " + nombreFila.get(2);
				insertarOActualizarSeccionIndicadores(conn, id_seccion, id_distrito_secc, nombre_secc, valoresRenta[0], valoresRenta[1], valoresRenta[2], valoresRenta[3], valoresRenta[4], (int)valoresRenta[5], valoresRenta[6]);
				break;
			default:
				System.err.println("ERROR, NO SE HA CONSEGUIDO IDENTIFICAR EL TIPO DEL NOMBRE DE LONGITUD:" + tipo);
			}

			System.out.println();
		}

	}

	private static void insertarOActualizarMunicipioIndicadores(Connection connection, String idMunicipio, String nombreMunicipio, 
			double edadMediaPoblacion, double porcentajeMenor18, double porcentajeMayor65, double tamanoMedioHogar, 
			double porcentajeHogaresUnipersonales, int poblacion, double porcentajePoblacionEspanola) throws SQLException {

		String query = "INSERT INTO Municipios (id_municipio, nombre_municipio, edad_media_poblacion, porcentaje_menor_18, " +
				"porcentaje_mayor_65, tamaño_medio_hogar, porcentaje_hogares_unipersonales, poblacion, porcentaje_poblacion_española) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"nombre_municipio = VALUES(nombre_municipio), " +
				"edad_media_poblacion = VALUES(edad_media_poblacion), " +
				"porcentaje_menor_18 = VALUES(porcentaje_menor_18), " +
				"porcentaje_mayor_65 = VALUES(porcentaje_mayor_65), " +
				"tamaño_medio_hogar = VALUES(tamaño_medio_hogar), " +
				"porcentaje_hogares_unipersonales = VALUES(porcentaje_hogares_unipersonales), " +
				"poblacion = VALUES(poblacion), " +
				"porcentaje_poblacion_española = VALUES(porcentaje_poblacion_española)";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, idMunicipio);
			statement.setString(2, nombreMunicipio);
			statement.setDouble(3, edadMediaPoblacion);
			statement.setDouble(4, porcentajeMenor18);
			statement.setDouble(5, porcentajeMayor65);
			statement.setDouble(6, tamanoMedioHogar);
			statement.setDouble(7, porcentajeHogaresUnipersonales);
			statement.setInt(8, poblacion);
			statement.setDouble(9, porcentajePoblacionEspanola);

			statement.executeUpdate();
		}
	}

	private static void insertarOActualizarDistritoIndicadores(Connection connection, String idDistrito, String idMunicipio, 
			String nombreDistrito, double edadMediaPoblacion, double porcentajeMenor18, double porcentajeMayor65, double tamanoMedioHogar, 
			double porcentajeHogaresUnipersonales, int poblacion, double porcentajePoblacionEspanola) throws SQLException {

		String query = "INSERT INTO Distritos (id_distrito, id_municipio, nombre_distrito, edad_media_poblacion, porcentaje_menor_18, " +
				"porcentaje_mayor_65, tamaño_medio_hogar, porcentaje_hogares_unipersonales, poblacion, porcentaje_poblacion_española) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"id_municipio = VALUES(id_municipio), " +
				"nombre_distrito = VALUES(nombre_distrito), " +
				"edad_media_poblacion = VALUES(edad_media_poblacion), " +
				"porcentaje_menor_18 = VALUES(porcentaje_menor_18), " +
				"porcentaje_mayor_65 = VALUES(porcentaje_mayor_65), " +
				"tamaño_medio_hogar = VALUES(tamaño_medio_hogar), " +
				"porcentaje_hogares_unipersonales = VALUES(porcentaje_hogares_unipersonales), " +
				"poblacion = VALUES(poblacion), " +
				"porcentaje_poblacion_española = VALUES(porcentaje_poblacion_española)";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, idDistrito);
			statement.setString(2, idMunicipio);
			statement.setString(3, nombreDistrito);
			statement.setDouble(4, edadMediaPoblacion);
			statement.setDouble(5, porcentajeMenor18);
			statement.setDouble(6, porcentajeMayor65);
			statement.setDouble(7, tamanoMedioHogar);
			statement.setDouble(8, porcentajeHogaresUnipersonales);
			statement.setInt(9, poblacion);
			statement.setDouble(10, porcentajePoblacionEspanola);

			statement.executeUpdate();
		}
	}

	private static void insertarOActualizarSeccionIndicadores(Connection connection, String idSeccion, String idDistrito, 
			String nombreSeccion, double edadMediaPoblacion, double porcentajeMenor18, double porcentajeMayor65, double tamanoMedioHogar, 
			double porcentajeHogaresUnipersonales, int poblacion, double porcentajePoblacionEspanola) throws SQLException {

		String query = "INSERT INTO Secciones (id_seccion, id_distrito, nombre_seccion, edad_media_poblacion, porcentaje_menor_18, " +
				"porcentaje_mayor_65, tamaño_medio_hogar, porcentaje_hogares_unipersonales, poblacion, porcentaje_poblacion_española) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE " +
				"id_distrito = VALUES(id_distrito), " +
				"nombre_seccion = VALUES(nombre_seccion), " +
				"edad_media_poblacion = VALUES(edad_media_poblacion), " +
				"porcentaje_menor_18 = VALUES(porcentaje_menor_18), " +
				"porcentaje_mayor_65 = VALUES(porcentaje_mayor_65), " +
				"tamaño_medio_hogar = VALUES(tamaño_medio_hogar), " +
				"porcentaje_hogares_unipersonales = VALUES(porcentaje_hogares_unipersonales), " +
				"poblacion = VALUES(poblacion), " +
				"porcentaje_poblacion_española = VALUES(porcentaje_poblacion_española)";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, idSeccion);
			statement.setString(2, idDistrito);
			statement.setString(3, nombreSeccion);
			statement.setDouble(4, edadMediaPoblacion);
			statement.setDouble(5, porcentajeMenor18);
			statement.setDouble(6, porcentajeMayor65);
			statement.setDouble(7, tamanoMedioHogar);
			statement.setDouble(8, porcentajeHogaresUnipersonales);
			statement.setInt(9, poblacion);
			statement.setDouble(10, porcentajePoblacionEspanola);

			statement.executeUpdate();
		}
	}


	/*
	 * Método que consulta el .txt de vías de España y obtiene para cada seccion censal su codigo postal y los almacena en un Excel
	 */
	public static void obtenerCPostalesSeccionesCensalesTxt() {
		String inputFile = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/datos cartografia caj_esp/TRAM.P01-52.D240630.G240703";
		String outputFile = "C:/Users/Pablo Guerrero/eclipse-workspace/AEMET/src/iNE/SeccionesCensales.xlsx";

		// Usamos LinkedHashMap para almacenar los datos únicos y conservar el orden
		Map<String, String> uniqueData = new LinkedHashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				// Dividir la línea en partes ignorando múltiples espacios
				String[] parts = line.trim().split("\\s+");
				if (parts.length < 3) continue;

				// Extraemos seccion censal y su codigo postal
				String sectionCensal = parts[0];
				String postalCode = parts[2].substring(0, 5);

				// Si no existe la sección censal, la añadimos
				uniqueData.putIfAbsent(sectionCensal, postalCode);
			}

			// Crear el archivo Excel
			try (Workbook workbook = new XSSFWorkbook()) {
				Sheet sheet = workbook.createSheet("Secciones Censales");

				// Crear encabezados
				Row headerRow = sheet.createRow(0);
				headerRow.createCell(0).setCellValue("Sección Censal");
				headerRow.createCell(1).setCellValue("Código Postal");

				// Agregar los datos
				int rowNum = 1;
				for (Map.Entry<String, String> entry : uniqueData.entrySet()) {
					Row row = sheet.createRow(rowNum++);
					row.createCell(0).setCellValue(entry.getKey());
					row.createCell(1).setCellValue(entry.getValue());
				}

				// Escribir el archivo Excel
				try (FileOutputStream fos = new FileOutputStream(outputFile)) {
					workbook.write(fos);
				}
			}

			System.out.println("Datos exportados a: " + outputFile);

		} catch (IOException e) {
			System.err.println("Error al procesar el archivo: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void almacenarCPostalesSeccionesCensalesBBDD(Connection conn) {
		String inputFile = "C:/Users/Pablo Guerrero/eclipse-workspace/AEMET/src/iNE/SeccionesCensales.xlsx";

		// Consulta SQL para actualizar el código postal si el id_seccion ya existe
	    String updateQuery = "UPDATE Secciones SET codigo_postal = ? WHERE id_seccion = ?";

	    try (FileInputStream fis = new FileInputStream(inputFile);
	         Workbook workbook = new XSSFWorkbook(fis);
	         PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

	        // Seleccionar la primera hoja del Excel
	        Sheet sheet = workbook.getSheetAt(0);

	        // Iterar sobre las filas, empezando desde la segunda fila (ignoramos encabezados)
	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	            Row row = sheet.getRow(i);
	            if (row == null) continue;

	            // Leer la columna "Sección Censal" (columna 0) y "Código Postal" (columna 1)
	            Cell sectionCensalCell = row.getCell(0);
	            Cell postalCodeCell = row.getCell(1);

	            if (sectionCensalCell == null || postalCodeCell == null) continue;

	            String sectionCensal = sectionCensalCell.getStringCellValue();
	            String postalCode = postalCodeCell.getStringCellValue();

	            // Actualizar en la base de datos
	            stmt.setString(1, postalCode);   // codigo_postal
	            stmt.setString(2, sectionCensal); // id_seccion
	            stmt.addBatch();
	        }

	        // Ejecutar el batch de actualizaciones
	        stmt.executeBatch();
	        System.out.println("Datos actualizados en la base de datos correctamente.");

	    } catch (Exception e) {
	        System.err.println("Error al leer el archivo Excel o actualizar en la base de datos: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	

	/*
	 * Método principal que contiene el codigo que se tiene que ejecutar para guardar todos los datos relativos al INE,
	 * incluidos los codigos postales de cada seccion censal (obtenidos de .txt en /datos cartografia caj_esp/TRAM.P01-52.D240630
	 * 
	 * Recorre todos los archivos guardados en la carpeta INE
	 * En esta carpeta se encuentran 3 excel para cada uno de los municipios españoles(52)
	 * Estos excel contienen los datos sociodemograficos descargados del INE.
	 * 
	 * Este metodo recorrera cada uno de esos archivos e ira actualizando la base de datos a nivel de municipio, distrito y seccion censal
	 * Por ultimo se introduciran los codigos postales obtenidos por otra via, explicada al principio de este comentario
	 */
	public static void principal() {
		//Creo la conexion y obtengo la lista de sd
		CargarDatosINE_BBDD ini = new CargarDatosINE_BBDD();
		ArrayList<String> sd = ini.getSubdirectorios();
		//Para cada sd, obtengo la lista de los archivos que hay dentro
		//Cargo todos los archivos en la bbdd
		for(String nombreSD : sd) {
			ArrayList<String> archivos = UtilsDirectorios.listarArchivos(path_directorio + "/" + nombreSD);

			//Para cada archivo excel, leo y almaceno en la bbdd
			Workbook workbook = null;
			for(String nombreArchivo : archivos) {
				try {
					FileInputStream file = new FileInputStream(new File(path_directorio + "/" + nombreSD + "/" + nombreArchivo));
					workbook = new XSSFWorkbook(file);
					// Selecciona la primera hoja del archivo Excel
					Sheet sheet = workbook.getSheetAt(0);
					//Obtengo el tipo de archivo (indicadores de renta media y mediana, distribución por fuente de ingresos o indicadores demograficos),
					//En función del tipo cargaré unos datos u otros
					String tipoExcel = LeerExcel.obtenerNombreTipoExcel(sheet);
					switch(tipoExcel) {

					case "Indicadores de renta media y mediana":
						procesarIndicadoresRentaMedia(sheet);
						break;
					case "Distribución por fuente de ingresos":
						procesarDistribucionPorFuenteIngresos(sheet);
						break;
					case "Indicadores demográficos":
						procesarIndicadoresDemograficos(sheet);
						break;
					default:
						break;
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if(workbook != null) {
						try {
							workbook.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
			//Una vez guardados todos los datos del ine, procedemos a guardar todos los codigos postales
			//Primero se creará el Excel con los datos y después se consultara de cada seccion censal su codigo postal
			obtenerCPostalesSeccionesCensalesTxt();
			almacenarCPostalesSeccionesCensalesBBDD(ini.getConnection());
			
		}
	}

	public static void main (String [] args) {
		
		principal();
		
		
		Boolean cargarCPPrueba = false;
		if(cargarCPPrueba) {
			CargarDatosINE_BBDD ini = new CargarDatosINE_BBDD(); 
			obtenerCPostalesSeccionesCensalesTxt();
			almacenarCPostalesSeccionesCensalesBBDD(ini.getConnection());
		}
	}
}
