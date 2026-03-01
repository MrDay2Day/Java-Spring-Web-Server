package org.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.file")
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
        SpringApplication.run(Main.class, args);
    }
}