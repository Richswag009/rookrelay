package com.richcodes.hookrelay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableCaching
//@EnableScheduling
public class HookrelayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HookrelayApplication.class, args);
	}

}
