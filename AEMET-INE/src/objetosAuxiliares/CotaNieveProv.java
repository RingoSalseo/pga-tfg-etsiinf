package objetosAuxiliares;

public class CotaNieveProv extends DatoGenericoString {

    public CotaNieveProv(String value, String periodo) {
        super(value, periodo);
    }

    @Override
    public String toString() {
        return "CotaNieveProv [value=" + getValue() + ", periodo=" + getPeriodo() + "]";
    }
}
