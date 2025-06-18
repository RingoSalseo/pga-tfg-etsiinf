package api.servicio;

import api.model.Seccion;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ServicioApplicationTests {

	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeccionService seccionService;
    
    @MockBean
    private EstacionService estacionService;
	
	@Test
	void contextLoads() {
	}

	@Test
    void testEndpointCodigoPostal() throws Exception {
		
        Seccion seccionMock = new Seccion();
        seccionMock.setIdSeccion("2812701009");
        seccionMock.setCodigoPostal("28290");
        seccionMock.setLatitud_centroide_seccion(40.45);
        seccionMock.setLongitud_centroide_seccion(-3.88);

        // Configurar el mock del servicio
        Mockito.when(seccionService.obtenerSeccionesPorCodigoPostal("28290"))
                .thenReturn(List.of(seccionMock));

        // Realizar la petición al endpoint
        mockMvc.perform(get("/api/demografico/codigopostal")
                        .param("codigo_postal", "28232")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mediaSecciones.codigoPostal").value("28290"));
    }
	
}
