package objetosAuxiliares;

import java.util.List;

public class SensTermica extends GenericoMinMax {

    public SensTermica(int maxima, int minima, List<Dato> dato) {
        super(maxima, minima, dato);
    }

    @Override
    public String toString() {
        return "SensTermica [maxima=" + getMaxima() + ", minima=" + getMinima() + "]";
    }
}
