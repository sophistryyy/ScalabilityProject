package seng468.scalability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ScalabilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScalabilityApplication.class, args);
	}
}
