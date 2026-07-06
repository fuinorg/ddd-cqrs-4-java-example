package org.fuin.cqrs4j.example.quarkus.query.views.statistic;

import jakarta.json.bind.annotation.JsonbCreator;

/**
 * DTO class sent back to the client for the category based statistic.
 *
 * @param category Unique category name (e.g. {@code created} or {@code deleted}).
 * @param count    Number of events counted for the category.
 */
public record CategoryStatistic(String category, int count) {

    @JsonbCreator
    public CategoryStatistic {
    }

}
