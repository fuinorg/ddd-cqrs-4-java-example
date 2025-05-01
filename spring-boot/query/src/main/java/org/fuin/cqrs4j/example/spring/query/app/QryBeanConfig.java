package org.fuin.cqrs4j.example.spring.query.app;

import org.fuin.cqrs4j.example.spring.query.views.personlist.PersonListView;
import org.fuin.cqrs4j.example.spring.query.views.statistics.StatisticView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QryBeanConfig {

    @Bean
    public PersonListView personListView() {
        return new PersonListView();
    }

    @Bean
    public StatisticView statisticView() {
        return new StatisticView();
    }

}
