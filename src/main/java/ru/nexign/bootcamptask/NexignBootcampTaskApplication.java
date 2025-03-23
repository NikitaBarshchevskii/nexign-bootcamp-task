package ru.nexign.bootcamptask;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.nexign.bootcamptask.service.CDRGeneratorService;

@SpringBootApplication
public class NexignBootcampTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexignBootcampTaskApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CDRGeneratorService generatorService){
        return args -> generatorService.generate();
    }
}
