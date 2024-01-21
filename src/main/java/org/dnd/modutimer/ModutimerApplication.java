package org.dnd.modutimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class ModutimerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModutimerApplication.class, args);
	}

}
