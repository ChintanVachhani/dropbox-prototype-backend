package com.dropboxPrototypeBackend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dropboxPrototypeBackend"})
@EnableAsync
public class DropboxPrototypeBackEndApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DropboxPrototypeBackEndApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
