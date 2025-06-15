package estaciones.nuevo;

import java.sql.Date;

import com.google.gson.annotations.SerializedName;

public class ObjetoEstacionHistorico {

private Date fecha;
	
	@SerializedName("indicativo")
    private String id_estacion;
	
    private String nombre;
    private String provincia;
    private String altitud;
    
    @SerializedName("tmed")
    private String temperatura_media;

    @SerializedName("prec")
    private String precipitacion;

    @SerializedName("tmin")
    private String temperatura_minima;

    @SerializedName("horatmin")
    private String hora_temperatura_minima;

    @SerializedName("tmax")
    private String temperatura_maxima;

    @SerializedName("horatmax")
    private String hora_temperatura_maxima;

    @SerializedName("dir")
    private String direccion_racha_maxima;
    
    @SerializedName("velmedia")
    private String velocidad_media_viento;
    
    @SerializedName("racha")
    private String racha_maxima;
    
    private String horaracha;
    
    @SerializedName("sol")
    private String insolacion;

    @SerializedName("presMax")
    private String presion_maxima;

    @SerializedName("horaPresMax")
    private String hora_presion_maxima;

    @SerializedName("presMin")
    private String presion_minima;

    @SerializedName("horaPresMin")
    private String hora_presion_minima;

    @SerializedName("hrMedia")
    private String humedad_media;

    @SerializedName("hrMax")
    private String humedad_maxima;

    @SerializedName("horaHrMax")
    private String hora_humedad_maxima;

    @SerializedName("hrMin")
    private String humedad_minima;

    @SerializedName("horaHrMin")
    private String hora_humedad_minima;
    
    public static float reemplazarComa(String valor) {
    	return Float.parseFloat(valor.trim().replace(",", "."));
    }

