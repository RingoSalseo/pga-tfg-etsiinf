package api.model;
import jakarta.persistence.*;

@Entity
@Table(name = "secciones")
public class Seccion {

	@Id
    @Column(name = "id_seccion")
	private String idSeccion;
	
	@Column(name = "id_distrito")
    private String idDistrito;
	
	@Column(name = "nombre_seccion")
    private String nombreSeccion;
	
	@Column(name = "codigo_postal")
    private String codigoPostal;
	
	@Column(name = "renta_neta_media_persona")
    private Integer rentaNetaMediaPersona;
	
	@Column(name = "renta_neta_media_hogar")
    private Integer rentaNetaMediaHogar;
	
	@Column(name = "renta_unidad_consumo")
    private Integer rentaUnidadConsumo;
	
	@Column(name = "mediana_renta_consumo")
    private Integer medianaRentaConsumo;
	
	@Column(name = "renta_bruta_media_persona")
    private Integer rentaBrutaMediaPersona;
	
	@Column(name = "renta_bruta_media_hogar")
    private Integer rentaBrutaMediaHogar;
	
	@Column(name = "edad_media_poblacion")
    private Double edadMediaPoblacion;
	
	@Column(name = "porcentaje_menor_18")
    private Double porcentajeMenor18;
	
	@Column(name = "porcentaje_mayor_65")
    private Double porcentajeMayor65;
	
	@Column(name = "tamaño_medio_hogar")
    private Double tamañoMedioHogar;
	
	@Column(name = "porcentaje_hogares_unipersonales")
    private Double porcentajeHogaresUnipersonales;
	
	@Column(name = "poblacion")
    private Integer poblacion;
	
	@Column(name = "porcentaje_poblacion_española")
    private Double porcentajePoblacionEspañola;
    
	@Column(name = "fuente_ingresos_salario")
    private Integer fuenteIngresosSalario;
    
	@Column(name = "fuente_ingresos_pensiones")
    private Integer fuenteIngresosPensiones;
    
	@Column(name = "fuente_ingresos_pdesempleado")
    private Integer fuenteIngresosPDesempleado;
    
	@Column(name = "fuente_ingresos_otrprestaciones")
    private Integer fuenteIngresosOtrPrestaciones;
    
	@Column(name = "fuente_ingresos_otringresos")
    private Integer fuenteIngresosOtrIngresos;
    
	@Column(name = "latitud_centroide_seccion")
    private Double latitud_centroide_seccion;
    
	@Column(name = "longitud_centroide_seccion")
    private Double longitud_centroide_seccion;

    // Constructor vacío (obligatorio para JPA)
    public Seccion() {}

    // Constructor completo
    public Seccion(String idSeccion, String idDistrito, String nombreSeccion, String codigoPostal,
    		Integer rentaNetaMediaPersona, Integer rentaNetaMediaHogar, Integer rentaUnidadConsumo,
    		Integer medianaRentaConsumo, Integer rentaBrutaMediaPersona, Integer rentaBrutaMediaHogar,
    		Double edadMediaPoblacion, Double porcentajeMenor18, Double porcentajeMayor65,
                   Double tamañoMedioHogar, Double porcentajeHogaresUnipersonales, Integer poblacion,
                   Double porcentajePoblacionEspañola, Integer fuenteIngresosSalario, Integer fuenteIngresosPensiones,
                   Integer fuenteIngresosPDesempleado, Integer fuenteIngresosOtrPrestaciones, Integer fuenteIngresosOtrIngresos,
                   Double latitud_centroide_seccion, Double longitud_centroide_seccion) {
        this.idSeccion = idSeccion;
        this.idDistrito = idDistrito;
        this.nombreSeccion = nombreSeccion;
        this.codigoPostal = codigoPostal;
        this.rentaNetaMediaPersona = rentaNetaMediaPersona;
        this.rentaNetaMediaHogar = rentaNetaMediaHogar;
        this.rentaUnidadConsumo = rentaUnidadConsumo;
        this.medianaRentaConsumo = medianaRentaConsumo;
        this.rentaBrutaMediaPersona = rentaBrutaMediaPersona;
        this.rentaBrutaMediaHogar = rentaBrutaMediaHogar;
        this.edadMediaPoblacion = edadMediaPoblacion;
        this.porcentajeMenor18 = porcentajeMenor18;
        this.porcentajeMayor65 = porcentajeMayor65;
        this.tamañoMedioHogar = tamañoMedioHogar;
        this.porcentajeHogaresUnipersonales = porcentajeHogaresUnipersonales;
        this.poblacion = poblacion;
        this.porcentajePoblacionEspañola = porcentajePoblacionEspañola;
        this.fuenteIngresosSalario = fuenteIngresosSalario;
        this.fuenteIngresosPensiones = fuenteIngresosPensiones;
        this.fuenteIngresosPDesempleado = fuenteIngresosPDesempleado;
        this.fuenteIngresosOtrPrestaciones = fuenteIngresosOtrPrestaciones;
        this.fuenteIngresosOtrIngresos = fuenteIngresosOtrIngresos;
        this.latitud_centroide_seccion = latitud_centroide_seccion;
        this.longitud_centroide_seccion = longitud_centroide_seccion;
    }

