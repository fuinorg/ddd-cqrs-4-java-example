package org.fuin.cqrs4j.example.spring.query.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;

@SpringBootApplication(scanBasePackages = {
        "org.fuin.cqrs4j.springboot.view",
        "org.fuin.cqrs4j.example.spring.query.app",
        "org.fuin.cqrs4j.example.spring.shared",
        "org.fuin.cqrs4j.example.spring.query.views"
})
@EnableJpaRepositories("org.fuin.cqrs4j.")
@EntityScan({
        "org.fuin.cqrs4j.springboot.view",
        "org.fuin.cqrs4j.example.spring.query.views"
})
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
