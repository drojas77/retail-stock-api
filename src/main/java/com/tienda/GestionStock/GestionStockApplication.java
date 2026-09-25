package com.tienda.GestionStock;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class GestionStockApplication {

    @PostConstruct
    public void init() {
        // Fuerza la zona horaria por defecto para toda la JVM de la app
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Madrid"));
    }

	public static void main(String[] args) {

        SpringApplication.run(GestionStockApplication.class, args);
	}

}
