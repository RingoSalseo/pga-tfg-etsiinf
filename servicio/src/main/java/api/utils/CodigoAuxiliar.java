package api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import api.model.Estacion;

@Component
public class CodigoAuxiliar {	

	public CodigoAuxiliar() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * RUTA AL SHAPEFILE QUE CONTIENE LOS DATOS GEOGRAFICOS DE ESPAÑA
	 */
	public static String rutaShapefile = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/cartografia_censo2011_nacional/SECC_CPV_E_20111101_01_R_INE.shp";


	/**
	 * Método para obtener el CUSEC (Código Único de Sección Censal) y las coordenadas
	 * del centroide de la sección censal a la que pertenece un punto (latitud y longitud).
	 *
	 * @param latitud La latitud del punto.
	 * @param longitud La longitud del punto.
	 * @return resultado Un array de dos objetos que contiene el CUSEC y las coordenadas del centroide de la sección como un objeto Point.
	 *         Si el punto no pertenece a ninguna sección, el arreglo contendrá valores nulos.
	 * @throws Exception Si ocurre algún error al procesar el archivo shapefile de cartografía.
	 * 
	 * @author Pablo Guerrero Álvarez b190292. ETSIINF
	 * 
	 */
	public static Object[] obtenerCusecYCoordenadasSeccion(double latitud, double longitud) throws Exception {
		Object[] resultado = new Object[2];
		File file = new File(rutaShapefile);
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();

		GeometryFactory geometryFactory = new GeometryFactory();
		Point point = geometryFactory.createPoint(new Coordinate(longitud, latitud));

		point = transformPointToShapefileCRS(point,featureSource);

		// Iterar sobre las features (secciones) del shapefile para verificar si el punto está dentro de alguna
		FeatureIterator<SimpleFeature> features = featureSource.getFeatures().features();
		boolean found = false;
		while (features.hasNext()) {
			SimpleFeature feature = features.next();
			Geometry geometry = (Geometry) feature.getDefaultGeometry();


			if (geometry.contains(point)) {
				found = true;
				System.out.println(LocalTime.now().withNano(0) + " El punto pertenece a la sección: " + feature.getID());
				for(Property attribute : feature.getProperties()) {
					System.out.println(attribute.getName() + " " +  attribute.getValue());					
				}
				System.out.println(LocalTime.now().withNano(0) + " CENTROIDE: " + geometry.getCentroid());
				//El id_seccion
				resultado[0] = feature.getAttribute("CUSEC").toString();
				//El centroide
				resultado[1] = obtenerCentroSeccion(feature.getAttribute("CUSEC").toString());
				break;
			}
		}
		if (!found) {
			System.out.println("El punto no pertenece a ninguna sección.");
		}

		features.close();
		store.dispose();
		return resultado;
	}

	/**
	 * Método que transforma un punto en coordenadas geográficas (WGS84) a las coordenadas
	 * del sistema de referencia espacial (CRS) del shapefile.
	 *
	 * @param point El punto en coordenadas geográficas (latitud y longitud) Objeto JTS.
	 * @param featureSource El objeto que representa las características del shapefile.
	 * @return Un objeto Point transformado al CRS del shapefile. (coordenadas de referencia)
	 * @throws Exception Si ocurre algún error al realizar la transformación de coordenadas.
	 * 
	 * @author Pablo Guerrero Álvarez b190292. ETSIINF
	 */
	public static Point transformPointToShapefileCRS(Point point, SimpleFeatureSource featureSource) throws Exception {
		// Obtener el sistema de referencia del shapefile
		CoordinateReferenceSystem shapefileCRS = featureSource.getSchema().getCoordinateReferenceSystem();

		// Obtener la transformación entre WGS84 (lat/long) y el CRS del shapefile
		MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, shapefileCRS, true);

