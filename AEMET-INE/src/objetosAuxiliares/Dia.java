package objetosAuxiliares;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Dia {

	private List<ProbPrecipitacion> probPrecipitacion;
    private List<CotaNieveProv> cotaNieveProv;
    private List<EstadoCielo> estadoCielo;
    private List<Viento> viento;
    private List<RachaMax> rachaMax;
    private Temperatura temperatura;
    private SensTermica sensTermica;
    private HumedadRelativa humedadRelativa;
    private int uvMax;
    private String fecha;
    
	public Dia(List<ProbPrecipitacion> probPrecipitacion, List<CotaNieveProv> cotaNieveProv,
			List<EstadoCielo> estadoCielo, List<Viento> viento, List<RachaMax> rachaMax, Temperatura temperatura,
			SensTermica sensTermica, HumedadRelativa humedadRelativa, int uvMax, String fecha) {
		super();
		this.probPrecipitacion = probPrecipitacion;
		this.cotaNieveProv = cotaNieveProv;
		this.estadoCielo = estadoCielo;
		this.viento = viento;
		this.rachaMax = rachaMax;
		this.temperatura = temperatura;
		this.sensTermica = sensTermica;
		this.humedadRelativa = humedadRelativa;
		this.uvMax = uvMax;
		this.fecha = fecha;
	}

	public List<ProbPrecipitacion> getProbPrecipitacion() {
		return probPrecipitacion;
	}

	public void setProbPrecipitacion(List<ProbPrecipitacion> probPrecipitacion) {
		this.probPrecipitacion = probPrecipitacion;
	}

	public List<CotaNieveProv> getCotaNieveProv() {
		return cotaNieveProv;
	}

	public void setCotaNieveProv(List<CotaNieveProv> cotaNieveProv) {
		this.cotaNieveProv = cotaNieveProv;
	}

	public List<EstadoCielo> getEstadoCielo() {
		return estadoCielo;
	}

	public void setEstadoCielo(List<EstadoCielo> estadoCielo) {
		this.estadoCielo = estadoCielo;
	}

	public List<Viento> getViento() {
		return viento;
	}

	public void setViento(List<Viento> viento) {
		this.viento = viento;
	}

	public List<RachaMax> getRachaMax() {
		return rachaMax;
	}

	public void setRachaMax(List<RachaMax> rachaMax) {
		this.rachaMax = rachaMax;
	}

	public Temperatura getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(Temperatura temperatura) {
		this.temperatura = temperatura;
	}

	public SensTermica getSensTermica() {
		return sensTermica;
	}

	public void setSensTermica(SensTermica sensTermica) {
		this.sensTermica = sensTermica;
	}

	public HumedadRelativa getHumedadRelativa() {
		return humedadRelativa;
	}

	public void setHumedadRelativa(HumedadRelativa humedadRelativa) {
		this.humedadRelativa = humedadRelativa;
	}

	public int getUvMax() {
		return uvMax;
	}

	public void setUvMax(int uvMax) {
		this.uvMax = uvMax;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	@Override
	public String toString() {
		return "Dia [\n" + probPrecipitacion + ",\n " + cotaNieveProv + ",\n "
				+ estadoCielo + ",\n " + viento + ",\n " + rachaMax + ",\n " + temperatura
				+ ",\n " + sensTermica + ",\n " + humedadRelativa + ",\n " + "uvMax =" + uvMax
				+ ",\n " + "fecha = " + fecha + "\n]";
	}

	
    
}
