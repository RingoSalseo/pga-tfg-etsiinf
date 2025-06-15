package api.model;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;


@JsonPropertyOrder({
	"fecha", 
	"temperaturaMedia", 
	"precipitacion", 
	"temperaturaMinima", 
	"temperaturaMaxima", 
	"direccionRachaMaxima", 
	"velocidadMediaViento", 
	"rachaMaxima", 
	"insolacion", 
	"presionMaxima", 
	"presionMinima", 
	"humedadMedia", 
	"humedadMaxima", 
	"humedadMinima"
})
@Entity
@IdClass(MedicionId.class)
@Table(name = "mediciones")
public class Medicion {

	@Id
	@Column(name = "id_estacion")
	private String id_estacion;

	@Id
	@Column(name = "fecha")
	private String fecha;

	@Column(name = "temperatura_media")
	private Double temperaturaMedia;

	@Column(name = "precipitacion")
	private Double precipitacion;

	@Column(name = "temperatura_minima")
	private Double temperaturaMinima;

	@Column(name = "temperatura_maxima")
	private Double temperaturaMaxima;

	@Column(name = "direccion_racha_maxima")
	private Double direccionRachaMaxima;

	@Column(name = "velocidad_media_viento")
	private Double velocidadMediaViento;

	@Column(name = "racha_maxima")
	private Double rachaMaxima;

	@Column(name = "insolacion")
	private Double insolacion;

	@Column(name = "presion_maxima")
	private Double presionMaxima;

	@Column(name = "presion_minima")
	private Double presionMinima;

	@Column(name = "humedad_media")
	private Double humedadMedia;

	@Column(name = "humedad_maxima")
	private Double humedadMaxima;

	@Column(name = "humedad_minima")
	private Double humedadMinima;

	public Medicion() {}

	// Constructor
	public Medicion(String fecha, Double temperaturaMedia, Double precipitacion,
			Double temperaturaMinima, Double temperaturaMaxima, Double direccionRachaMaxima,
			Double velocidadMediaViento, Double rachaMaxima, Double insolacion,
			Double presionMaxima, Double presionMinima, Double humedadMedia,
			Double humedadMaxima, Double humedadMinima) {
		this.fecha = fecha;

		// Asignar valores predeterminados de 0.0 si el valor es null
		this.temperaturaMedia = temperaturaMedia != null ? temperaturaMedia : 0.0;
		this.precipitacion = precipitacion != null ? precipitacion : 0.0;
		this.temperaturaMinima = temperaturaMinima != null ? temperaturaMinima : 0.0;
		this.temperaturaMaxima = temperaturaMaxima != null ? temperaturaMaxima : 0.0;
		this.direccionRachaMaxima = direccionRachaMaxima != null ? direccionRachaMaxima : 0.0;
		this.velocidadMediaViento = velocidadMediaViento != null ? velocidadMediaViento : 0.0;
		this.rachaMaxima = rachaMaxima != null ? rachaMaxima : 0.0;
		this.insolacion = insolacion != null ? insolacion : 0.0;
		this.presionMaxima = presionMaxima != null ? presionMaxima : 0.0;
		this.presionMinima = presionMinima != null ? presionMinima : 0.0;
		this.humedadMedia = humedadMedia != null ? humedadMedia : 0.0;
		this.humedadMaxima = humedadMaxima != null ? humedadMaxima : 0.0;
		this.humedadMinima = humedadMinima != null ? humedadMinima : 0.0;
	}


	// Getters y Setters
	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public Double getTemperaturaMedia() {
		return temperaturaMedia;
	}

	public void setTemperaturaMedia(Double temperaturaMedia) {
		this.temperaturaMedia = temperaturaMedia;
	}

	public Double getPrecipitacion() {
		return precipitacion;
	}

	public void setPrecipitacion(Double precipitacion) {
		this.precipitacion = precipitacion;
	}

	public Double getTemperaturaMinima() {
		return temperaturaMinima;
	}

	public void setTemperaturaMinima(Double temperaturaMinima) {
		this.temperaturaMinima = temperaturaMinima;
	}

	public Double getTemperaturaMaxima() {
		return temperaturaMaxima;
	}

	public void setTemperaturaMaxima(Double temperaturaMaxima) {
		this.temperaturaMaxima = temperaturaMaxima;
	}

	public Double getDireccionRachaMaxima() {
		return direccionRachaMaxima;
	}

	public void setDireccionRachaMaxima(Double direccionRachaMaxima) {
		this.direccionRachaMaxima = direccionRachaMaxima;
	}

	public Double getVelocidadMediaViento() {
		return velocidadMediaViento;
	}

	public void setVelocidadMediaViento(Double velocidadMediaViento) {
		this.velocidadMediaViento = velocidadMediaViento;
	}

	public Double getRachaMaxima() {
		return rachaMaxima;
	}

	public void setRachaMaxima(Double rachaMaxima) {
		this.rachaMaxima = rachaMaxima;
	}

	public Double getInsolacion() {
		return insolacion;
	}

	public void setInsolacion(Double insolacion) {
		this.insolacion = insolacion;
	}

	public Double getPresionMaxima() {
		return presionMaxima;
	}

	public void setPresionMaxima(Double presionMaxima) {
		this.presionMaxima = presionMaxima;
	}

	public Double getPresionMinima() {
		return presionMinima;
	}

	public void setPresionMinima(Double presionMinima) {
		this.presionMinima = presionMinima;
	}

	public Double getHumedadMedia() {
		return humedadMedia;
	}

	public void setHumedadMedia(Double humedadMedia) {
		this.humedadMedia = humedadMedia;
	}

	public Double getHumedadMaxima() {
		return humedadMaxima;
	}

	public void setHumedadMaxima(Double humedadMaxima) {
		this.humedadMaxima = humedadMaxima;
	}

	public Double getHumedadMinima() {
		return humedadMinima;
	}

	public void setHumedadMinima(Double humedadMinima) {
		this.humedadMinima = humedadMinima;
	}

}

