package org.dnd.timeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class TimeetApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeetApplication.class, args);
    }

}
