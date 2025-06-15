package objetosAuxiliares;

import com.google.gson.annotations.SerializedName;

public class DatoGenericoString {

    @SerializedName("value")
    private String value;

    @SerializedName("periodo")
    private String periodo;

    public DatoGenericoString(String value, String periodo) {
        this.value = value;
        this.periodo = periodo;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
        return "DatoGenericoString [value=" + value + ", periodo=" + periodo + "]";
    }
}
