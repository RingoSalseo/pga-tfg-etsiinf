package api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import api.model.Estacion;

public interface EstacionRepository extends JpaRepository<Estacion, String> {

	//Unicamente devuelve 3 atributos, aunque realmente la bbdd tiene 5 atributos
    @Query("SELECT e FROM Estacion e")
    List<Estacion> findAllCoords();
}