	public String getIdSeccion() {
		return idSeccion;
	}

	public void setIdSeccion(String idSeccion) {
		this.idSeccion = idSeccion;
	}

	public String getIdDistrito() {
		return idDistrito;
	}

	public void setIdDistrito(String idDistrito) {
		this.idDistrito = idDistrito;
	}

	public String getNombreSeccion() {
		return nombreSeccion;
	}

	public void setNombreSeccion(String nombreSeccion) {
		this.nombreSeccion = nombreSeccion;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public int getRentaNetaMediaPersona() {
        return rentaNetaMediaPersona != null ? rentaNetaMediaPersona : 0;
    }

	public void setRentaNetaMediaPersona(Integer rentaNetaMediaPersona) {
		this.rentaNetaMediaPersona = rentaNetaMediaPersona;
	}

	public int getRentaNetaMediaHogar() {
        return rentaNetaMediaHogar != null ? rentaNetaMediaHogar : 0;
    }

	public void setRentaNetaMediaHogar(Integer rentaNetaMediaHogar) {
		this.rentaNetaMediaHogar = rentaNetaMediaHogar;
	}

	public int getRentaUnidadConsumo() {
        return rentaUnidadConsumo != null ? rentaUnidadConsumo : 0;
    }

	public void setRentaUnidadConsumo(Integer rentaUnidadConsumo) {
		this.rentaUnidadConsumo = rentaUnidadConsumo;
	}

	public int getMedianaRentaConsumo() {
        return medianaRentaConsumo != null ? medianaRentaConsumo : 0;
    }

	public void setMedianaRentaConsumo(Integer medianaRentaConsumo) {
		this.medianaRentaConsumo = medianaRentaConsumo;
	}

	public int getRentaBrutaMediaPersona() {
        return rentaBrutaMediaPersona != null ? rentaBrutaMediaPersona : 0;
    }

	public void setRentaBrutaMediaPersona(Integer rentaBrutaMediaPersona) {
		this.rentaBrutaMediaPersona = rentaBrutaMediaPersona;
	}

	public int getRentaBrutaMediaHogar() {
        return rentaBrutaMediaHogar != null ? rentaBrutaMediaHogar : 0;
    }

	public void setRentaBrutaMediaHogar(Integer rentaBrutaMediaHogar) {
		this.rentaBrutaMediaHogar = rentaBrutaMediaHogar;
	}

	public double getEdadMediaPoblacion() {
        return edadMediaPoblacion != null ? edadMediaPoblacion : 0.0;
    }

	public void setEdadMediaPoblacion(Double edadMediaPoblacion) {
		this.edadMediaPoblacion = edadMediaPoblacion;
	}

	public double getPorcentajeMenor18() {
        return porcentajeMenor18 != null ? porcentajeMenor18 : 0.0;
    }

	public void setPorcentajeMenor18(Double porcentajeMenor18) {
		this.porcentajeMenor18 = porcentajeMenor18;
	}

	public double getPorcentajeMayor65() {
        return porcentajeMayor65 != null ? porcentajeMayor65 : 0.0;
    }

	public void setPorcentajeMayor65(Double porcentajeMayor65) {
		this.porcentajeMayor65 = porcentajeMayor65;
	}

	public double getTamañoMedioHogar() {
        return tamañoMedioHogar != null ? tamañoMedioHogar : 0.0;
    }

	public void setTamañoMedioHogar(Double tamañoMedioHogar) {
		this.tamañoMedioHogar = tamañoMedioHogar;
	}

	public double getPorcentajeHogaresUnipersonales() {
        return porcentajeHogaresUnipersonales != null ? porcentajeHogaresUnipersonales : 0.0;
    }

	public void setPorcentajeHogaresUnipersonales(Double porcentajeHogaresUnipersonales) {
		this.porcentajeHogaresUnipersonales = porcentajeHogaresUnipersonales;
	}

	public int getPoblacion() {
        return poblacion != null ? poblacion : 0;
    }

	public void setPoblacion(Integer poblacion) {
		this.poblacion = poblacion;
	}

	public double getPorcentajePoblacionEspañola() {
        return porcentajePoblacionEspañola != null ? porcentajePoblacionEspañola : 0.0;
    }

	public void setPorcentajePoblacionEspañola(Double porcentajePoblacionEspañola) {
		this.porcentajePoblacionEspañola = porcentajePoblacionEspañola;
	}

	public int getFuenteIngresosSalario() {
        return fuenteIngresosSalario != null ? fuenteIngresosSalario : 0;
    }

	public void setFuenteIngresosSalario(Integer fuenteIngresosSalario) {
		this.fuenteIngresosSalario = fuenteIngresosSalario;
	}

	public int getFuenteIngresosPensiones() {
        return fuenteIngresosPensiones != null ? fuenteIngresosPensiones : 0;
    }

	public void setFuenteIngresosPensiones(Integer fuenteIngresosPensiones) {
		this.fuenteIngresosPensiones = fuenteIngresosPensiones;
	}

	public int getFuenteIngresosPDesempleado() {
        return fuenteIngresosPDesempleado != null ? fuenteIngresosPDesempleado : 0;
    }

	public void setFuenteIngresosPDesempleado(Integer fuenteIngresosPDesempleado) {
		this.fuenteIngresosPDesempleado = fuenteIngresosPDesempleado;
	}

	public int getFuenteIngresosOtrPrestaciones() {
        return fuenteIngresosOtrPrestaciones != null ? fuenteIngresosOtrPrestaciones : 0;
    }

	public void setFuenteIngresosOtrPrestaciones(Integer fuenteIngresosOtrPrestaciones) {
		this.fuenteIngresosOtrPrestaciones = fuenteIngresosOtrPrestaciones;
	}

	public int getFuenteIngresosOtrIngresos() {
        return fuenteIngresosOtrIngresos != null ? fuenteIngresosOtrIngresos : 0;
    }

	public void setFuenteIngresosOtrIngresos(Integer fuenteIngresosOtrIngresos) {
		this.fuenteIngresosOtrIngresos = fuenteIngresosOtrIngresos;
	}

	public double getLatitud_centroide_seccion() {
        return latitud_centroide_seccion != null ? latitud_centroide_seccion : 0.0;
    }


	public void setLatitud_centroide_seccion(Double latitud_centroide_seccion) {
		this.latitud_centroide_seccion = latitud_centroide_seccion;
	}

	public double getLongitud_centroide_seccion() {
        return longitud_centroide_seccion != null ? longitud_centroide_seccion : 0.0;
    }

	public void setLongitud_centroide_seccion(Double longitud_centroide_seccion) {
		this.longitud_centroide_seccion = longitud_centroide_seccion;
	}
	
}
