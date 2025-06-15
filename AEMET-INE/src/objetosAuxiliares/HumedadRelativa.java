package objetosAuxiliares;

import java.util.List;

public class HumedadRelativa extends GenericoMinMax {

    public HumedadRelativa(int maxima, int minima, List<Dato> dato) {
        super(maxima, minima, dato);
    }

    @Override
    public String toString() {
        return "HumedadRelativa [maxima=" + getMaxima() + ", minima=" + getMinima() + "]";
    }
}
