package seng468.scalability;

import org.apache.catalina.startup.HomesUserDatabase;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ch.qos.logback.classic.Logger;

@SpringBootApplication
public class ScalabilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScalabilityApplication.class, args);
	}
}
