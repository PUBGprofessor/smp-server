package com.pubg.smp.smpgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * @author itning
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SmpGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpGatewayApplication.class, args);
    }

}
