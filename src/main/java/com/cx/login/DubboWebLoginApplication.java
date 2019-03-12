package com.cx.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DubboWebLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboWebLoginApplication.class, args);
    }

}
