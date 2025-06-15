package objetosAuxiliares;

import com.google.gson.annotations.SerializedName;

public class DatoGenerico {

    @SerializedName("value")
    private int value;

    @SerializedName("periodo")
    private String periodo;

    public DatoGenerico(int value, String periodo) {
        this.value = value;
        this.periodo = periodo;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    @Override
    public String toString() {
        return "Data [value=" + value + ", periodo=" + periodo + "]";
    }
}
