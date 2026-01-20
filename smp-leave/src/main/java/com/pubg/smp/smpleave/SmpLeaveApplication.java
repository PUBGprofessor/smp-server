package com.pubg.smp.smpleave;

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
public class SmpLeaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpLeaveApplication.class, args);
    }

}
