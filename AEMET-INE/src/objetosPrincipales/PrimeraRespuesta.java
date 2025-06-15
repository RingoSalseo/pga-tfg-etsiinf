package objetosPrincipales;

import com.google.gson.annotations.SerializedName;

public class PrimeraRespuesta {

	@SerializedName("descripcion")
	private String descripcion;
	@SerializedName("estado")
	private int estado;
	@SerializedName("datos")
	private String datos;
	@SerializedName("metadatos")
	private String metadatos;

	public PrimeraRespuesta(String descripcion, int estado, String datos, String metadatos) {
		this.descripcion = descripcion;
		this.estado = estado;
		this.datos = datos;
		this.metadatos = metadatos;
	}

	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public String getDatos() {
		return datos;
	}
	public void setDatos(String datos) {
		this.datos = datos;
	}
	public String getMetadatos() {
		return metadatos;
	}
	public void setMetadatos(String metadatos) {
		this.metadatos = metadatos;
	}

	@Override
	public String toString() {
		return "PrimeraRespuesta [descripcion=" + descripcion + ", estado=" + estado + ", datos=" + datos
				+ ", metadatos=" + metadatos + "]";
	}

}
