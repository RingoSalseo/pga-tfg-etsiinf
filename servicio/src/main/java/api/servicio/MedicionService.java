package api.servicio;

import api.dto.MedicionDTO;
import api.model.Estacion;
import api.repository.MedicionRepository;
import api.utils.CodigoAuxiliar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MedicionService {

	private final MedicionRepository medicionRepository;
	private final EstacionService estacionService;

	@Autowired
	public MedicionService(MedicionRepository medicionRepository, EstacionService estacionService) {
		this.medicionRepository = medicionRepository;
		this.estacionService = estacionService;
	}

	public List<MedicionDTO> obtenerMedicionesAgrupadas(List<String> ids) {
		return medicionRepository.obtenerMedicionesAgrupadasPorFecha(ids);
	}

	public List<MedicionDTO> obtenerMedicionesPorUbicacion(
			double latitud, double longitud,
			String idZona,
			boolean esComunidad,
			int cantidadEstaciones
			) {
		List<Estacion> todasEstaciones = estacionService.obtenerEstaciones();

		List<Estacion> estacionesCercanas = (cantidadEstaciones == 4)
				? CodigoAuxiliar.obtenerCuatroEstacionesMasCercanas(latitud, longitud, todasEstaciones)
						: CodigoAuxiliar.obtenerDosEstacionesMasCercanas(latitud, longitud, todasEstaciones);

		return obtenerMedicionesMediasEstacionesCercanas(idZona, estacionesCercanas, esComunidad);
	}


	public List<MedicionDTO> obtenerMedicionesMediasEstacionesCercanas(String idZona, List<Estacion> estacionesCercanas, boolean esComunidad) {
		if (estacionesCercanas == null || estacionesCercanas.isEmpty()) {
			String tipoZona = esComunidad ? "comunidad autónoma" : "sección censal";
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"ERROR: No se encontraron estaciones cercanas para la " + tipoZona + ": " + idZona + "\nComprobar coordenadas."
					);
		}

		List<String> idsEstaciones = estacionesCercanas.stream()
				.map(Estacion::getId_estacion)
				.toList();

		System.out.println(LocalTime.now().withNano(0) + " Obteniendo datos de las estaciones: " + idsEstaciones);

		List<MedicionDTO> mediciones = obtenerMedicionesAgrupadas(idsEstaciones);
		mediciones.sort(Comparator.comparing(MedicionDTO::getFecha));

		System.out.println(LocalTime.now().withNano(0) + " Datos de las estaciones obtenidos.");
		return mediciones;
	}
}
