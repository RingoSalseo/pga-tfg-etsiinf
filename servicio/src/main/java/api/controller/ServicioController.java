package api.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import api.dto.MedicionDTO;
import api.model.CoordenadasGeograficas;
import api.model.ObjetoRespuesta;
import api.model.Seccion;
import api.servicio.MedicionService;
import api.servicio.SeccionService;
import api.utils.CodigoAuxiliar;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/demografico")
public class ServicioController {

	private static final String URL = "jdbc:mysql://localhost:3306/bbdd_tfg";
	private static final String USER = "root";
	private static final String PASSWORD = "root";

	private final SeccionService seccionService;
	private MedicionService medicionService;


	@Autowired
	public ServicioController(SeccionService seccionService, MedicionService medicionService) {
		this.seccionService = seccionService;
		this.medicionService = medicionService;
	}

	@GetMapping("/comunidad")
	@Operation(
			summary = "Obtener la información relativa a la comunidad y a las estaciones más cercanas a ella en función del id de la comunidad.",
			description = "El id debe pertenecer a España. Estar en un rango entre 01, 02... y 17"
			)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(mediaType = "application/json", 
					schema = @Schema(implementation = ObjetoRespuesta.class))),
			@ApiResponse(responseCode = "204", description = "No se encontró la comunidad autónoma con el id empleado"),
			@ApiResponse(responseCode = "400", description = "El id_comunidad tiene un formato incorrecto. El formato correcto es \'dd\', donde d es un digito entre 0 y 9",
			content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
			content = @Content(mediaType = "application/json"))
	})
	public ObjetoRespuesta obtenerComunidadPorId(@Parameter(name = "id_comunidad", description = "Identificador de comunidad (01 a 17)", required = true, example = "01")
	@RequestParam(name = "id_comunidad") String idComunidad) {
		
		// Validación del formato: exactamente dos dígitos
	    if (!idComunidad.matches("^\\d{2}$")) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "El id_comunidad tiene un formato incorrecto. Debe tener exactamente dos dígitos numéricos."
	        );
	    }

	    // Validación del rango: entre 01 y 17
	    int idNumerico = Integer.parseInt(idComunidad);
	    if (idNumerico < 1 || idNumerico > 17) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "El id_comunidad debe estar en el rango de 01 a 17."
	        );
	    }
		
		List<Seccion> secciones = seccionService.obtenerSeccionesPorComunidad(idComunidad);

		ObjetoRespuesta resultado = null;
		int total = secciones.size();

		//ACumuladores
		String idsSecciones = "";
		String idsDistrito = "";
		String nombresSecciones = "";
		int sumaRentaNetaMediaPersona = 0, sumaRentaNetaMediaHogar = 0, sumaRentaUnidadConsumo = 0;
		int sumaMedianaRentaConsumo = 0, sumaRentaBrutaMediaPersona = 0, sumaRentaBrutaMediaHogar = 0;
		double sumaEdadMediaPoblacion = 0, sumaPorcentajeMenor18 = 0, sumaPorcentajeMayor65 = 0;
		double sumaTamañoMedioHogar = 0, sumaPorcentajeHogaresUnipersonales = 0, sumaPorcentajePoblacionEspañola = 0;
		int sumaPoblacion = 0, sumaFuenteIngresosSalario = 0, sumaFuenteIngresosPensiones = 0;
		int sumaFuenteIngresosPdesempleado = 0, sumaFuenteIngresosOtrPrestaciones = 0, sumaFuenteIngresosOtrIngresos = 0;
		double sumaLatitudCentroide = 0, sumaLongitudCentroide = 0;

		System.out.println("___SECCIONES___");
		for (Seccion seccion : secciones) {
			System.out.println("(" + seccion.getLatitud_centroide_seccion() + ", " + seccion.getLongitud_centroide_seccion() + ")");
			idsSecciones += " - " + seccion.getIdSeccion();
			idsDistrito += " - " + seccion.getIdDistrito();
			nombresSecciones += " - " + seccion.getNombreSeccion();
			sumaRentaNetaMediaPersona += seccion.getRentaNetaMediaPersona();
			sumaRentaNetaMediaHogar += seccion.getRentaNetaMediaHogar();
			sumaRentaUnidadConsumo += seccion.getRentaUnidadConsumo();
			sumaMedianaRentaConsumo += seccion.getMedianaRentaConsumo();
			sumaRentaBrutaMediaPersona += seccion.getRentaBrutaMediaPersona();
			sumaRentaBrutaMediaHogar += seccion.getRentaBrutaMediaHogar();
			sumaEdadMediaPoblacion += seccion.getEdadMediaPoblacion();
			sumaPorcentajeMenor18 += seccion.getPorcentajeMenor18();
			sumaPorcentajeMayor65 += seccion.getPorcentajeMayor65();
			sumaTamañoMedioHogar += seccion.getTamañoMedioHogar();
			sumaPorcentajeHogaresUnipersonales += seccion.getPorcentajeHogaresUnipersonales();
			sumaPoblacion += seccion.getPoblacion();
			sumaPorcentajePoblacionEspañola += seccion.getPorcentajePoblacionEspañola();
			sumaFuenteIngresosSalario += seccion.getFuenteIngresosSalario();
			sumaFuenteIngresosPensiones += seccion.getFuenteIngresosPensiones();
			sumaFuenteIngresosPdesempleado += seccion.getFuenteIngresosPDesempleado();
			sumaFuenteIngresosOtrPrestaciones += seccion.getFuenteIngresosOtrPrestaciones();
			sumaFuenteIngresosOtrIngresos += seccion.getFuenteIngresosOtrIngresos();

			if(seccion.getLatitud_centroide_seccion() != 0) {
				sumaLatitudCentroide += seccion.getLatitud_centroide_seccion();
				sumaLongitudCentroide += seccion.getLongitud_centroide_seccion();
			}
			else {
				total--;
			}

		}
		System.out.println("_______________");
		if(total == 0) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Las secciones censales de la bbdd tienen las coordenadas a (0,0)\nImposible calcular un centroide ubicado en España");
		}

		//PARTE SECCION
		Seccion mediaSeccionesComunidad = new Seccion(
				idsSecciones,
				idsDistrito,
				"Promedio de: " + nombresSecciones,
				"",
				sumaRentaNetaMediaPersona / total,
				sumaRentaNetaMediaHogar / total,
				sumaRentaUnidadConsumo / total,
				sumaMedianaRentaConsumo / total,
				sumaRentaBrutaMediaPersona / total,
				sumaRentaBrutaMediaHogar / total,
				sumaEdadMediaPoblacion / total,
				sumaPorcentajeMenor18 / total,
				sumaPorcentajeMayor65 / total,
				sumaTamañoMedioHogar / total,
				sumaPorcentajeHogaresUnipersonales / total,
				sumaPoblacion / total,
				sumaPorcentajePoblacionEspañola / total,
				sumaFuenteIngresosSalario / total,
				sumaFuenteIngresosPensiones / total,
				sumaFuenteIngresosPdesempleado / total,
				sumaFuenteIngresosOtrPrestaciones / total,
				sumaFuenteIngresosOtrIngresos / total,
				sumaLatitudCentroide / total,
				sumaLongitudCentroide / total
				);
		//PARTE ESTACIONES
		double latitudCentroide = sumaLatitudCentroide / total;
		double longitudCentroide = sumaLongitudCentroide / total;

		List<MedicionDTO> medicionesMediaEstacionesCercanasComunidad =
		    medicionService.obtenerMedicionesPorUbicacion(
		        latitudCentroide, longitudCentroide,
		        idComunidad, true, 4);

		resultado = new ObjetoRespuesta(medicionesMediaEstacionesCercanasComunidad, mediaSeccionesComunidad);
		return resultado;
	}


	@GetMapping("/codigopostal")
	@Operation(
			summary = "Obtener la información relativa a las secciones y a las estaciones más cercanas en función del codigo postal.",
			description = "El código postal debe pertenecer a España."
			)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", 
					content = @Content(mediaType = "application/json", 
					schema = @Schema(implementation = ObjetoRespuesta.class))),
			@ApiResponse(responseCode = "204", description = "No se encontraron secciones para el código postal proporcionado."),
			@ApiResponse(responseCode = "400", description = "El código postal tiene un formato inválido o no existen estaciones cercanas al mismo.",
			content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
			content = @Content(mediaType = "application/json"))
	})
	public ObjetoRespuesta obtenerSeccionesPorCodigoPostal(@Parameter(name = "codigo_postal",description = "Código postal del municipio. Debe ser de 5 dígitos.", required = true, example = "28290")
	@RequestParam(name = "codigo_postal") String codigoPostal) {

		if (codigoPostal.length() != 5) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR : El código postal debe tener exactamente 5 caracteres.");
		if(!codigoPostal.matches("\\d{5}")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR : Formato del código postal invalido.");

		// Con esta simple llamada obtenemos todas las secciones bajo un mismo codigo postal
		List<Seccion> secciones = seccionService.obtenerSeccionesPorCodigoPostal(codigoPostal);
		ObjetoRespuesta resultado = null;
		int total = secciones.size();

		//ACumuladores
		String idsSecciones = "";
		String idsDistrito = "";
		String nombresSecciones = "";
		int sumaRentaNetaMediaPersona = 0, sumaRentaNetaMediaHogar = 0, sumaRentaUnidadConsumo = 0;
		int sumaMedianaRentaConsumo = 0, sumaRentaBrutaMediaPersona = 0, sumaRentaBrutaMediaHogar = 0;
		double sumaEdadMediaPoblacion = 0, sumaPorcentajeMenor18 = 0, sumaPorcentajeMayor65 = 0;
		double sumaTamañoMedioHogar = 0, sumaPorcentajeHogaresUnipersonales = 0, sumaPorcentajePoblacionEspañola = 0;
		int sumaPoblacion = 0, sumaFuenteIngresosSalario = 0, sumaFuenteIngresosPensiones = 0;
		int sumaFuenteIngresosPdesempleado = 0, sumaFuenteIngresosOtrPrestaciones = 0, sumaFuenteIngresosOtrIngresos = 0;
		double sumaLatitudCentroide = 0, sumaLongitudCentroide = 0;

		System.out.println("___SECCIONES___");
		for (Seccion seccion : secciones) {
			System.out.println("(" + seccion.getLatitud_centroide_seccion() + ", " + seccion.getLongitud_centroide_seccion() + ")");
			idsSecciones += " - " + seccion.getIdSeccion();
			idsDistrito += " - " + seccion.getIdDistrito();
			nombresSecciones += " - " + seccion.getNombreSeccion();
			sumaRentaNetaMediaPersona += seccion.getRentaNetaMediaPersona();
			sumaRentaNetaMediaHogar += seccion.getRentaNetaMediaHogar();
			sumaRentaUnidadConsumo += seccion.getRentaUnidadConsumo();
			sumaMedianaRentaConsumo += seccion.getMedianaRentaConsumo();
			sumaRentaBrutaMediaPersona += seccion.getRentaBrutaMediaPersona();
			sumaRentaBrutaMediaHogar += seccion.getRentaBrutaMediaHogar();
			sumaEdadMediaPoblacion += seccion.getEdadMediaPoblacion();
			sumaPorcentajeMenor18 += seccion.getPorcentajeMenor18();
			sumaPorcentajeMayor65 += seccion.getPorcentajeMayor65();
			sumaTamañoMedioHogar += seccion.getTamañoMedioHogar();
			sumaPorcentajeHogaresUnipersonales += seccion.getPorcentajeHogaresUnipersonales();
			sumaPoblacion += seccion.getPoblacion();
			sumaPorcentajePoblacionEspañola += seccion.getPorcentajePoblacionEspañola();
			sumaFuenteIngresosSalario += seccion.getFuenteIngresosSalario();
			sumaFuenteIngresosPensiones += seccion.getFuenteIngresosPensiones();
			sumaFuenteIngresosPdesempleado += seccion.getFuenteIngresosPDesempleado();
			sumaFuenteIngresosOtrPrestaciones += seccion.getFuenteIngresosOtrPrestaciones();
			sumaFuenteIngresosOtrIngresos += seccion.getFuenteIngresosOtrIngresos();

			if(seccion.getLatitud_centroide_seccion() != 0) {
				sumaLatitudCentroide += seccion.getLatitud_centroide_seccion();
				sumaLongitudCentroide += seccion.getLongitud_centroide_seccion();
			}
			else {
				total--;
			}

		}
		System.out.println("_______________");
		if(total == 0) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Las secciones censales de la bbdd tienen las coordenadas a (0,0)\nImposible calcular un centroide ubicado en España");
		}

		//PARTE SECCION
		Seccion seccion_res = new Seccion(
				idsSecciones,
				idsDistrito,
				"Promedio de: " + nombresSecciones,
				codigoPostal,
				sumaRentaNetaMediaPersona / total,
				sumaRentaNetaMediaHogar / total,
				sumaRentaUnidadConsumo / total,
				sumaMedianaRentaConsumo / total,
				sumaRentaBrutaMediaPersona / total,
				sumaRentaBrutaMediaHogar / total,
				sumaEdadMediaPoblacion / total,
				sumaPorcentajeMenor18 / total,
				sumaPorcentajeMayor65 / total,
				sumaTamañoMedioHogar / total,
				sumaPorcentajeHogaresUnipersonales / total,
				sumaPoblacion / total,
				sumaPorcentajePoblacionEspañola / total,
				sumaFuenteIngresosSalario / total,
				sumaFuenteIngresosPensiones / total,
				sumaFuenteIngresosPdesempleado / total,
				sumaFuenteIngresosOtrPrestaciones / total,
				sumaFuenteIngresosOtrIngresos / total,
				sumaLatitudCentroide / total,
				sumaLongitudCentroide / total
				);

		//PARTE ESTACIONES
		double latitudCentroide = sumaLatitudCentroide / total;
		double longitudCentroide = sumaLongitudCentroide / total;

		List<MedicionDTO> medicionesMediaEstacionesCercanas =
		    medicionService.obtenerMedicionesPorUbicacion(
		        latitudCentroide, longitudCentroide,
		        idsSecciones, false, 2);

		resultado = new ObjetoRespuesta(medicionesMediaEstacionesCercanas, seccion_res);
		return resultado;
	}

	@GetMapping("/coordenadas")
	@Operation(
	    summary = "Obtener la información relativa a las secciones y a las estaciones más cercanas en función de las coordenadas geográficas.",
	    description = "Las coordenadas geográficas deben pertenecer a un punto de la geografía española."
	)
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "OK",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ObjetoRespuesta.class))),
	    @ApiResponse(responseCode = "400", description = "Las coordenadas tienen un formato inválido o no existen estaciones cercanas a ellas.",
	        content = @Content(mediaType = "application/json")),
	    @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
	        content = @Content(mediaType = "application/json"))
	})
	public ObjetoRespuesta obtenerSeccionesPorCoordenadasGeograficas(
	    @Parameter(description = "Latitud en grados decimales. Rango válido: -90 a 90", required = true, example = "40.541538246225144")
	    @RequestParam(name = "latitud") String latitud,

	    @Parameter(description = "Longitud en grados decimales. Rango válido: -180 a 180", required = true, example = "-3.8995029645066284")
	    @RequestParam(name = "longitud") String longitud) {

	    if (latitud.contains(",") || longitud.contains(",")) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "ERROR : Los parámetros latitud y longitud no deben contener comas (,).\n"
	            + "Debes emplear la siguiente notación : latitud=43.5 o longitud=-2.546"
	        );
	    }

	    double lat = Double.parseDouble(latitud);
	    double lon = Double.parseDouble(longitud);
	    System.out.println("Latitud : " + lat + " / Longitud : " + lon);

	    Object[] res;
	    try {
	        res = CodigoAuxiliar.obtenerCusecYCoordenadasSeccion(lat, lon);
	    } catch (Exception e) {
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al acceder al shapefile", e);
	    }

	    String id_seccion = (String) res[0];
	    Point centroide_seccion = (Point) res[1];

	    if (id_seccion == null) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "ERROR : No existe una sección censal con coordenadas: (" + lat + ", " + lon + ") en el Shapefile en la ruta:\n" + CodigoAuxiliar.rutaShapefile
	        );
	    }
	    
	    //PARTE ESTACIONES
	    Seccion seccion = seccionService.obtenerSeccionPorId(id_seccion);
	    List<MedicionDTO> mediciones = medicionService.obtenerMedicionesPorUbicacion(
	        centroide_seccion.getY(), centroide_seccion.getX(),
	        id_seccion, false, 2);

	    return new ObjetoRespuesta(mediciones, seccion);
	}


	@GetMapping("/cp")
	@Operation(
			summary = "Obtener la media de los datos de las secciones censales que pertenecen a dicho código postal.",
			description = "El código postal debe pertenecer a España."
			)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", 
					content = @Content(mediaType = "application/json", 
					schema = @Schema(implementation = Seccion.class))),
			@ApiResponse(responseCode = "204", description = "No se encontraron secciones para el código postal proporcionado."),
			@ApiResponse(responseCode = "400", description = "El código postal tiene un formato inválido."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})
	public Seccion obtenerSeccionesPorCodigoPostalPrueba(@RequestParam(name = "codigo_postal") String codigoPostal) {

		if (codigoPostal.length() != 5) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR : El código postal debe tener exactamente 5 caracteres.");
		if(!codigoPostal.matches("\\d{5}")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR : Formato del código postal invalido.");

		List<Seccion> secciones = new ArrayList<>();
		String query = "SELECT * FROM Secciones WHERE codigo_postal = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, codigoPostal);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Seccion seccion = new Seccion(
						resultSet.getString("id_seccion"),
						resultSet.getString("id_distrito"),
						resultSet.getString("nombre_seccion"),
						resultSet.getString("codigo_postal"),
						resultSet.getInt("renta_neta_media_persona"),
						resultSet.getInt("renta_neta_media_hogar"),
						resultSet.getInt("renta_unidad_consumo"),
						resultSet.getInt("mediana_renta_consumo"),
						resultSet.getInt("renta_bruta_media_persona"),
						resultSet.getInt("renta_bruta_media_hogar"),
						resultSet.getDouble("edad_media_poblacion"),
						resultSet.getDouble("porcentaje_menor_18"),
						resultSet.getDouble("porcentaje_mayor_65"),
						resultSet.getDouble("tamaño_medio_hogar"),
						resultSet.getDouble("porcentaje_hogares_unipersonales"),
						resultSet.getInt("poblacion"),
						resultSet.getDouble("porcentaje_poblacion_española"),
						resultSet.getInt("fuente_ingresos_salario"),
						resultSet.getInt("fuente_ingresos_pensiones"),
						resultSet.getInt("fuente_ingresos_pdesempleado"),
						resultSet.getInt("fuente_ingresos_otrprestaciones"),
						resultSet.getInt("fuente_ingresos_otringresos"),
						resultSet.getDouble("latitud_centroide_seccion"),
						resultSet.getDouble("longitud_centroide_seccion")
						);
				secciones.add(seccion);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error al consultar la base de datos.");
		}

		// Calcular la media de los campos
		if (secciones.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "INFO : No se encontraron secciones para el código postal proporcionado.");
		}

		int total = secciones.size();

		//ACumuladores
		String idsSecciones = "";
		String idsDistrito = "";
		String nombresSecciones = "";
		int sumaRentaNetaMediaPersona = 0, sumaRentaNetaMediaHogar = 0, sumaRentaUnidadConsumo = 0;
		int sumaMedianaRentaConsumo = 0, sumaRentaBrutaMediaPersona = 0, sumaRentaBrutaMediaHogar = 0;
		double sumaEdadMediaPoblacion = 0, sumaPorcentajeMenor18 = 0, sumaPorcentajeMayor65 = 0;
		double sumaTamañoMedioHogar = 0, sumaPorcentajeHogaresUnipersonales = 0, sumaPorcentajePoblacionEspañola = 0;
		int sumaPoblacion = 0, sumaFuenteIngresosSalario = 0, sumaFuenteIngresosPensiones = 0;
		int sumaFuenteIngresosPdesempleado = 0, sumaFuenteIngresosOtrPrestaciones = 0, sumaFuenteIngresosOtrIngresos = 0;
		double sumaLatitudCentroide = 0, sumaLongitudCentroide = 0;

		for (Seccion seccion : secciones) {
			idsSecciones += " - " + seccion.getIdSeccion();
			idsDistrito += " - " + seccion.getIdDistrito();
			nombresSecciones += " - " + seccion.getNombreSeccion();
			sumaRentaNetaMediaPersona += seccion.getRentaNetaMediaPersona();
			sumaRentaNetaMediaHogar += seccion.getRentaNetaMediaHogar();
			sumaRentaUnidadConsumo += seccion.getRentaUnidadConsumo();
			sumaMedianaRentaConsumo += seccion.getMedianaRentaConsumo();
			sumaRentaBrutaMediaPersona += seccion.getRentaBrutaMediaPersona();
			sumaRentaBrutaMediaHogar += seccion.getRentaBrutaMediaHogar();
			sumaEdadMediaPoblacion += seccion.getEdadMediaPoblacion();
			sumaPorcentajeMenor18 += seccion.getPorcentajeMenor18();
			sumaPorcentajeMayor65 += seccion.getPorcentajeMayor65();
			sumaTamañoMedioHogar += seccion.getTamañoMedioHogar();
			sumaPorcentajeHogaresUnipersonales += seccion.getPorcentajeHogaresUnipersonales();
			sumaPoblacion += seccion.getPoblacion();
			sumaPorcentajePoblacionEspañola += seccion.getPorcentajePoblacionEspañola();
			sumaFuenteIngresosSalario += seccion.getFuenteIngresosSalario();
			sumaFuenteIngresosPensiones += seccion.getFuenteIngresosPensiones();
			sumaFuenteIngresosPdesempleado += seccion.getFuenteIngresosPDesempleado();
			sumaFuenteIngresosOtrPrestaciones += seccion.getFuenteIngresosOtrPrestaciones();
			sumaFuenteIngresosOtrIngresos += seccion.getFuenteIngresosOtrIngresos();
			sumaLatitudCentroide += seccion.getLatitud_centroide_seccion();
			sumaLongitudCentroide += seccion.getLongitud_centroide_seccion();
		}
		Seccion resultado = new Seccion(
				idsSecciones,
				idsDistrito,
				"Promedio de: " + nombresSecciones,
				codigoPostal,
				sumaRentaNetaMediaPersona / total,
				sumaRentaNetaMediaHogar / total,
				sumaRentaUnidadConsumo / total,
				sumaMedianaRentaConsumo / total,
				sumaRentaBrutaMediaPersona / total,
				sumaRentaBrutaMediaHogar / total,
				sumaEdadMediaPoblacion / total,
				sumaPorcentajeMenor18 / total,
				sumaPorcentajeMayor65 / total,
				sumaTamañoMedioHogar / total,
				sumaPorcentajeHogaresUnipersonales / total,
				sumaPoblacion / total,
				sumaPorcentajePoblacionEspañola / total,
				sumaFuenteIngresosSalario / total,
				sumaFuenteIngresosPensiones / total,
				sumaFuenteIngresosPdesempleado / total,
				sumaFuenteIngresosOtrPrestaciones / total,
				sumaFuenteIngresosOtrIngresos / total,
				sumaLatitudCentroide / total,
				sumaLongitudCentroide / total
				);

		return resultado;
	}

	@GetMapping("/centroide")
	@Operation(
			summary = "Obtener el centroide de cada una de las secciones de un municipio",
			description = "Devuelve las coordenadas geográficas del centroide de las secciones de un municipio basado en el código postal proporcionado."
					+ "\nConsulta la base de datos."
			)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "204", description = "No se encontraron secciones para el código postal proporcionado."),
			@ApiResponse(responseCode = "400", description = "El código postal tiene un formato inválido."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})
	public List<CoordenadasGeograficas> obtenerCentroideMunicipio(@Parameter(description = "Código postal del municipio. Debe ser de 5 dígitos.")
	@RequestParam(name = "codigo_postal", defaultValue = "28290") String codigoPostal){   	

		if (codigoPostal.length() != 5) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR : El código postal debe tener exactamente 5 caracteres.");
		if(!codigoPostal.matches("\\d{5}")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR : Formato del código postal invalido.");

		List<CoordenadasGeograficas> centroidesSecciones = new ArrayList<>();
		String query = "SELECT id_seccion, latitud_centroide_seccion, longitud_centroide_seccion FROM Secciones WHERE codigo_postal = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, codigoPostal);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				CoordenadasGeograficas coordG = new CoordenadasGeograficas(
						resultSet.getString("id_seccion"),
						resultSet.getDouble("latitud_centroide_seccion"),
						resultSet.getDouble("longitud_centroide_seccion")
						);
				centroidesSecciones.add(coordG);
			}

		} catch (SQLException e) {
			System.err.println("Error al consultar la base de datos.");
			e.printStackTrace();
		}
		if(centroidesSecciones.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "INFO : No existen secciones para el codigo postal : " + codigoPostal);

		return centroidesSecciones;
	}
}
