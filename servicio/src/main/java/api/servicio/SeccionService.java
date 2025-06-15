package api.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import api.model.Seccion;
import api.repository.SeccionRepository;
import api.utils.CodigoAuxiliar;

@Service
public class SeccionService {

	 private final SeccionRepository seccionRepository;

	    @Autowired
	    public SeccionService(SeccionRepository seccionRepository) {
	        this.seccionRepository = seccionRepository;
	    }

	    public List<Seccion> obtenerSeccionesPorCodigoPostal(String codigoPostal) {
	        return seccionRepository.findByCodigoPostal(codigoPostal);
	    }
	    
	    public List<Seccion> obtenerSeccionesPorComunidad(String idComunidad) {
	        Set<String> provincias = CodigoAuxiliar.obtenerProvinciasPorComunidad(idComunidad);
	        if (provincias.isEmpty()) return List.of();

	        return seccionRepository.findByIdProvincias(new ArrayList<>(provincias));
	    }
}
