package shapefile;

import java.io.File;
import java.util.ArrayList;
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

import cartociudad.ConsultasCartoCiudad;
import cartociudad.ObjetoCartoCiudad;
import cartociudad.ObjetoLocationUS;

public class ShapefileUtils {

	private static String rutaShapefile = "C:/Users/Pablo Guerrero/OneDrive - Universidad Politécnica de Madrid/Escritorio/TFG PABLO GUERRERO ALVAREZ/cartografia_censo2011_nacional/SECC_CPV_E_20111101_01_R_INE.shp";

	public static void main(String[] args) throws Exception {
		// Cargar el archivo shapefile

		//Pruebas para API CartoCiudad
		Boolean prueba1 = false;
		if(prueba1) {
			System.out.println("El numero de secciones censales en el shapefile es : " + contarSeccionesCensales());
			ArrayList<SimpleFeature> resultado = obtenerSeccionesCensalesPorMunicipio("28127");
			ObjetoCartoCiudad cartociudad;
			String codigoPostalPorDefecto = null;
			for(int i = 0; i < resultado.size(); i++) {
				String cusec = resultado.get(i).getAttribute("CUSEC").toString();
				Point punto = obtenerCentroSeccion(cusec);
				System.out.println(cusec + punto);
				cartociudad = ConsultasCartoCiudad.consultarCartoCiudad(punto.getY(),punto.getX());
				if(cartociudad == null) {
					System.out.println("cartociudad no ha encontrado una direccion para dichas coordenadas");
				}
				else {
					System.out.println("Codigo postal encontrado : " + cartociudad.getPostalCode());
				}
			}
			System.out.println("_________________________________________________________________________");
		}

		//Pruebas para API LocationUS

		Boolean prueba2 = false;
		if(prueba2) {
			System.out.println("El numero de secciones censales en el shapefile es : " + contarSeccionesCensales());
			ArrayList<SimpleFeature> resultado = obtenerSeccionesCensalesPorMunicipio("28127");
			ObjetoLocationUS locationUS;
			String codigoPostalPorDefecto = null;
			for(int i = 0; i < resultado.size(); i++) {
				String cusec = resultado.get(i).getAttribute("CUSEC").toString();
				Point punto = obtenerCentroSeccion(cusec);
				System.out.println(cusec + punto);
				locationUS = ConsultasCartoCiudad.consultarLocationUS(punto.getY(),punto.getX());
				if(locationUS == null) {
					System.out.println("cartociudad no ha encontrado una direccion para dichas coordenadas");
				}
				else {
					System.out.println("Codigo postal encontrado : " + locationUS.getAddress().getPostcode());
				}
			}
			System.out.println("_________________________________________________________________________");
		}

		Boolean prueba3 = false;
		if(prueba3) {
			File file = new File(rutaShapefile);
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();

			double longitud = -3.8995029645066284;
			double latitud = 40.541538246225144;

			GeometryFactory geometryFactory = new GeometryFactory();
			Point point = geometryFactory.createPoint(new Coordinate(longitud, latitud));

			point = transformPointToShapefileCRS(point,featureSource);

			// Iterar sobre las features (secciones) del shapefile para verificar si el punto está dentro de alguna
			FeatureIterator<SimpleFeature> features = featureSource.getFeatures().features();
			boolean found = false;
			while (features.hasNext()) {
				SimpleFeature feature = features.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();

				String res = feature.getAttribute(18).toString();

				if (geometry.contains(point)) {
					found = true;
					System.out.println("El punto pertenece a la sección: " + feature.getID());

					for(Property attribute : feature.getProperties()) {
						System.out.println(attribute.getName() + " " +  attribute.getValue());
						
					}
					System.out.println("CENTROIDE: " + geometry.getCentroid());
					
					break;
				}
			}
			if (!found) {
				System.out.println("El punto no pertenece a ninguna sección.");
			}

			features.close();
		}

		Boolean prueba4 = false;
		if(prueba4) {
			System.out.println("CALCULO DEL CENTROIDE EN COORDENADAS GEOGRAFICAS PARA 28127");
			Point centroideGeografico = obtenerCentroMunicipio("28127");
			System.out.println(centroideGeografico);
		}

	}

	/*
	 * Método que transforma un punto en coordenadas geográficas a un punto Shapefile CRS
	 */
	public static Point transformPointToShapefileCRS(Point point, SimpleFeatureSource featureSource) throws Exception {
		// Obtener el sistema de referencia del shapefile
		CoordinateReferenceSystem shapefileCRS = featureSource.getSchema().getCoordinateReferenceSystem();

		// Obtener la transformación entre WGS84 (lat/long) y el CRS del shapefile
		MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, shapefileCRS, true);

		// Transformar el punto a las coordenadas del shapefile
		return (Point) JTS.transform(point, transform);
	}

	/*
	 * Método que obtiene el centroide de un municipio en coordendas geográficas a partir de un id_municipio
	 */
	public static Point obtenerCentroMunicipio(String idMunicipio) throws Exception {
		// Cargar el shapefile
		File file = new File(rutaShapefile);
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();

		Point puntoGeografico = null;

		try (FeatureIterator<SimpleFeature> features = featureSource.getFeatures().features()) {
			while (features.hasNext()) {
				SimpleFeature feature = features.next();

				// Verificar si el atributo del ID del municipio coincide
				String municipioID = feature.getAttribute("CUMUN").toString(); // Cambia "CUMUN" si el campo tiene otro nombre
				if (municipioID.equals(idMunicipio)) {
					// Obtener el centroide
					Geometry geometry = (Geometry) feature.getDefaultGeometry();
					Point centroide = geometry.getCentroid();

					CoordinateReferenceSystem shapefileCRS = featureSource.getSchema().getCoordinateReferenceSystem();
					MathTransform transform = CRS.findMathTransform(shapefileCRS, DefaultGeographicCRS.WGS84, true);
					puntoGeografico = (Point) JTS.transform(centroide, transform);
					break;
				}
			}
		}

		store.dispose();
		return puntoGeografico;
	}

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
		}

		store.dispose();
		return puntoGeografico;
	}


	public static int contarSeccionesCensales() throws Exception {
		// Cargar el archivo shapefile
		File file = new File(rutaShapefile);
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();

		int contador = 0;
		try (FeatureIterator<SimpleFeature> features = featureSource.getFeatures().features()) {
			while (features.hasNext()) {
				features.next();
				contador++;
			}
		}

		store.dispose();
		return contador;
	}

	public static ArrayList<SimpleFeature> obtenerSeccionesCensalesPorMunicipio(String idMunicipio) throws Exception {	
		// Cargar el archivo shapefile
		File file = new File(rutaShapefile);
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();

		ArrayList<SimpleFeature> seccionesCensales = new ArrayList<>();

		// Iterar sobre las features en busca de secciones que coincidan con el ID del municipio
		try (FeatureIterator<SimpleFeature> features = featureSource.getFeatures().features()) {
			while (features.hasNext()) {
				SimpleFeature feature = features.next();

				// Verificar si el atributo del ID del municipio coincide
				String municipioID = feature.getAttribute("CUMUN").toString();
				if (municipioID.equals(idMunicipio)) {
					seccionesCensales.add(feature);
				}
			}
		}

		store.dispose();
		return seccionesCensales;
	}

}
