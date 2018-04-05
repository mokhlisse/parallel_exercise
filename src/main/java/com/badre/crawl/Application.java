package com.badre.crawl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.badre.crawl.service.UrlService;

/**
 * Main spring boot Class
 * 
 * @author <a href="mailto:mokhlisse_badre@yahoo.fr">Badre-Edine Mokhlisse</a>
 *
 */
@SpringBootApplication
public class Application {

	@Autowired
	UrlService urlService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's crawl this url ...");
			urlService.start();
			Thread.sleep(2000);
			urlService.urlsToFile();
			urlService.destroy();
		};
	}

}
