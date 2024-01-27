package br.com.motur.dealbackendservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableCaching
@EnableSpringDataWebSupport
public class DealBackendServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DealBackendServiceApplication.class, args);
	}

}
