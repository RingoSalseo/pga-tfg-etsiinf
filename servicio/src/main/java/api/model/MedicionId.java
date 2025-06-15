package api.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class MedicionId implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id_estacion;
    private LocalDate fecha;

    public MedicionId() {}

    public MedicionId(String id_estacion, LocalDate fecha) {
        this.id_estacion = id_estacion;
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicionId)) return false;
        MedicionId that = (MedicionId) o;
        return Objects.equals(id_estacion, that.id_estacion) && Objects.equals(fecha, that.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_estacion, fecha);
    }
}

