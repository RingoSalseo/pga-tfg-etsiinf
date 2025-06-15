package api.model;

import java.util.List;

import api.dto.MedicionDTO;

public class ObjetoRespuesta {

	
	private List<MedicionDTO> medicionesMediaEstacionesCercanas;
	private Seccion mediaSecciones;
/*	
	private String id_municipio;
	private double latitud;
	private double longitud;
	
	//INE
	private String idSeccion;
    private String idDistrito;
    private String nombreSeccion;
    private String codigoPostal;
    private int rentaNetaMediaPersona;
    private int rentaNetaMediaHogar;
    private int rentaUnidadConsumo;
    private int medianaRentaConsumo;
    private int rentaBrutaMediaPersona;
    private int rentaBrutaMediaHogar;
    private double edadMediaPoblacion;
    private double porcentajeMenor18;
    private double porcentajeMayor65;
    private double tamañoMedioHogar;
    private double porcentajeHogaresUnipersonales;
    private int poblacion;
    private double porcentajePoblacionEspañola;
    private int fuenteIngresosSalario;
    private int fuenteIngresosPensiones;
    private int fuenteIngresosPDesempleado;
    private int fuenteIngresosOtrPrestaciones;
    private int fuenteIngresosOtrIngresos;
    
    // Estacion
    private String[] id_estacion;
    private String[] nombre;
    private String[] provincia;
    private String[] altitud;
    private String temperatura_media;
    private String precipitacion;
    private String temperatura_minima;
    private String hora_temperatura_minima;
    private String temperatura_maxima;
    private String hora_temperatura_maxima;
    private String direccion_racha_maxima; 
    private String velocidad_media_viento;    
    private String racha_maxima;   
    private String horaracha;   
    private String insolacion;
    private String presion_maxima;
    private String hora_presion_maxima;
    private String presion_minima;
    private String hora_presion_minima;
    private String humedad_media;
    private String humedad_maxima;
    private String hora_humedad_maxima;
    private String humedad_minima;
    private String hora_humedad_minima;
*/	
	public ObjetoRespuesta() {}

	public ObjetoRespuesta(List<MedicionDTO> medicionesMediaEstacionesCercanas, Seccion mediaSecciones) {
		this.medicionesMediaEstacionesCercanas = medicionesMediaEstacionesCercanas;
		this.mediaSecciones = mediaSecciones;
	}

	public List<MedicionDTO> getMedicionesMediaEstacionesCercanas() {
		return medicionesMediaEstacionesCercanas;
	}

	public void setMedicionesMediaEstacionesCercanas(List<MedicionDTO> medicionesMediaEstacionesCercanas) {
		this.medicionesMediaEstacionesCercanas = medicionesMediaEstacionesCercanas;
	}

	public Seccion getMediaSecciones() {
		return mediaSecciones;
	}

	public void setMediaSecciones(Seccion mediaSecciones) {
		this.mediaSecciones = mediaSecciones;
	}

}
