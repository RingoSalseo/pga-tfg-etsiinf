package estaciones.antiguo;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Estacion {
	
	//Al poner transient, gson evita deserializar dichos atributos, obteniendo unicamente los necesarios

	@SerializedName("idema")
	private String ID_ESTACION;
	
	@SerializedName("lon")
	private float LONGITUD;
	
	@SerializedName("lat")
	private float LATITUD;
	
	@SerializedName("alt")
	private float ALTITUD_ESTACION;
	
	@SerializedName("ubi")
	private String NOMBRE;
	
	@SerializedName("fint")
	private Date FECHA;
	
	@SerializedName("prec")
	private float PRECIPITACION;
	
	private transient float pacutp;
	
	private transient float pliqtp;
	
	private transient float psolt;
	
	@SerializedName("vmax")
	private float VELOCIDAD_MAXIMA_VIENTO;
	
	@SerializedName("vv")
	private float VELOCIDAD_MEDIA_VIENTO;
	
	private transient float vmaxu;
	
	private transient float vvu;
	
	@SerializedName("dv")
	private float DIRECCION_VIENTO;
	
	private transient float dvu;
	
	private transient float dmax;
	
	private transient float dmaxu;
	
	private transient float stdvv;
	
	private transient float stddv;
	
	private transient float stdvvu;
	
	private transient float stddvu;
	
	@SerializedName("hr")
	private float HUMEDAD;
	
	private transient float inso;
	
	@SerializedName("pres")
	private float PRESION_ATMOSFERICA;
	
	private transient float pres_nmar;
	
	@SerializedName("ts")
	private float TEMPERATURA_SUELO;
	
	private transient float tss20cm;
	
	private transient float tss5cm;
	
	private transient float ta;
	
	private transient float tpr;
	
	@SerializedName("tamin")
	private float TEMPERATURA_MINIMA;
	
	@SerializedName("tamax")
	private float TEMPERATURA_MAXIMA;
	
	@SerializedName("vis")
	private float VISIBILIDAD;
	
	private transient float geo700;
	
	private transient float geo850;
	
	private transient float geo925;
	
	private transient float rviento;
	
	@SerializedName("nieve")
	private float ESPESOR_NIEVE;

	public Estacion(String iD_ESTACION, float lONGITUD, float lATITUD, float aLTITUD_ESTACION, String nOMBRE,
			Date fECHA, float pRECIPITACION, float pacutp, float pliqtp, float psolt, float vELOCIDAD_MAXIMA_VIENTO,
			float vELOCIDAD_MEDIA_VIENTO, float vmaxu, float vvu, float dIRECCION_VIENTO, float dvu, float dmax,
			float dmaxu, float stdvv, float stddv, float stdvvu, float stddvu, float hUMEDAD, float inso,
			float pRESION_ATMOSFERICA, float pres_nmar, float tEMPERATURA_SUELO, float tss20cm, float tss5cm, float ta,
			float tpr, float tEMPERATURA_MINIMA, float tEMPERATURA_MAXIMA, float vISIBILIDAD, float geo700,
			float geo850, float geo925, float rviento, float eSPESOR_NIEVE) {
		super();
		ID_ESTACION = iD_ESTACION;
		LONGITUD = lONGITUD;
		LATITUD = lATITUD;
		ALTITUD_ESTACION = aLTITUD_ESTACION;
		NOMBRE = nOMBRE;
		FECHA = fECHA;
		PRECIPITACION = pRECIPITACION;
		this.pacutp = pacutp;
		this.pliqtp = pliqtp;
		this.psolt = psolt;
		VELOCIDAD_MAXIMA_VIENTO = vELOCIDAD_MAXIMA_VIENTO;
		VELOCIDAD_MEDIA_VIENTO = vELOCIDAD_MEDIA_VIENTO;
		this.vmaxu = vmaxu;
		this.vvu = vvu;
		DIRECCION_VIENTO = dIRECCION_VIENTO;
		this.dvu = dvu;
		this.dmax = dmax;
		this.dmaxu = dmaxu;
		this.stdvv = stdvv;
		this.stddv = stddv;
		this.stdvvu = stdvvu;
		this.stddvu = stddvu;
		HUMEDAD = hUMEDAD;
		this.inso = inso;
		PRESION_ATMOSFERICA = pRESION_ATMOSFERICA;
		this.pres_nmar = pres_nmar;
		TEMPERATURA_SUELO = tEMPERATURA_SUELO;
		this.tss20cm = tss20cm;
		this.tss5cm = tss5cm;
		this.ta = ta;
		this.tpr = tpr;
		TEMPERATURA_MINIMA = tEMPERATURA_MINIMA;
		TEMPERATURA_MAXIMA = tEMPERATURA_MAXIMA;
		VISIBILIDAD = vISIBILIDAD;
		this.geo700 = geo700;
		this.geo850 = geo850;
		this.geo925 = geo925;
		this.rviento = rviento;
		ESPESOR_NIEVE = eSPESOR_NIEVE;
	}

	public String getID_ESTACION() {
		return ID_ESTACION;
	}

	public void setID_ESTACION(String iD_ESTACION) {
		ID_ESTACION = iD_ESTACION;
	}

	public float getLONGITUD() {
		return LONGITUD;
	}

	public void setLONGITUD(float lONGITUD) {
		LONGITUD = lONGITUD;
	}

	public float getLATITUD() {
		return LATITUD;
	}

	public void setLATITUD(float lATITUD) {
		LATITUD = lATITUD;
	}

	public float getALTITUD_ESTACION() {
		return ALTITUD_ESTACION;
	}

	public void setALTITUD_ESTACION(float aLTITUD_ESTACION) {
		ALTITUD_ESTACION = aLTITUD_ESTACION;
	}

	public String getNOMBRE() {
		return NOMBRE;
	}

	public void setNOMBRE(String nOMBRE) {
		NOMBRE = nOMBRE;
	}

	public Date getFECHA() {
		return FECHA;
	}

	public void setFECHA(Date fECHA) {
		FECHA = fECHA;
	}

	public float getPRECIPITACION() {
		return PRECIPITACION;
	}

	public void setPRECIPITACION(float pRECIPITACION) {
		PRECIPITACION = pRECIPITACION;
	}

	public float getVELOCIDAD_MAXIMA_VIENTO() {
		return VELOCIDAD_MAXIMA_VIENTO;
	}

	public void setVELOCIDAD_MAXIMA_VIENTO(float vELOCIDAD_MAXIMA_VIENTO) {
		VELOCIDAD_MAXIMA_VIENTO = vELOCIDAD_MAXIMA_VIENTO;
	}

	public float getVELOCIDAD_MEDIA_VIENTO() {
		return VELOCIDAD_MEDIA_VIENTO;
	}

	public void setVELOCIDAD_MEDIA_VIENTO(float vELOCIDAD_MEDIA_VIENTO) {
		VELOCIDAD_MEDIA_VIENTO = vELOCIDAD_MEDIA_VIENTO;
	}

	public float getDIRECCION_VIENTO() {
		return DIRECCION_VIENTO;
	}

	public void setDIRECCION_VIENTO(float dIRECCION_VIENTO) {
		DIRECCION_VIENTO = dIRECCION_VIENTO;
	}

	public float getHUMEDAD() {
		return HUMEDAD;
	}

	public void setHUMEDAD(float hUMEDAD) {
		HUMEDAD = hUMEDAD;
	}

	public float getPRESION_ATMOSFERICA() {
		return PRESION_ATMOSFERICA;
	}

	public void setPRESION_ATMOSFERICA(float pRESION_ATMOSFERICA) {
		PRESION_ATMOSFERICA = pRESION_ATMOSFERICA;
	}

	public float getTEMPERATURA_SUELO() {
		return TEMPERATURA_SUELO;
	}

	public void setTEMPERATURA_SUELO(float tEMPERATURA_SUELO) {
		TEMPERATURA_SUELO = tEMPERATURA_SUELO;
	}

	public float getTEMPERATURA_MINIMA() {
		return TEMPERATURA_MINIMA;
	}

	public void setTEMPERATURA_MINIMA(float tEMPERATURA_MINIMA) {
		TEMPERATURA_MINIMA = tEMPERATURA_MINIMA;
	}

	public float getTEMPERATURA_MAXIMA() {
		return TEMPERATURA_MAXIMA;
	}

	public void setTEMPERATURA_MAXIMA(float tEMPERATURA_MAXIMA) {
		TEMPERATURA_MAXIMA = tEMPERATURA_MAXIMA;
	}

	public float getVISIBILIDAD() {
		return VISIBILIDAD;
	}

	public void setVISIBILIDAD(float vISIBILIDAD) {
		VISIBILIDAD = vISIBILIDAD;
	}

	public float getESPESOR_NIEVE() {
		return ESPESOR_NIEVE;
	}

	public void setESPESOR_NIEVE(float eSPESOR_NIEVE) {
		ESPESOR_NIEVE = eSPESOR_NIEVE;
	}

	@Override
	public String toString() {
		return "Estacion [ID_ESTACION=" + ID_ESTACION + ", \n\t LONGITUD=" + LONGITUD + ", \n\t LATITUD=" + LATITUD
				+ ", \n\t ALTITUD_ESTACION=" + ALTITUD_ESTACION + ", \n\t NOMBRE=" + NOMBRE + ", \n\t FECHA=" + FECHA
				+ ", \n\t PRECIPITACION=" + PRECIPITACION + ", \n\t VELOCIDAD_MAXIMA_VIENTO=" + VELOCIDAD_MAXIMA_VIENTO
				+ ", \n\t VELOCIDAD_MEDIA_VIENTO=" + VELOCIDAD_MEDIA_VIENTO + ", \n\t DIRECCION_VIENTO=" + DIRECCION_VIENTO
				+ ", \n\t HUMEDAD=" + HUMEDAD + ", \n\t PRESION_ATMOSFERICA=" + PRESION_ATMOSFERICA + ", \n\t TEMPERATURA_SUELO="
				+ TEMPERATURA_SUELO + ", \n\t TEMPERATURA_MINIMA=" + TEMPERATURA_MINIMA + ", \n\t TEMPERATURA_MAXIMA="
				+ TEMPERATURA_MAXIMA + ", \n\t VISIBILIDAD=" + VISIBILIDAD + ", \n\t ESPESOR_NIEVE=" + ESPESOR_NIEVE + "]";
	}
	
	
	
}
