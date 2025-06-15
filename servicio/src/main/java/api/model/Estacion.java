package api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estaciones")
public class Estacion {

	@Id
    @Column(name = "id_estacion")
	private String id_estacion;
	
	@Column(name = "latitud")
	private Float latitud;
	
	@Column(name = "longitud")
	private Float longitud;

	public Estacion() {}

    public Estacion(String id_estacion, float latitud, float longitud) {
    	this.id_estacion = id_estacion;
    	this.latitud = latitud;
    	this.longitud = longitud;
    }

    public String getId_estacion() {
		return id_estacion;
	}

	public void setId_estacion(String id_estacion) {
		this.id_estacion = id_estacion;
	}

	public Float getLatitud() {
		return latitud;
	}

	public void setLatitud(Float latitud) {
		this.latitud = latitud;
	}

	public Float getLongitud() {
		return longitud;
	}

	public void setLongitud(Float longitud) {
		this.longitud = longitud;
	}

	@Override
	public String toString() {
		return "Estacion [id_estacion=" + id_estacion + ", latitud=" + latitud + ", longitud=" + longitud + "]";
	}
}
