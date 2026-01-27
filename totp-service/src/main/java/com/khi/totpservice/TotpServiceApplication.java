package com.khi.totpservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class TotpServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TotpServiceApplication.class, args);
    }

}
