package org.ticker.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.ticker.auth.config.KeycloakServerProperties;
@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties({ KeycloakServerProperties.class })
public class TickerAuthorizationServerApp {
	private static final Logger LOG = LoggerFactory.getLogger(TickerAuthorizationServerApp.class);
	public static void main(String[] args) throws Exception {
		SpringApplication.run(TickerAuthorizationServerApp.class, args);
		LOG.info("Keycloak Auth Server started.");
	}

}
