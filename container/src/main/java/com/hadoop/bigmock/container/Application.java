package com.hadoop.bigmock.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		log.info("\n\n\n$$$$$$$$$$$$$$$$$$$$$$ before spring\n\n\n");
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		while (true) {
			log.info("\n\n++++++++++++++\n\nBigMock Container running ...");
//			Thread.sleep(3000);
//		}
	}

}
