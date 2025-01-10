package com.onesquad.formulafan.config;

import com.onesquad.formulafan.adapter.persistence.GrandPrix;
import com.onesquad.formulafan.adapter.persistence.GrandPrixRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GrandPrixInitializer implements CommandLineRunner {

    private final GrandPrixRepository grandPrixRepository;

    public GrandPrixInitializer(GrandPrixRepository grandPrixRepository) {
        this.grandPrixRepository = grandPrixRepository;
    }

    @Override
    public void run(String... args) {
        List<String> grandPrixNames = List.of(
                "Bahrain Grand Prix",
                "Saudi Arabian Grand Prix",
                "Australian Grand Prix",
                "Japanese Grand Prix",
                "Chinese Grand Prix",
                "Miami Grand Prix",
                "Emilia Romagna Grand Prix",
                "Monaco Grand Prix",
                "Canadian Grand Prix",
                "Spanish Grand Prix",
                "Austrian Grand Prix",
                "British Grand Prix",
                "Hungarian Grand Prix",
                "Belgian Grand Prix",
                "Dutch Grand Prix",
                "Italian Grand Prix",
                "Azerbaijan Grand Prix",
                "Singapore Grand Prix",
                "United States Grand Prix",
                "Mexico City Grand Prix",
                "São Paulo Grand Prix",
                "Las Vegas Grand Prix",
                "Qatar Grand Prix",
                "Abu Dhabi Grand Prix");

        if (grandPrixRepository.count() == 0) {
            grandPrixNames.forEach(name -> {
                GrandPrix grandPrix = new GrandPrix(name);
                grandPrixRepository.save(grandPrix);
            });
            System.out.println("Grand Prix data initialized.");
        } else {
            System.out.println(
                    "Grand Prix data already exists. Skipping initialization.");
        }
    }
}
