package org.ticker.auth.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.keycloak.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureOrder(1)
public class KeycloakConfig {

	private static final Logger LOG = LoggerFactory.getLogger(KeycloakConfig.class);

	@Bean
	ServletRegistrationBean<HttpServlet30Dispatcher> application(KeycloakServerProperties properties, DataSource dataSource) throws Exception {
		mockJndi(dataSource);
		KeycloakApplication.keycloakServerProperties = properties;
		ServletRegistrationBean<HttpServlet30Dispatcher> servlet = new ServletRegistrationBean<>(
				new HttpServlet30Dispatcher());
		servlet.addInitParameter("javax.ws.rs.Application", KeycloakApplication.class.getName());
		servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX,
				properties.getContextPath());
		servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS, "true");
		servlet.addUrlMappings(properties.getContextPath() + "/*");
		servlet.setLoadOnStartup(1);
		servlet.setAsyncSupported(true);

		return servlet;
	}

	@Bean
	FilterRegistrationBean<KeycloakRequestFilter> sessionManagement(KeycloakServerProperties keycloakServerProperties) {
	    FilterRegistrationBean<KeycloakRequestFilter> filter = new FilterRegistrationBean<>();
	    filter.setName("Keycloak Session Management");
	    filter.setFilter(new KeycloakRequestFilter());
	    filter.addUrlPatterns(keycloakServerProperties.getContextPath() + "/*");

	    return filter;
	}

	private void mockJndi(DataSource dataSource) throws NamingException {
		NamingManager.setInitialContextFactoryBuilder((env) -> (environment) -> new InitialContext() {

			@Override
			public Object lookup(Name name) {
				return lookup(name.toString());
			}

			@Override
			public Object lookup(String name) {
				if ("spring/datasource".equals(name)) {
					return dataSource;
				} else if (name.startsWith("java:jboss/ee/concurrency/executor/")) {
					return fixedThreadPool();
				}

				return null;
			}

			@Override
			public NameParser getNameParser(String name) {
				return CompositeName::new;
			}

			@Override
			public void close() {
				LOG.info("NamingManager CLOSED");
			}
		});
	}
	
	@Bean("fixedThreadPool")
	public ExecutorService fixedThreadPool() {
		return Executors.newFixedThreadPool(5);
	}

	@Bean
	@ConditionalOnMissingBean(name = "springBootPlatform")
	protected SimplePlatformProvider springBootPlatform() {
		return (SimplePlatformProvider) Platform.getPlatform();
	}
}
