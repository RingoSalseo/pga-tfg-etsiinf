package objetosAuxiliares;

public class Dato {

	private int value;
    private int hora;
    
	public Dato(int value, int hora) {
		super();
		this.value = value;
		this.hora = hora;
	}
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getHora() {
		return hora;
	}
	public void setHora(int hora) {
		this.hora = hora;
	}
	
	@Override
	public String toString() {
		return "Dato [value=" + value + ", hora=" + hora + "]";
	}
    
    
}
