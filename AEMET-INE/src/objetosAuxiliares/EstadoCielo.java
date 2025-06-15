package objetosAuxiliares;

public class EstadoCielo extends DatoGenericoString {

    private String descripcion;

    public EstadoCielo(String value, String periodo, String descripcion) {
        super(value, periodo);
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "EstadoCielo [value=" + getValue() + ", periodo=" + getPeriodo() + ", descripcion=" + descripcion + "]";
    }
}
