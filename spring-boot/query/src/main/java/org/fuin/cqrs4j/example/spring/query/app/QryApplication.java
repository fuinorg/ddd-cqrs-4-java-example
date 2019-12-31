package org.fuin.cqrs4j.example.spring.query.app;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication(scanBasePackages = { "org.fuin.cqrs4j.example.spring.query.app",
		"org.fuin.cqrs4j.example.spring.query.controller", "org.fuin.cqrs4j.example.spring.query.views.common",
		"org.fuin.cqrs4j.example.spring.query.views.personlist", "org.fuin.cqrs4j.example.spring.shared" })
@EnableJpaRepositories("org.fuin.cqrs4j.example.spring.query.views.common")
@EntityScan({ "org.fuin.cqrs4j.example.spring.query.views.common",
		"org.fuin.cqrs4j.example.spring.query.views.personlist" })
@EnableScheduling
@EnableAsync
public class QryApplication {

	@Bean("projectorExecutor")
	public Executor taskExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("person-");
		executor.initialize();
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(QryApplication.class, args);
	}

}