	public ObjetoEstacionHistorico(Date fecha, String id_estacion, String nombre, String provincia, String altitud,
			String temperatura_media, String precipitacion, String temperatura_minima, String hora_temperatura_minima,
			String temperatura_maxima, String hora_temperatura_maxima, String direccion_racha_maxima,
			String velocidad_media_viento, String racha_maxima, String horaracha, String insolacion, String presion_maxima,
			String hora_presion_maxima, String presion_minima, String hora_presion_minima, String humedad_media,
			String humedad_maxima, String hora_humedad_maxima, String humedad_minima, String hora_humedad_minima) {
		super();
		this.fecha = fecha;
        this.id_estacion = id_estacion;
        this.nombre = nombre;
        this.provincia = provincia;
        this.altitud = altitud;
        this.temperatura_media = temperatura_media;
        this.precipitacion = precipitacion;
        this.temperatura_minima = temperatura_minima;
        this.hora_temperatura_minima = hora_temperatura_minima;
        this.temperatura_maxima = temperatura_maxima;
        this.hora_temperatura_maxima = hora_temperatura_maxima;
        this.direccion_racha_maxima = direccion_racha_maxima;
        this.velocidad_media_viento = velocidad_media_viento;
        this.racha_maxima = racha_maxima;
        this.horaracha = horaracha;
        this.insolacion = insolacion;
        this.presion_maxima = presion_maxima;
        this.hora_presion_maxima = hora_presion_maxima;
        this.presion_minima = presion_minima;
        this.hora_presion_minima = hora_presion_minima;
        this.humedad_media = humedad_media;
        this.humedad_maxima = humedad_maxima;
        this.hora_humedad_maxima = hora_humedad_maxima;
        this.humedad_minima = humedad_minima;
        this.hora_humedad_minima = hora_humedad_minima;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getId_estacion() {
		return id_estacion;
	}

	public void setId_estacion(String id_estacion) {
		this.id_estacion = id_estacion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getAltitud() {
		return altitud;
	}

	public void setAltitud(String altitud) {
		this.altitud = altitud;
	}

	public java.lang.String getTemperatura_media() {
		return temperatura_media;
	}

	public void setTemperatura_media(java.lang.String temperatura_media) {
		this.temperatura_media = temperatura_media;
	}

	public String getPrecipitacion() {
		return precipitacion;
	}

	public void setPrecipitacion(String precipitacion) {
		this.precipitacion = precipitacion;
	}

	public String getTemperatura_minima() {
		return temperatura_minima;
	}

	public void setTemperatura_minima(String temperatura_minima) {
		this.temperatura_minima = temperatura_minima;
	}

	public String getHora_temperatura_minima() {
		return hora_temperatura_minima;
	}

	public void setHora_temperatura_minima(String hora_temperatura_minima) {
		this.hora_temperatura_minima = hora_temperatura_minima;
	}

	public String getTemperatura_maxima() {
		return temperatura_maxima;
	}

	public void setTemperatura_maxima(String temperatura_maxima) {
		this.temperatura_maxima = temperatura_maxima;
	}

	public String getHora_temperatura_maxima() {
		return hora_temperatura_maxima;
	}

	public void setHora_temperatura_maxima(String hora_temperatura_maxima) {
		this.hora_temperatura_maxima = hora_temperatura_maxima;
	}

	public String getDireccion_racha_maxima() {
		return direccion_racha_maxima;
	}

	public void setDireccion_racha_maxima(String direccion_racha_maxima) {
		this.direccion_racha_maxima = direccion_racha_maxima;
	}

	public String getVelocidad_media_viento() {
		return velocidad_media_viento;
	}

	public void setVelocidad_media_viento(String velocidad_media_viento) {
		this.velocidad_media_viento = velocidad_media_viento;
	}

	public String getRacha_maxima() {
		return racha_maxima;
	}

	public void setRacha_maxima(String racha_maxima) {
		this.racha_maxima = racha_maxima;
	}

	public String getHoraracha() {
		return horaracha;
	}

	public void setHoraracha(String horaracha) {
		this.horaracha = horaracha;
	}

	public String getInsolacion() {
		return insolacion;
	}

	public void setInsolacion(String insolacion) {
		this.insolacion = insolacion;
	}

	public String getPresion_maxima() {
		return presion_maxima;
	}

	public void setPresion_maxima(String presion_maxima) {
		this.presion_maxima = presion_maxima;
	}

	public String getHora_presion_maxima() {
		return hora_presion_maxima;
	}

	public void setHora_presion_maxima(String hora_presion_maxima) {
		this.hora_presion_maxima = hora_presion_maxima;
	}

	public String getPresion_minima() {
		return presion_minima;
	}

	public void setPresion_minima(String presion_minima) {
		this.presion_minima = presion_minima;
	}

	public String getHora_presion_minima() {
		return hora_presion_minima;
	}

	public void setHora_presion_minima(String hora_presion_minima) {
		this.hora_presion_minima = hora_presion_minima;
	}

	public String getHumedad_media() {
		return humedad_media;
	}

	public void setHumedad_media(String humedad_media) {
		this.humedad_media = humedad_media;
	}

	public String getHumedad_maxima() {
		return humedad_maxima;
	}

	public void setHumedad_maxima(String humedad_maxima) {
		this.humedad_maxima = humedad_maxima;
	}

	public String getHora_humedad_maxima() {
		return hora_humedad_maxima;
	}

	public void setHora_humedad_maxima(String hora_humedad_maxima) {
		this.hora_humedad_maxima = hora_humedad_maxima;
	}

	public java.lang.String getHumedad_minima() {
		return humedad_minima;
	}

	public void setHumedad_minima(java.lang.String humedad_minima) {
		this.humedad_minima = humedad_minima;
	}

	public String getHora_humedad_minima() {
		return hora_humedad_minima;
	}

	public void setHora_humedad_minima(String hora_humedad_minima) {
		this.hora_humedad_minima = hora_humedad_minima;
	}

	@Override
	public String toString() {
		return "ObjetoEstacionHistorico [fecha=" + fecha + ", id_estacion=" + id_estacion + ", nombre=" + nombre
				+ ", provincia=" + provincia + ", altitud=" + altitud + ", temperatura_media=" + temperatura_media
				+ ", precipitacion=" + precipitacion + ", temperatura_minima=" + temperatura_minima
				+ ", hora_temperatura_minima=" + hora_temperatura_minima + ", temperatura_maxima=" + temperatura_maxima
				+ ", hora_temperatura_maxima=" + hora_temperatura_maxima + ", direccion_racha_maxima="
				+ direccion_racha_maxima + ", velocidad_media_viento=" + velocidad_media_viento + ", racha_maxima="
				+ racha_maxima + ", horaracha=" + horaracha + ", insolacion=" + insolacion + ", presion_maxima="
				+ presion_maxima + ", hora_presion_maxima=" + hora_presion_maxima + ", presion_minima=" + presion_minima
				+ ", hora_presion_minima=" + hora_presion_minima + ", humedad_media=" + humedad_media
				+ ", humedad_maxima=" + humedad_maxima + ", hora_humedad_maxima=" + hora_humedad_maxima
				+ ", humedad_minima=" + humedad_minima + ", hora_humedad_minima=" + hora_humedad_minima + "]";
	}
    
    
}
