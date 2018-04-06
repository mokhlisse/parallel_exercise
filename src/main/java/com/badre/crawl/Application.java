package com.badre.crawl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.badre.crawl.service.LinkService;

/**
 * Main spring boot Class
 * 
 * @author <a href="mailto:mokhlisse_badre@yahoo.fr">Badre-Edine Mokhlisse</a>
 *
 */
@SpringBootApplication
public class Application {

	private static final Logger logger = LogManager.getLogger(Application.class);

	@Autowired
	LinkService linkService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			logger.info("Let's crawl this url ...");
			linkService.start();
			linkService.printToFile();
		};
	}

}
