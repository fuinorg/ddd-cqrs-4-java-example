package org.fuin.cqrs4j.example.spring.query.views.statistics;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * DTO class sent back to the client for the category based statistic.
 *
 * @param category Unique category name (e.g. {@code created} or {@code deleted}).
 * @param count    Number of events counted for the category.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CategoryStatistic(String category, int count) {

}
