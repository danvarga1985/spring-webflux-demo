package com.danvarga.reactordemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class ReactorDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactorDemoApplication.class, args);
    }

}
