package com.varkalys.homework4;

import com.varkalys.homework4.service.SeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Homework4Application implements CommandLineRunner {

    private final SeedService seedService;

    @Autowired
    public Homework4Application(SeedService seedService) {
        this.seedService = seedService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Homework4Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        seedService.seed();
    }
}
