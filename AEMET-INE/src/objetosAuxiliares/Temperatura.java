package objetosAuxiliares;

import java.util.List;

public class Temperatura extends GenericoMinMax {

    public Temperatura(int maxima, int minima, List<Dato> dato) {
        super(maxima, minima, dato);
    }

    @Override
    public String toString() {
        return "Temperatura [maxima=" + getMaxima() + ", minima=" + getMinima() + "]";
    }
}
