package objetosPrincipales;

import com.google.gson.annotations.SerializedName;

import objetosAuxiliares.Origen;
import objetosAuxiliares.Prediccion;

public class SegundaRespuesta {

	@SerializedName("origen")
	private Origen origen;
	@SerializedName("elaborado")
	private String elaborado;
	@SerializedName("nombre")
	private String nombre;
	@SerializedName("provincia")
	private String provincia;
	@SerializedName("prediccion")
	private Prediccion prediccion;
	@SerializedName("id")
	private int id;
	@SerializedName("version")
	private double version;
	
	public SegundaRespuesta(Origen origen, String elaborado, String nombre, String provincia, Prediccion prediccion,
			int id, double version) {
		super();
		this.origen = origen;
		this.elaborado = elaborado;
		this.nombre = nombre;
		this.provincia = provincia;
		this.prediccion = prediccion;
		this.id = id;
		this.version = version;
	}
	
	public Origen getOrigen() {
		return origen;
	}
	public void setOrigen(Origen origen) {
		this.origen = origen;
	}
	public String getElaborado() {
		return elaborado;
	}
	public void setElaborado(String elaborado) {
		this.elaborado = elaborado;
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
	public Prediccion getPrediccion() {
		return prediccion;
	}
	public void setPrediccion(Prediccion prediccion) {
		this.prediccion = prediccion;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getVersion() {
		return version;
	}
	public void setVersion(double version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "SegundaRespuesta [origen=" + origen + ", elaborado=" + elaborado + ", nombre=" + nombre + ", provincia="
				+ provincia + ", prediccion=" + prediccion + ", id=" + id + ", version=" + version + "]";
	}
	
	public String imprimirPrediccion() {
		return "";
	}
	
}
