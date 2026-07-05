package org.fuin.cqrs4j.example.spring.query.app;

import org.fuin.cqrs4j.example.spring.shared.EventstoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;

/**
 * Query side application. The projection/view engine is auto-configured by the
 * {@code cqrs-4-java-springboot-query-starter} (see its {@code Cqrs4jConfig}); the application only needs
 * to expose its own {@code View} beans (scanned from the {@code views} package) plus the event store /
 * serializer wiring from {@code SharedConfig}.
 */
@SpringBootApplication(scanBasePackages = {
        "org.fuin.cqrs4j.example.spring.shared",
        "org.fuin.cqrs4j.example.spring.query.app",
        "org.fuin.cqrs4j.example.spring.query.views"
})
@EnableConfigurationProperties(EventstoreConfig.class)
@EntityScan("org.fuin.cqrs4j.example.spring.query.views")
@EnableScheduling
@EnableAsync
public class QryApplication {

    @Bean("projectorExecutor")
    public Executor taskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("qry-app-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ScheduledTaskRegistrar scheduledTaskRegistrar(TaskScheduler taskScheduler) {
        final ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();
        scheduledTaskRegistrar.setScheduler(taskScheduler);
        return scheduledTaskRegistrar;
    }

    @Bean
    public TaskScheduler threadPoolTaskScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);
        return scheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(QryApplication.class, args);
    }

}
