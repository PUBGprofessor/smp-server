package com.pubg.smp.smpstatistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author itning
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class SmpStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpStatisticsApplication.class, args);
    }

}
