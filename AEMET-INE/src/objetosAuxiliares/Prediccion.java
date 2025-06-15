package objetosAuxiliares;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Prediccion {

	@SerializedName("dia")
	private List<Dia> dia;

	
	
    public Prediccion(List<Dia> dia) {
		super();
		this.dia = dia;
	}

	public List<Dia> getDia() {
        return dia;
    }

    public void setDia(List<Dia> dia) {
        this.dia = dia;
    }

	@Override
	public String toString() {
		return "Prediccion " + dia + "\n\n";
	}
    
}
