package com.example.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
*   https://www.baeldung.com/spring-mvc-tutorial
*   Each application built using Spring Boot needs merely to define the main entry point.
*   This is usually a Java class with the main method, annotated with @SpringBootApplication:
 * */
@SpringBootApplication
public class SelftestingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelftestingApplication.class, args);
    }

}
