package objetosAuxiliares;

public class RachaMax extends DatoGenericoString {

    public RachaMax(String value, String periodo) {
        super(value, periodo);
    }

    @Override
    public String toString() {
        return "RachaMax [value=" + getValue() + ", periodo=" + getPeriodo() + "]";
    }
}
