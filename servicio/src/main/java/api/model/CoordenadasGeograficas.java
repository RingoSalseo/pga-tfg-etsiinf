package api.model;

public class CoordenadasGeograficas {

	private String id_seccion;
	private double latitud;
	private double longitud;
	
	 public CoordenadasGeograficas() {}
	 
	 public CoordenadasGeograficas(String id_seccion, double latitud, double longitud) {
		 this.id_seccion = id_seccion;
		 this.latitud = latitud;
		 this.longitud = longitud;
	 }

	public String getId_seccion() {
		return id_seccion;
	}

	public void setId_seccion(String id_seccion) {
		this.id_seccion = id_seccion;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}
	
}
