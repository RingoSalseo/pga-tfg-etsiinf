package api.servicio;

import api.dto.MedicionDTO;
import api.repository.MedicionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicionService {

    private final MedicionRepository medicionRepository;

    @Autowired
    public MedicionService(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    public List<MedicionDTO> obtenerMedicionesAgrupadas(List<String> ids) {
        return medicionRepository.obtenerMedicionesAgrupadasPorFecha(ids);
    }
}
