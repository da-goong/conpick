package com.team1.conpick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ConpickApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConpickApplication.class, args);
    }

}
