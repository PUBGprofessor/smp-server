package com.pubg.smp.smpinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author itning
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SmpInfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpInfoApplication.class, args);
    }

}
