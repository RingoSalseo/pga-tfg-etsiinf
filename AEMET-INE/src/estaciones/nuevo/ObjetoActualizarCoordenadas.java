package estaciones.nuevo;

import com.google.gson.annotations.SerializedName;

public class ObjetoActualizarCoordenadas {

	private String latitud;
    private String provincia;
    private String altitud;
    
    @SerializedName("indicativo")
    private String id_estacion;
    
    private String nombre;
    private String indsinop;
    private String longitud;

    public ObjetoActualizarCoordenadas() {}

    public ObjetoActualizarCoordenadas(String latitud, String provincia, String altitud, String id_estacion, 
                    String nombre, String indsinop, String longitud) {
        this.latitud = latitud;
        this.provincia = provincia;
        this.altitud = altitud;
        this.id_estacion = id_estacion;
        this.nombre = nombre;
        this.indsinop = indsinop;
        this.longitud = longitud;
    }

    // Getters y Setters
    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
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

    public String getIdEstacion() {
        return id_estacion;
    }

    public void setIdEstacion(String id_estacion) {
        this.id_estacion = id_estacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIndsinop() {
        return indsinop;
    }

    public void setIndsinop(String indsinop) {
        this.indsinop = indsinop;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return "Estacion{" +
                "latitud='" + latitud + '\'' +
                ", provincia='" + provincia + '\'' +
                ", altitud='" + altitud + '\'' +
                ", id_estacion='" + id_estacion + '\'' +
                ", nombre='" + nombre + '\'' +
                ", indsinop='" + indsinop + '\'' +
                ", longitud='" + longitud + '\'' +
                '}';
    }
}
