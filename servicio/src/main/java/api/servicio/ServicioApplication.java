package api.servicio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "api.repository")
@EntityScan(basePackages = "api.model")
@ComponentScan(basePackages = {"api.servicio", "api.utils"})
@SpringBootApplication
public class ServicioApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioApplication.class, args);
		System.out.println("Hello World!");
	}

}