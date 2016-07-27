package com.communicator.app;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Starting point of the application.
 * 
 * @author pavan
 *
 */
@SpringBootApplication
public class CommunicatorApplication {

	public static void main(String[] args) {

		// Boot up
		SpringApplication.run(CommunicatorApplication.class, args);
	}

	/**
	 * For h2 console. Only in dev.
	 * 
	 * @return
	 */
	@Bean
	Server h2Server() {
		Server server = new Server();
		try {
			server.runTool("-tcp");
			server.runTool("-tcpAllowOthers");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return server;

	}
}
