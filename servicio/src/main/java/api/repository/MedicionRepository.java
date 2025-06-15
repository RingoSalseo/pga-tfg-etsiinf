package api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import api.dto.MedicionDTO;
import api.model.Medicion;
import api.model.MedicionId;

public interface MedicionRepository extends JpaRepository<Medicion, MedicionId> {

	@Query("SELECT " +
            "m.id.fecha AS fecha, " +
            "AVG(m.temperaturaMedia) AS temperaturaMedia, " +
            "AVG(m.precipitacion) AS precipitacion, " +
            "AVG(m.temperaturaMinima) AS temperaturaMinima, " +
            "AVG(m.temperaturaMaxima) AS temperaturaMaxima, " +
            "AVG(m.direccionRachaMaxima) AS direccionRachaMaxima, " +
            "AVG(m.velocidadMediaViento) AS velocidadMediaViento, " +
            "AVG(m.rachaMaxima) AS rachaMaxima, " +
            "AVG(m.insolacion) AS insolacion, " +
            "AVG(m.presionMaxima) AS presionMaxima, " +
            "AVG(m.presionMinima) AS presionMinima, " +
            "AVG(m.humedadMedia) AS humedadMedia, " +
            "AVG(m.humedadMaxima) AS humedadMaxima, " +
            "AVG(m.humedadMinima) AS humedadMinima " +
            "FROM Medicion m " +
            "WHERE m.id.id_estacion IN :ids " +
            "GROUP BY m.id.fecha")
    List<MedicionDTO> obtenerMedicionesAgrupadasPorFecha(@Param("ids") List<String> ids);
}
