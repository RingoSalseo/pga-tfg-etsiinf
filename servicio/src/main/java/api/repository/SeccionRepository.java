package api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import api.model.Seccion;
import java.util.List;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, String> {
    
    // Método para encontrar todas las secciones por código postal
    List<Seccion> findByCodigoPostal(String codigoPostal);
    
    // Método para obtener todas las secciones de una comunidad autónoma
    @Query(value = "SELECT * FROM secciones WHERE LEFT(id_seccion, 2) IN (:provincias)", nativeQuery = true)
    List<Seccion> findByIdProvincias(@Param("provincias") List<String> provincias);

    //Devuelve una seccion censal por id
    Seccion findFirstByIdSeccion(String idSeccion);
}
