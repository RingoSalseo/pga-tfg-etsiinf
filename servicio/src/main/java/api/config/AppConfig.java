package api.config;


import api.utils.CodigoAuxiliar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CodigoAuxiliar codigoAuxiliar() {
        return new CodigoAuxiliar();
    }
}


