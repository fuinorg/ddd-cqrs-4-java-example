package org.fuin.cqrs4j.example.quarkus.query.views.statistic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Read model of the {@link StatisticViewCategories} projection: the number of events counted per category
 * (e.g. {@code created} for {@code GenesisEvent}, {@code deleted} for {@code ExodusEvent}), derived purely
 * from the category marker interfaces - independent of the concrete event types.
 */
@Entity
@Table(name = "STATISTIC_CATEGORIES")
@NamedQuery(name = StatisticCategoriesEntity.FIND_ALL,
        query = "SELECT new org.fuin.cqrs4j.example.quarkus.query.views.statistic.CategoryStatistic(s.category, s.count) FROM StatisticCategoriesEntity s")
public class StatisticCategoriesEntity {

    public static final String FIND_ALL = "StatisticCategoriesEntity.findAll";

    /** Maximum length of a category name. */
    public static final int MAX_LENGTH = 30;

    @Id
    @Column(name = "CATEGORY", nullable = false, length = MAX_LENGTH, updatable = false)
    @NotNull
    private String category;

    @Column(name = "COUNT", updatable = true)
    private int count;

    /**
     * JPA default constructor.
     */
    protected StatisticCategoriesEntity() {
    }

    /**
     * Constructor with a given category that sets the number of occurrences to one.
     *
     * @param category Unique category name.
     */
    public StatisticCategoriesEntity(@NotNull String category) {
        this.category = Objects.requireNonNull(category, "category==null");
        this.count = 1;
    }

    public CategoryStatistic toDto() {
        return new CategoryStatistic(category, count);
    }

    /**
     * Increases the number of occurrences for the category by one.
     */
    public void inc() {
        this.count++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticCategoriesEntity that = (StatisticCategoriesEntity) o;
        return Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category);
    }

    @Override
    public String toString() {
        return "StatisticCategoriesEntity{" +
                "category='" + category + '\'' +
                ", count=" + count +
                '}';
    }
}
