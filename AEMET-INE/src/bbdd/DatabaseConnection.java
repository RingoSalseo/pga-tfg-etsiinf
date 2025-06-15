package bbdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/bbdd_tfg";
	private static final String USER = "root";
	private static final String PASSWORD = "root";
	private static Connection connection = null;

	public static Connection getConnection() {
		if (connection == null) {
			try {
				connection = DriverManager.getConnection(URL, USER, PASSWORD);
				System.out.println("Conexión a la base de datos exitosa.");
			} catch (SQLException e) {
				System.err.println("Error al conectar con la base de datos: " + e.getMessage());
			}
		}
		return connection;
	}

	public static void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
				System.out.println("Conexión a la base de datos cerrada.");
			} catch (SQLException e) {
				System.err.println("Error al cerrar la conexión de la base de datos: " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {

		/*
		 * PRUEBAS
		 */
		Boolean pruebas = false;
		if(pruebas) {
			// Obtener la conexión a la base de datos
			Connection conn = getConnection();

			if (conn != null) {
				String query = "SELECT * FROM Municipios";  // Consulta SQL para seleccionar todos los municipios
				try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

					// Iterar sobre el ResultSet y mostrar los datos
					while (rs.next()) {
						String idMunicipio = rs.getString("id_municipio");
						String nombreMunicipio = rs.getString("nombre_municipio");
						double rentaNetaMediaPersona = rs.getDouble("renta_neta_media_persona");
						double rentaNetaMediaHogar = rs.getDouble("renta_neta_media_hogar");
						double rentaUnidadConsumo = rs.getDouble("renta_unidad_consumo");
						double medianaRentaConsumo = rs.getDouble("mediana_renta_consumo");
						double rentaBrutaMediaPersona = rs.getDouble("renta_bruta_media_persona");
						double rentaBrutaMediaHogar = rs.getDouble("renta_bruta_media_hogar");

						System.out.printf("ID Municipio: %s, Nombre: %s, Renta Neta Media Persona: %.2f, Renta Neta Media Hogar: %.2f, " +
								"Renta Unidad Consumo: %.2f, Mediana Renta Consumo: %.2f, Renta Bruta Media Persona: %.2f, Renta Bruta Media Hogar: %.2f%n",
								idMunicipio, nombreMunicipio, rentaNetaMediaPersona, rentaNetaMediaHogar, 
								rentaUnidadConsumo, medianaRentaConsumo, rentaBrutaMediaPersona, rentaBrutaMediaHogar);
					}

				} catch (SQLException e) {
					System.err.println("Error al ejecutar la consulta: " + e.getMessage());
				} finally {
					// Cerrar la conexión
					closeConnection();
				}
			}
		}
	}
}

