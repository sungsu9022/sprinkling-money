package com.kakaopay.sprinklingmoney;

import com.kakaopay.sprinklingmoney.config.RootConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {RootConfig.class})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(new Class[] { Application.class }, args);
	}
}
