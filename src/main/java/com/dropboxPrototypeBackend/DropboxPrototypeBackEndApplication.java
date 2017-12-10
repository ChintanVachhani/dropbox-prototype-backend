package com.dropboxPrototypeBackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dropboxPrototypeBackend"})
@EnableAsync
public class DropboxPrototypeBackEndApplication implements CommandLineRunner {


    public static Set<BigInteger> activeUsers = new HashSet<BigInteger>();

    public static void main(String[] args) {
        SpringApplication.run(DropboxPrototypeBackEndApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
