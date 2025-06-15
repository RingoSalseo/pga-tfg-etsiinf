package api.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import api.model.Estacion;
import api.repository.EstacionRepository;

@Service
public class EstacionService {

	private final EstacionRepository estacionRepository;

	@Autowired
	public EstacionService(EstacionRepository estacionRepository) {
		this.estacionRepository = estacionRepository;
	}

	// Obtener todas las estaciones
	public List<Estacion> obtenerEstaciones() {
		return estacionRepository.findAllCoords();

	}
}