		// Transformar el punto a las coordenadas del shapefile
		return (Point) JTS.transform(point, transform);
	}

	/**
	 * Método que obtiene el centroide de una sección censal a partir de su idSeccion.
	 * 
	 * @param idSeccion ID de la sección censal que se encuentra en el shapefile.
	 * @return puntoGeografico Punto geográfico (latitud y longitud) correspondiente al centroide de la sección (Point).
	 * @throws Exception Si ocurre un error al procesar el shapefile o al transformar las coordenadas.
	 * 
	 * @author: Pablo Guerrero Álvarez b190292. ETSIINF
	 */
	public static Point obtenerCentroSeccion(String idSeccion) throws Exception {
		// Cargar el shapefile
		File file = new File(rutaShapefile);
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();
		Point puntoGeografico = null;

		try (FeatureIterator<SimpleFeature> features = featureSource.getFeatures().features()) {
			while (features.hasNext()) {
				SimpleFeature feature = features.next();

				// Verificar si el atributo del ID de la sección censal coincide
				String seccionID = feature.getAttribute("CUSEC").toString();
				if (seccionID.equals(idSeccion)) {

					Geometry geometry = (Geometry) feature.getDefaultGeometry();
					Point centroide = geometry.getCentroid();

					CoordinateReferenceSystem shapefileCRS = featureSource.getSchema().getCoordinateReferenceSystem();
					MathTransform transform = CRS.findMathTransform(shapefileCRS, DefaultGeographicCRS.WGS84, true);
					puntoGeografico = (Point) JTS.transform(centroide, transform);
					break;
				}
			}
			features.close();
		}
		store.dispose();
		return puntoGeografico;
	}

	/**
	 * Método para calcular la distancia en kilómetros entre dos puntos geográficos
	 * utilizando la fórmula del haversine.
	 * 
	 * @param lat1 Latitud del primer punto.
	 * @param lon1 Longitud del primer punto.
	 * @param lat2 Latitud del segundo punto.
	 * @param lon2 Longitud del segundo punto.
	 * @return Distancia en kilómetros entre los dos puntos.
	 * 
	 * {@code} Pablo Guerrero Álvarez b190292. ETSIINF
	 * @author https://www.genbeta.com/desarrollo/como-calcular-la-distancia-entre-dos-puntos-geograficos-en-c-formula-de-haversine
	 */
	public static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
		final int R = 6371; // Radio de la Tierra en kilómetros
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c;
	}

	/**
	 * Método para obtener las mediciones medias de las dos estaciones más cercanas a una ubicación específica.
	 * 
	 * Este método busca todas las estaciones disponibles y selecciona las dos más cercanas al punto especificado por 
	 * las coordenadas dadas. A continuación, recupera las mediciones meteorológicas de ambas estaciones y calcula 
	 * las mediciones medias para estas. Si no se encuentran estaciones cercanas, se genera una excepción indicando 
	 * el error.
	 * 
	 * @param id_seccion Identificador de la sección en la que se está realizando la búsqueda.
	 * @param latitud Latitud del punto de referencia.
	 * @param longitud Longitud del punto de referencia.
	 * @return List<Medicion> Lista con las mediciones medias ordenadas cronológicamente por fecha.
	 * 
	 * @throws ResponseStatusException Si no se encuentran estaciones cercanas dentro del rango establecido.
	 * @author Pablo Guerrero Álvarez b190292. ETSIINF
	 */
	public static List<Estacion> obtenerDosEstacionesMasCercanas(double latitudCentroide, double longitudCentroide, List<Estacion> estaciones) {
		// Lista para almacenar las distancias
		List<EstacionDistancia> estacionesConDistancia = new ArrayList<>();
		int distanciaMaxima = 30;
		System.out.println(LocalTime.now().withNano(0) +  " CALCULANDO DISTANCIA DE LAS ESTACIONES A : (" + latitudCentroide + ", " + longitudCentroide + ")");
		// Calcular la distancia de cada estación al centroide
		for (Estacion estacion : estaciones) {
			double distancia = calcularDistancia(
					latitudCentroide,
					longitudCentroide,
					estacion.getLatitud(),
					estacion.getLongitud()
					);
			if (distancia <= distanciaMaxima) {
				estacionesConDistancia.add(new EstacionDistancia(estacion, distancia));
			}
		}
		estacionesConDistancia.sort(Comparator.comparingDouble(EstacionDistancia::getDistancia));
		List<Estacion> resultado = new ArrayList<>();

		if (estacionesConDistancia.isEmpty()) {
			// Si no hay estaciones a 30 km -> vacio
			return resultado;
		} else if (estacionesConDistancia.size() == 1) {
			// Si hay solo una estación a 30 km -> la ponemos dos veces
			resultado.add(estacionesConDistancia.get(0).getEstacion());
			resultado.add(estacionesConDistancia.get(0).getEstacion());
		} else {
			resultado.add(estacionesConDistancia.get(0).getEstacion());
			resultado.add(estacionesConDistancia.get(1).getEstacion());
		}

		return resultado;
	}

	/**
	 * Obtiene exactamente cuatro estaciones más cercanas al punto indicado por latitud y longitud,
	 * siempre que se encuentren a una distancia máxima de 70 km. Si hay menos de 4 estaciones dentro
	 * del radio, se repiten las más cercanas hasta completar el total.
	 *
	 * @param latitudCentroide   Latitud del punto de referencia.
	 * @param longitudCentroide  Longitud del punto de referencia.
	 * @param estaciones         Lista de estaciones disponibles.
	 * @return Lista con exactamente 4 estaciones (posiblemente con repeticiones).
	 * @author Pablo Guerrero Álvarez b190292. ETSIINF
	 */
	public static List<Estacion> obtenerCuatroEstacionesMasCercanas(double latitudCentroide, double longitudCentroide, List<Estacion> estaciones) {

		List<EstacionDistancia> estacionesConDistancia = new ArrayList<>();
		double distanciaMaxima = 70.0;

		for (Estacion estacion : estaciones) {
			double distancia = calcularDistancia(
					latitudCentroide,
					longitudCentroide,
					estacion.getLatitud(),
					estacion.getLongitud()
					);

			if (distancia <= distanciaMaxima) {
				estacionesConDistancia.add(new EstacionDistancia(estacion, distancia));
			}
		}

		// Ordenar las estaciones filtradas por distancia ascendente
		estacionesConDistancia.sort(Comparator.comparingDouble(EstacionDistancia::getDistancia));
		List<Estacion> resultado = new ArrayList<>();
		List<Estacion> estacionesFiltradas = new ArrayList<>();
		for (EstacionDistancia ed : estacionesConDistancia) {
			estacionesFiltradas.add(ed.getEstacion());
		}

		int total = estacionesFiltradas.size();
		if (total == 0) {
			return resultado;
		}

		for (int i = 0; i < 4; i++) {
			resultado.add(estacionesFiltradas.get(i % total));
		}

		return resultado;
	}

	/**
	 * Método para obtener las provincias por comunidad autónoma a partir de un archivo Excel sacado de la AEMET.
	 *
	 * @param codAuto El código de la comunidad autónoma.
	 * @return Un conjunto con los códigos de las provincias de esa comunidad.
	 */
	public static Set<String> obtenerProvinciasPorComunidad(String codAuto) {
		String ruta = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/AEMET/Códigos de municipio ESPAÑA.xlsx";
		Set<String> provincias = new HashSet<>();
		try (InputStream is = new FileInputStream(ruta)) {
			Workbook workbook = new XSSFWorkbook(is);
			Sheet sheet = workbook.getSheetAt(0);

			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue;

				Cell codAutoCell = row.getCell(0); // CODAUTO
				Cell cproCell = row.getCell(1);    // CPRO

				if (codAutoCell != null && cproCell != null) {
					String cod = codAutoCell.toString().replace(".0", "").trim();
					if (cod.equals(codAuto)) {
						String provincia = cproCell.toString().replace(".0", "").trim();
						provincias.add(provincia);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(provincias);
		return provincias;
	}


}

class EstacionDistancia {
	private final Estacion estacion;
	private final double distancia;

	public EstacionDistancia(Estacion estacion, double distancia) {
		this.estacion = estacion;
		this.distancia = distancia;
	}

	public Estacion getEstacion() {
		return estacion;
	}

	public double getDistancia() {
		return distancia;
	}
}
