package objetosAuxiliares;

import java.util.List;

public class GenericoMinMax {

    private int maxima;
    private int minima;
    private List<Dato> dato;

    public GenericoMinMax(int maxima, int minima, List<Dato> dato) {
        this.maxima = maxima;
        this.minima = minima;
        this.dato = dato;
    }

    public int getMaxima() {
        return maxima;
    }

    public void setMaxima(int maxima) {
        this.maxima = maxima;
    }

    public int getMinima() {
        return minima;
    }

    public void setMinima(int minima) {
        this.minima = minima;
    }

    public List<Dato> getDato() {
        return dato;
    }

    public void setDato(List<Dato> dato) {
        this.dato = dato;
    }

    @Override
    public String toString() {
        return "ResumenDatoEnteroConLista [maxima=" + maxima + ", minima=" + minima + "]";
    }
}
