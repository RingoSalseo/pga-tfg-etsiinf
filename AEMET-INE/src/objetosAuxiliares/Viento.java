package objetosAuxiliares;

public class Viento {

	private String direccion;
    private int velocidad;
    private String periodo;
	public Viento(String direccion, int velocidad, String periodo) {
		super();
		this.direccion = direccion;
		this.velocidad = velocidad;
		this.periodo = periodo;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public int getVelocidad() {
		return velocidad;
	}
	public void setVelocidad(int velocidad) {
		this.velocidad = velocidad;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	@Override
	public String toString() {
		return "Viento [direccion=" + direccion + ", velocidad=" + velocidad + ", periodo=" + periodo + "]";
	}
    
}
